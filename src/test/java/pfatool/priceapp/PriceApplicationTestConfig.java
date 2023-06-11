package pfatool.priceapp;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import pfatool.forecaster.DummyForecaster;
import pfatool.forecaster.PriceForecaster;

@TestConfiguration
public class PriceApplicationTestConfig {

    @Bean
    @Order(100)
    public PriceForecaster priceForecaster() {
        return new DummyForecaster();
    }

}
