package pfatool.forecaster;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Dummy forecaster always returns forecast object with following values:
 * <pre>{@literal
 * mean: 9.9
 * stdDeviation: 9.9
 * startDate: 2021-01-01T00:00:00.000+00:00"
 * forecastHorizon: 7
 * category: dummy__category
 * }</pre>
 */
public class DummyForecaster implements PriceForecaster {
    @Override
    public PriceForecast makeForecast(String category) {
        return new ForecastInfo(
                9.9,
                9.9,
                OffsetDateTime.of(2021, 1, 1,
                        0, 0, 0, 0,
                        ZoneOffset.UTC),
                7,
                category
        );
    }

    /**
     * Only has a single supported category. "dummy__category"
     */
    @Override
    public boolean isSupportedCategory(String category) {
        return category.equals("dummy__category");
    }

    /**
     * Only has a single supported category. "dummy__category"
     */
    @Override
    public List<String> getSupportedCategories() {
        return List.of("dummy__category");
    }
}
