package pfatool.forecaster;

import app.ec2prices.LiveSpotPrices;
import pfatool.generated.PfaDecisionTree;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Implementation of PriceForecaster that:<pre>{@literal
 *     1. Fetches previous historical prices from Amazon AWS.
 *     2. Makes forecast using the PfaDecisionTree implementation.
 *     3. Returns the forecasted prices.
 * }</pre>
 *
 */
public class ForecasterImpl implements PriceForecaster {

    public static final int HORIZON_DAYS = 7;
    public static final int FREQ_HOURS = 4;
    public static final int NUM_LAGS = 42;
    private final PfaDecisionTree treeMeans;
    private final PfaDecisionTree treeStdDev;
    private final SupportedCategories supportedCategories;
    private final LiveSpotPrices liveSpotPrices;

    /**
     * Construct a new forecaster implementation
     * @param treeMeans - Decision tree structure for predicting the future mean.
     * @param treeStdDev - Decision tree structure for predicting the future standard deviation.
     * @param supportedCategories - List of all supported categories.
     * @param liveSpotPrices - Instance from which to fetch historical prices.
     */
    public ForecasterImpl(PfaDecisionTree treeMeans,
                          PfaDecisionTree treeStdDev,
                          SupportedCategories supportedCategories, LiveSpotPrices liveSpotPrices) {
        this.treeMeans = treeMeans;
        this.treeStdDev = treeStdDev;
        this.supportedCategories = supportedCategories;
        this.liveSpotPrices = liveSpotPrices;
    }

    /**
     * Make a forecast using the decision tree implementation.
     *
     * @param category The category of the forecast.
     * @return PriceForecast containing the forecast information.
     */
    @Override
    public PriceForecast makeForecast(String category) {
        List<PriceData> input = fetchFromAws(category);
        PreProcessor pp = new PreProcessor(
                input,
                FREQ_HOURS,
                NUM_LAGS
        );
        PriceFrame f = pp.makeLags(category);
        double[] lags = f.getValues(f.getNumRows() - 1);
        double prevMean = pp.getRollingMean(category);
        double meanRatio = treeMeans.predict(lags);
        double std = treeStdDev.predict(lags);
        return new ForecastInfo(
                meanRatio * prevMean,
                std,
                OffsetDateTime.now(Clock.systemUTC()),
                HORIZON_DAYS,
                category
        );
    }

    @Override
    public boolean isSupportedCategory(String category) {
        return supportedCategories.getCategories().contains(category);
    }

    @Override
    public List<String> getSupportedCategories() {
        return supportedCategories.getCategories();
    }

    private List<PriceData> fetchFromAws(String category) {
        return liveSpotPrices.fetchPriceHistory(category, 1 + HORIZON_DAYS);
    }

}
