from preprocess import \
        load_data, save_data, process_data, create_lags, \
        make_y_values, filter_by_category, cat_remover, \
        save_text

from sklearn.tree import DecisionTreeRegressor
from sklearn2pfa import convert, utils

INPUT_FILE = 'data/latest/price_sample_01.tsv'
OUTPUT_RESAMPLED = 'src/test/resources/pfa/sample_01/price_sample_01_res.tsv'
OUTPUT_LAGS = 'src/test/resources/pfa/sample_01/price_sample_01_pp.tsv'
OUTPUT_Y = 'src/test/resources/pfa/sample_01/price_sample_01_y.tsv'
OUTPUT_PFA0 = 'src/test/resources/pfa/sample_01/price_sample_01_y0.pfa'
OUTPUT_PFA1 = 'src/test/resources/pfa/sample_01/price_sample_01_y1.pfa'
NUM_LAGS = 42
MY_CATEGORY = 'g4ad.2xlarge__Linux/UNIX__eu-west-1b'

if __name__ == '__main__':
    df = load_data(INPUT_FILE)
    df0 = process_data(df)
    save_data(OUTPUT_RESAMPLED, df0)
    
    df_lag = create_lags(df0, NUM_LAGS)
    df_lag = filter_by_category(df_lag, MY_CATEGORY)
    save_data(OUTPUT_LAGS, df_lag)
    
    df_y = make_y_values(df0, NUM_LAGS)
    df_y = filter_by_category(df_y, MY_CATEGORY)
    save_data(OUTPUT_Y, df_y)

    n_rows = df_y.shape[0]
    df_x = df_lag.iloc[:n_rows].rename(columns=cat_remover)
    y0 = df_y[[MY_CATEGORY + '__growth']]
    y1 = df_y[[MY_CATEGORY + '__spread']]

    dt0 = DecisionTreeRegressor(random_state=666)
    dt0.fit(df_x, y0)
    pfa0 = convert.to_pfa(dt0).to_json_str()
    pfa0 = utils.pretty_json(pfa0)
    save_text(OUTPUT_PFA0, pfa0)

    dt1 = DecisionTreeRegressor(random_state=666)
    dt1.fit(df_x, y1)
    pfa1 = convert.to_pfa(dt1).to_json_str()
    pfa1 = utils.pretty_json(pfa1)
    save_text(OUTPUT_PFA1, pfa1)
