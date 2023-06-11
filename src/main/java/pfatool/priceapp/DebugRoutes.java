package pfatool.priceapp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Additional routes to aid debugging.<br>
 * Activated by including the profile `debug-routes` in `application.properties`:
 * <pre>{@literal
 *   # exposes extra routes for debugging
 *   # should be commented out for production
 *   spring.profiles.active=debug-routes
 * }</pre>
 */
@RestController
@Profile("debug-routes")
@RequestMapping("/api/__debug")
public class DebugRoutes {

    private final Log logger = LogFactory.getLog(DebugRoutes.class);

    /**
     * Debugging helper to show classpath resources. Responds with an indented list
     * of resources on the classpath.
     */
    @GetMapping("/classpath")
    public ResponseEntity<String> showClasspath() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(new DebuggingUtils().printClassPathResources());
    }

    /**
     * Deliberately throw an exception for testing.
     * Will throw MissingServletRequestParameterException.
     */
    @GetMapping("/missing")
    public void missingError(@RequestParam String x) {}

    /**
     * Deliberately throw an exception for testing.
     * Will throw BasePriceAppException.
     */
    @GetMapping("/baseException")
    public void throwBaseException() {
        logger.error("deliberate BasePriceAppException");
        throw new BasePriceAppException(
                "deliberate exception",
                new JsonResponse(Map.of("dummy", "from debug route")),
                HttpStatus.BAD_REQUEST
        );
    }

}
