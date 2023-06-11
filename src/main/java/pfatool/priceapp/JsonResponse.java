package pfatool.priceapp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;

/**
 * Class to hold the JSON response to send back across the API.
 */
@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
class JsonResponse {
    @JsonValue
    private final Map<String, Object> map;

    /**
     * Create a new JSON response
     * @param map The JSON response as a map object
     */
    JsonResponse(Map<String, Object> map) {
        this.map = map;
    }
}
