package pfatool.priceapp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * Controller class to catch exceptions and configure what is displayed to users
 */
@ControllerAdvice(assignableTypes = {WebApi.class, DebugRoutes.class})
public class PriceAppExceptions {
    private final Log logger = LogFactory.getLog(PriceAppExceptions.class);
    private final ConfigOptions configOptions;

    @Autowired
    public PriceAppExceptions(ConfigOptions configOptions) {
        this.configOptions = configOptions;
    }

    /**
     * BasePriceAppException is generally some anticipated form of bad user input.
     * Info is displayed back to user.
     */
    @ExceptionHandler({BasePriceAppException.class})
    public ResponseEntity<JsonResponse> handleBase(BasePriceAppException ex) {
        logger.error(ex.getMessage());
        JsonResponse response = new JsonResponse(Map.of(
                "error", ex.getJsonResponse()
        ));
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(response);
    }

    /**
     * If some unknown exception occurs decide whether to show traceback info or not
     * depending on value of configOptions.exceptionsIncludeTraceback property
     */
    @ExceptionHandler
    public ResponseEntity<JsonResponse> handleUnknown(Exception ex) {
        logger.error(ex.getMessage(), ex);
        JsonResponse response;
        if (configOptions.exceptionsIncludeTraceback) {
            // include full exception as is
            response = new JsonResponse(Map.of(
                    "errorClass", ex.getClass(),
                    "error", ex
            ));
        } else {
            // otherwise include simple message
            response = new JsonResponse(Map.of(
                    "error", "internal error"
            ));
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
