package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class TestUtils {
    public static long parseIdFromResponse(String responseAsString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return Long
                .parseLong(
                        objectMapper
                                .readValue(
                                        responseAsString,
                                        new TypeReference<
                                                Map<String, Object>
                                                >() { }
                                ).get("id").toString());
    }
}
