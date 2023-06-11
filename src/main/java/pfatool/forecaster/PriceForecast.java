package pfatool.forecaster;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;

import java.time.OffsetDateTime;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(
        creatorVisibility = Visibility.NONE,
        fieldVisibility = Visibility.NONE,
        getterVisibility = Visibility.NONE,
        isGetterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE
)
public interface PriceForecast {
    /**
     * Gets the predicted mean price over the forecast horizon
     * @return mean price
     */
    @JsonGetter
    double mean();

    /**
     * Gets the predicted standard deviation of price over the forecast horizon
     * @return standard deviation of price
     */
    @JsonGetter
    double stdDeviation();

    /**
     * Gets the start date which is the date on which the forecast was made
     * @return start date of the forecast
     */
    @JsonGetter
    OffsetDateTime startDate();

    /**
     * Gets the forecast horizon in number of days
     * @return number of days ahead for which the forecast applies
     */
    @JsonGetter("forecastHorizonDays")
    int forecastHorizon();

    /**
     * Gets the category for which the forecast was made.
     * @return category name
     */
    @JsonGetter
    public String category();

}
