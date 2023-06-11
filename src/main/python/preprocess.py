from collections import deque
from functools import reduce
import numpy as np
import operator
import pandas as pd # type: ignore
import pathlib
import sys
from typing import Deque, Set

# can override the root path if necessary
# if None, automatically search for folder containing 'src'
ROOT_OVERRIDE = None


def find_root_path(start_dir: str,
                   search_str: str) -> str:
    '''
    Keep moving up one parent at a time, until the search str is
    found in the directory listing.
    '''
    path = pathlib.Path(start_dir).resolve()
    found = None
    while path.name != '':
        found = any(search_str in x.name for x in path.iterdir())
        if found:
            break
        path = path.parent
    if not found:
        raise LookupError(f'{search_str} not found in any parent of {start_dir}')
    return str(path)


def join_paths(*args: str) -> str:
    '''
    Wrapper around pathlibs joinpath. For example
    join_paths('a/b', 'c/d') -> 'a/b/c/d'
    '''
    path = pathlib.Path().joinpath(*args)
    return str(path)


def make_fullname(filename: str) -> str:
    if ROOT_OVERRIDE is None:
        root_path = find_root_path(sys.path[0], 'src')
    else:
        root_path = ROOT_OVERRIDE
    return join_paths(root_path, filename)


def load_data(filename: str) -> pd.DataFrame:
    '''
    Load data from given filename. Filename should be given relative
    to root of code repository. Example:
    load_data('data/latest/eu-west-1_2022-06-07T10_14_24.xz')
    File, which may be zip compressed, must contain tab separated fields:
    ['omit', 'price', 'date', 'instance_type', 'instance_description', 'region_zone']
    '''
    full_name = make_fullname(filename)
    return pd.read_csv(
        full_name,
        sep='\t',
        header=None,
        names=['omit', 'price', 'date', 'instance_type', 'instance_description', 'region_zone'],
        index_col='date',
        usecols=['price', 'date', 'instance_type', 'instance_description', 'region_zone'],
        parse_dates=['date']
    )


def save_data(filename: str,
              data: pd.DataFrame) -> None:
    '''
    Save data as tab-delimited csv file.
    '''
    full_name = make_fullname(filename)
    data.to_csv(
        full_name,
        sep='\t',
        line_terminator='\n',
        float_format='%.04f'
    )


def save_text(filename: str,
              text: str) -> None:
    '''
    Save text data to file
    '''
    full_name = make_fullname(filename)
    with open(full_name, mode='wt') as fp:
        fp.write(text)


def add_category_column(df: pd.DataFrame) -> pd.DataFrame:
    '''
    Adds category column to the data. Data must contain columns:
    ['instance_type', 'instance_description', 'region_zone']
    Category is defined as double-underscore separated names.
    For example, if the data is:
        ['c5n.2xlarge', 'SUSE Linux', 'eu-west-1b']
    the category is:
        'c5n.2xlarge__SUSE Linux__eu-west-1b'
    '''
    delim = '__'
    categorical_columns = ['instance_type', 'instance_description', 'region_zone']
    category_names = reduce(
        lambda a, b: a + delim + df[b],
        categorical_columns[1:],
        df[categorical_columns[0]]
    )
    category_names.name = 'category'
    return pd.concat([df, category_names], axis=1)


def filter_count_above_threshold(df: pd.DataFrame,
                        threshold: int) -> pd.DataFrame:
    '''
    Keeps only the categories that have a row count above the
    threshold.
    '''
    # get categories above the threshold
    category_names = df['category']
    counts = category_names.value_counts()
    above_threshold = counts[counts >= threshold]
    above_threshold_set = set(above_threshold.index)
    
    # then filter on the categories
    contains = np.vectorize(operator.contains)
    return df[contains(above_threshold_set, category_names)].copy()


def resample_timestamps(df: pd.DataFrame) -> pd.DataFrame:
    '''
    Resample the time series to fixed intervals, and create a column for every
    category.
    '''
    first_date = df.index.min()
    start_date = pd.Timestamp(
        year=first_date.year,
        month=first_date.month,
        day=first_date.day,
        tzinfo=first_date.tzinfo
    )
    df0 = df.sort_index()
    df0 = df0.pivot(columns='category', values='price') \
        .fillna(method='ffill') \
        .resample('4h', origin=start_date) \
        .ffill() \
        .dropna(axis=0)
    df0.columns = (x + '__price' for x in df0.columns)
    return df0


def create_lags(df: pd.DataFrame,
                num_lags: int) -> pd.DataFrame:
    '''
    Add lag columns for each price. Create a new column for each lag.
    The lag is the ratio of price / previous_price.
    '''

    lag_names = [
        '{}__lag_{:02d}'.format(cat_name, i)
        for i in range(num_lags)
        for cat_name in df.columns
    ]

    lags: Deque[pd.Series]
    lags = deque()
    rows = []
    
    # initialize first group of lags
    for i in range(1, 1 + num_lags):
        lags.append(df.iloc[i] / df.iloc[i-1])
    data = pd.concat(lags, axis=0)
    rows.append(data)

    # add remaining lags
    for i in range(1 + num_lags, len(df)):
        lags.popleft()
        lags.append(df.iloc[i] / df.iloc[i-1])
        data = pd.concat(lags, axis=0)
        rows.append(data)
    df0 = pd.concat(rows, axis=1).T
    df0.columns = lag_names
    df0.index = df.index[num_lags:]
    return df0


def make_y_values(df: pd.DataFrame,
                  num_lags: int) -> pd.DataFrame:
    '''
    Create y_values. The y_value is the dependant variable for fitting the decision tree.
    The y_growth is the ratio of rolling-mean / previous rolling-mean.
    The y_spread is the standard deviation.
    '''
    # ratio of means
    rolling_mean = df.rolling(1 + num_lags).mean()
    y_growth = rolling_mean.shift(-1 - num_lags) / rolling_mean
    y_growth.columns = (x.replace('__price', '__growth') for x in y_growth.columns)
    # standard deviation
    y_spread = df.rolling(1 + num_lags) \
        .std() \
        .shift(-1 - num_lags)
    y_spread.columns = (x.replace('__price', '__spread') for x in y_spread.columns)
    return pd.concat([y_growth, y_spread], axis=1).dropna()


def cat_remover(col_name: str) -> str:
    '''
    Rename string so it only contains the last segment after double underscore.
    Example: c1.xlarge__Linux/UNIX__eu-west-1a__growth
                ->  growth
             c1.xlarge__Linux/UNIX__eu-west-1a__price__lag_00
                ->  lag_00
    '''
    i = col_name.rindex('__')
    return col_name[i + 2:]


def merge_lags_and_y_values(df_lags: pd.DataFrame,
                            df_y: pd.DataFrame) -> pd.DataFrame:
    '''
    Put the lags and y_values into a single dataframe. Sort by column name,
    then unpivot the lags
    '''
    n_lags = df_lags.shape[1] // (df_y.shape[1] // 2)
    df0 = pd.concat([df_lags, df_y], axis=1)
    df0 = df0[sorted(df0.columns)].dropna()
    df0.rename(columns=cat_remover, inplace=True)
    rows = []
    for i in range(0, df0.shape[1], n_lags + 2):
        rows.append(df0.iloc[:, i:i + n_lags + 2])
    return pd.concat(rows)


def filter_by_category(df: pd.DataFrame,
                       category: str) -> pd.DataFrame:
    '''
    Include only columns that have the specific category
    '''
    cols = (x for x in df.columns if category in x)
    return df[cols].copy()


def process_data(df: pd.DataFrame) -> pd.DataFrame:
    '''
    Common preprocessing. Add category column, remove timezone offset,
    include only counts above threshold and resample time intervals.
    '''
    df0 = add_category_column(df)
    df0 = df0.tz_convert(None)
    df0 = resample_timestamps(df0)
    return df0


if __name__ == '__main__':
    df = load_data('data/latest/price_sample_01.tsv')
    print(df)
    df2 = process_data(df)
    print(df2)
    df3 = create_lags(df2, num_lags=42)
    print(df3)    
    df4 = make_y_values(df2, num_lags=42)
    print(df4)
    df5 = merge_lags_and_y_values(df3, df4)
    print(df5)
