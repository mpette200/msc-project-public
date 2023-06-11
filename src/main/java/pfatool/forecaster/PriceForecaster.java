package pfatool.forecaster;

import java.util.List;

public interface PriceForecaster {

    /**
     * Makes a forecast for the given category.
     * @param category The category of the forecast.
     * @return PriceForecast object containing the forecast information
     */
    PriceForecast makeForecast(String category);

    /**
     * Checks if a category is supported by the forecaster
     * @param category The category of the forecast.
     * @return boolean
     */
    boolean isSupportedCategory(String category);

    /**
     * Lists the categories supported by the forecaster.
     * @return List of categories
     */
    List<String> getSupportedCategories();

}
