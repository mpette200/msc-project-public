package pfatool.priceapp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Holds configuration properties and associated validation of those properties
 * that are critical to the operation of the application.
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "priceapp")
@Validated
public class ConfigOptions {
    @NotNull
    public final boolean exceptionsIncludeTraceback;
    @NotBlank
    public final String forecastingTreeMean;
    @NotBlank
    public final String forecastingTreeStd;
    @NotBlank
    public final String forecastingSupportedCategories;

    /**
     * Constructor is automatically called by use of the @ConstructorBinding annotation
     */
    public ConfigOptions(
            @DefaultValue("false") boolean exceptionsIncludeTraceback,
            String forecastingTreeMean,
            String forecastingTreeStd,
            String forecastingSupportedCategories
    ) {
        this.exceptionsIncludeTraceback = exceptionsIncludeTraceback;
        this.forecastingTreeMean = forecastingTreeMean;
        this.forecastingTreeStd = forecastingTreeStd;
        this.forecastingSupportedCategories = forecastingSupportedCategories;
    }
}
