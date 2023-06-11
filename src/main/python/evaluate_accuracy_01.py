from preprocess import \
        load_data, merge_lags_and_y_values, resample_timestamps, add_category_column, create_lags, \
        make_y_values, filter_count_above_threshold
from sklearn.tree import DecisionTreeRegressor
from sklearn.dummy import DummyRegressor
from sklearn import metrics

INPUT_FILE = 'data/latest/eu-west-1_2022-06-07T10_14_24.xz'
NUM_LAGS = 42
MIN_SAMPLES = 300
ACCURACY_METRICS = [
    ('mean absolute error', metrics.mean_absolute_error),
    ('mean absolute percentage error', metrics.mean_absolute_percentage_error),
    ('mean squared error', metrics.mean_squared_error)
]

def split_df(df, ratio):
    '''
    Split dataframe using the ratio. But make sure the split aligns with a change in timestamp.
    '''
    i = int(len(df) * ratio)
    datetime_index = df.index
    timestamp = datetime_index[i]
    while datetime_index[i] <= timestamp:
        # move the split forward until a change in timestamp
        i += 1
    return df.iloc[:i], df.iloc[i:]


def fit_estimator(estimator, df_train, lag_cols):
    estimator.fit(df_train[lag_cols], df_train[['growth']])


def compute_error(estimator, error_func, df_test, lag_cols):
    y_pred = estimator.predict(df_test[lag_cols])
    return error_func(df_test[['growth']], y_pred)


if __name__ == '__main__':
    print(f'loading: {INPUT_FILE}', flush=True)
    df = load_data(INPUT_FILE)

    print(f'using min sample count: {MIN_SAMPLES}', flush=True)
    df = add_category_column(df)
    df = filter_count_above_threshold(df, MIN_SAMPLES)
    supported_categories = df['category'].unique()

    print('resampling', flush=True)
    df = df.tz_convert(None)
    df = resample_timestamps(df)

    print(f'making lags: {NUM_LAGS}', flush=True)
    df_lag = create_lags(df, NUM_LAGS)

    print(f'computing means and standard deviations', flush=True)
    df_y = make_y_values(df, NUM_LAGS)
    df = merge_lags_and_y_values(df_lag, df_y)

    lag_cols = [x for x in df.columns if 'lag' in x]

    print(f'split into training set and test set', flush=True)
    df.sort_index(inplace=True)
    df_train, df_test = split_df(df, ratio=0.7)

    print('fitting decision tree for means', flush=True)
    dt0 = DecisionTreeRegressor(
        max_leaf_nodes=100,
        random_state=666
    )
    fit_estimator(dt0, df_train, lag_cols)
    for name, err_func in ACCURACY_METRICS:
        tree_error = compute_error(dt0, err_func, df_test, lag_cols)
        print(f'decision tree, {name} = {tree_error}', flush=True)

    print('comparing dummy estimator that is just the mean of inputs', flush=True)
    dummy = DummyRegressor(strategy='mean')
    fit_estimator(dummy, df_train, lag_cols)
    for name, err_func in ACCURACY_METRICS:
        dummy_error = compute_error(dummy, err_func, df_test, lag_cols)
        print(f'dummy estimator, {name} = {dummy_error}', flush=True)
