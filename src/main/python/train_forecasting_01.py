from preprocess import \
        load_data, merge_lags_and_y_values, resample_timestamps, save_data, add_category_column, create_lags, \
        make_y_values, filter_count_above_threshold, cat_remover, \
        save_text

from sklearn.tree import DecisionTreeRegressor
from sklearn2pfa import convert, utils

INPUT_FILE = 'data/latest/eu-west-1_2022-06-07T10_14_24.xz'
OUTPUT_PFA0 = 'src/main/resources/pfa/p_01/forecasting_01_y0.pfa'
OUTPUT_PFA1 = 'src/main/resources/pfa/p_01/forecasting_01_y1.pfa'
OUTPUT_SUPPORTED_CATEGORIES = 'src/main/resources/pfa/p_01/supported_01.txt'
NUM_LAGS = 42
MIN_SAMPLES = 300

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

    print('fitting decision tree for means', flush=True)
    dt0 = DecisionTreeRegressor(
        max_leaf_nodes=100,
        random_state=666
    )
    dt0.fit(df[lag_cols], df[['growth']])

    print('fitting decision tree for standard deviations', flush=True)
    dt1 = DecisionTreeRegressor(
        max_leaf_nodes=100,
        random_state=666
    )
    dt1.fit(df[lag_cols], df[['spread']])

    print(f'writing output {OUTPUT_PFA0}', flush=True)
    pfa0 = convert.to_pfa(dt0).to_json_str()
    pfa0 = utils.pretty_json(pfa0)
    save_text(OUTPUT_PFA0, pfa0)

    print(f'writing output {OUTPUT_PFA1}', flush=True)
    pfa1 = convert.to_pfa(dt1).to_json_str()
    pfa1 = utils.pretty_json(pfa1)
    save_text(OUTPUT_PFA1, pfa1)

    print(f'writing supported categories {OUTPUT_SUPPORTED_CATEGORIES}', flush=True)
    supported = '\n'.join(supported_categories)
    save_text(OUTPUT_SUPPORTED_CATEGORIES, supported)
