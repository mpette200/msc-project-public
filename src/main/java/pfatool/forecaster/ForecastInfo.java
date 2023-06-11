package pfatool.forecaster;

import java.time.OffsetDateTime;

/**
 * Implementation of PriceForecast interface using an immutable record.
 *
 * @param mean
 * @param stdDeviation
 * @param startDate
 * @param forecastHorizon
 * @param category
 */
public record ForecastInfo(
        double mean,
        double stdDeviation,
        OffsetDateTime startDate,
        int forecastHorizon,
        String category
) implements PriceForecast {}
