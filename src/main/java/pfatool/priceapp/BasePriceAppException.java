package pfatool.priceapp;

import org.springframework.http.HttpStatus;

/**
 * Exception class that includes a JsonResponse and a http status code
 * to use for the response.
 */
public class BasePriceAppException extends RuntimeException {
    private final HttpStatus httpStatus;

    // exceptions are supposed to be serializable so transient modifier prevents warnings
    private final transient JsonResponse jsonResponse;

    /**
     * Create a new exception with the given message, JSON response and
     * status code. The intention is that these details are displayed in the
     * response to the user.
     *
     * @param message exception message
     * @param jsonResponse JSON response to display to user
     * @param httpStatus HTTP status code to use for response
     */
    public BasePriceAppException(String message,
                                 JsonResponse jsonResponse,
                                 HttpStatus httpStatus) {
        super(message);
        this.jsonResponse = jsonResponse;
        this.httpStatus = httpStatus;
    }

    /**
     * Get the HTTP status code
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    /**
     * Get the JSON response object
     */
    public JsonResponse getJsonResponse() {
        return jsonResponse;
    }
}
