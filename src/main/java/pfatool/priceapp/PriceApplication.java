package pfatool.priceapp;

import app.ec2prices.LiveSpotPrices;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import pfatool.forecaster.ForecasterImpl;
import pfatool.forecaster.PriceForecaster;
import pfatool.forecaster.SupportedCategories;
import pfatool.generated.PfaDecisionTree;

import java.io.IOException;

@SpringBootApplication
@EnableConfigurationProperties(ConfigOptions.class)
public class PriceApplication {

    /**
     * Provide an instance of MarkdownRenderer and configure it with the template information.
     */
    @Bean
    public MarkdownRenderer markdownRenderer(ResourceReader resourceReader) throws IOException {
        return new MarkdownFromTemplate(
                resourceReader.readResource("classpath:templates/markdown_template.html"),
                "{{ markdown-html }}"
        );
    }

    /**
     * Provide a PriceForecaster implementation and configure it with the filenames containing
     * the decision tree data, the supported categories, and provide a fetcher for the historical
     * price data.
     */
    @Bean
    public PriceForecaster priceForecaster(ConfigOptions configOptions) {
        return new ForecasterImpl(
                new PfaDecisionTree(configOptions.forecastingTreeMean),
                new PfaDecisionTree(configOptions.forecastingTreeStd),
                new SupportedCategories(configOptions.forecastingSupportedCategories),
                new LiveSpotPrices());
    }

    public static void main(String[] args) {
        SpringApplication.run(PriceApplication.class, args);
    }

}
