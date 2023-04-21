package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;

public class TestUtils {
    public static final String USERS_PATH = Paths
            .get("src", "test", "resources", "data", "users")
            .toAbsolutePath()
            .normalize()
            .toString();

    public static final String USERS_LOGIN_PATH = Paths
            .get("src", "test", "resources", "data", "users", "login")
            .toAbsolutePath()
            .normalize()
            .toString();

    public static final String TASK_STATUS_PATH = Paths
            .get("src", "test", "resources", "data", "taskStatus")
            .toAbsolutePath()
            .normalize()
            .toString();

    public static final String LABELS_PATH = Paths
            .get("src", "test", "resources", "data", "labels")
            .toAbsolutePath()
            .normalize()
            .toString();

    public static final String TASKS_PATH = Paths
            .get("src", "test", "resources", "data", "tasks")
            .toAbsolutePath()
            .normalize()
            .toString();

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

    public static String readJsonFromFile(String filePath) throws IOException {
        return FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
    }

    public static String buildTaskJsonFromFile(
            String filePath,
            long taskStatusId,
            long userId,
            long labelId) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> fileContent = objectMapper
                .readValue(
                        new File(filePath),
                        new TypeReference<Map<String, Object>>() { });

        fileContent.put("taskStatusId", taskStatusId);
        fileContent.put("executorId", userId);
        fileContent.put("labelIds", new long[] {labelId});

        return objectMapper.writeValueAsString(fileContent);
    }
}
