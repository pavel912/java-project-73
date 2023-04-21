package hexlet.code;

import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private long userId;

    private long taskStatusId;

    private long taskId;

    private String token;

    private long labelId;

    @BeforeEach
    public void createUserAndTask() throws Exception {
        MockHttpServletResponse createUserResponse = mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtils.readJsonFromFile(TestUtils.USERS_PATH + "/JohnSmith.json"))
                )
                .andReturn()
                .getResponse();

        userId = TestUtils.parseIdFromResponse(createUserResponse.getContentAsString());


        MockHttpServletResponse loginResponse = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.readJsonFromFile(TestUtils.USERS_LOGIN_PATH + "/JohnSmith.json"))
                )
                .andReturn()
                .getResponse();

        token = loginResponse.getContentAsString();

        MockHttpServletResponse createTaskStatusResponse = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/statuses")
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.readJsonFromFile(TestUtils.TASK_STATUS_PATH + "/New.json"))
                )
                .andReturn()
                .getResponse();

        assertThat(createTaskStatusResponse.getStatus()).isEqualTo(200);

        taskStatusId = TestUtils.parseIdFromResponse(createTaskStatusResponse.getContentAsString());

        MockHttpServletResponse createLabelResponse = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/labels")
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.readJsonFromFile(TestUtils.LABELS_PATH + "/Bug.json"))
                )
                .andReturn()
                .getResponse();

        assertThat(createLabelResponse.getStatus()).isEqualTo(200);

        labelId = TestUtils.parseIdFromResponse(createLabelResponse.getContentAsString());

        MockHttpServletResponse createTaskResponse = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/tasks")
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils
                                .buildTaskJsonFromFile(TestUtils.TASKS_PATH + "/CreateNewFeature.json",
                                        taskStatusId,
                                        userId,
                                        labelId))
                )
                .andReturn()
                .getResponse();

        assertThat(createTaskStatusResponse.getStatus()).isEqualTo(200);

        taskId = TestUtils.parseIdFromResponse(createTaskResponse.getContentAsString());
    }

    @Test
    void testGetTasks() throws Exception {
        MockHttpServletResponse getAllResponse = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/tasks"))
                .andReturn()
                .getResponse();

        assertThat(getAllResponse.getStatus()).isEqualTo(200);
        assertThat(getAllResponse.getContentAsString()).contains("Create new feature");

        MockHttpServletResponse getWithFilterResponse = mockMvc
                .perform(MockMvcRequestBuilders.get(
                        String.format("/api/tasks?taskStatus=%d&executorId=%d&labels=%d&authorId=%d",
                                taskStatusId, userId, labelId, userId)))
                .andReturn()
                .getResponse();

        assertThat(getWithFilterResponse.getStatus()).isEqualTo(200);
        assertThat(getWithFilterResponse.getContentAsString()).contains("Create new feature");


        MockHttpServletResponse getWithFilterEmptyResponse = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/tasks?authorId=10000000"))
                .andReturn()
                .getResponse();

        assertThat(getWithFilterEmptyResponse.getStatus()).isEqualTo(200);
        assertThat(getWithFilterEmptyResponse.getContentAsString()).doesNotContain("Create new feature");
    }

    @Test
    void testGetTask() throws Exception {
        MockHttpServletResponse getResponse = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/tasks/" + taskId))
                .andReturn()
                .getResponse();

        assertThat(getResponse.getStatus()).isEqualTo(200);
        assertThat(getResponse.getContentAsString()).contains("Create new feature");
    }

    @Test
    void testUpdateTask() throws Exception {
        MockHttpServletResponse putResponse = mockMvc
                .perform(MockMvcRequestBuilders.put("/api/tasks/" + taskId)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils
                                .buildTaskJsonFromFile(TestUtils.TASKS_PATH + "/AddNewFeature.json",
                                        taskStatusId,
                                        userId,
                                        labelId))
                )
                .andReturn()
                .getResponse();

        assertThat(putResponse.getStatus()).isEqualTo(200);

        MockHttpServletResponse getResponse = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/tasks"))
                .andReturn()
                .getResponse();

        assertThat(getResponse.getStatus()).isEqualTo(200);
        assertThat(getResponse.getContentAsString()).contains("Please add authorization");
    }

    @Test
    void testDeleteTask() throws Exception {
        MockHttpServletResponse deleteResponse = mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/tasks/" + taskId)
                        .header(AUTHORIZATION, token))
                .andReturn()
                .getResponse();

        assertThat(deleteResponse.getStatus()).isEqualTo(200);

        MockHttpServletResponse getTasksResponse = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/tasks"))
                .andReturn()
                .getResponse();

        assertThat(getTasksResponse.getStatus()).isEqualTo(200);
        assertThat(getTasksResponse.getContentAsString()).doesNotContain("Create new feature");


        // Check availability of related entities
        MockHttpServletResponse getUserResponse = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/users/" + userId)
                        .header(AUTHORIZATION, token))
                .andReturn()
                .getResponse();

        assertThat(getUserResponse.getStatus()).isEqualTo(200);


        MockHttpServletResponse getTaskStatusResponse = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/statuses/" + taskStatusId)
                        .header(AUTHORIZATION, token))
                .andReturn()
                .getResponse();

        assertThat(getTaskStatusResponse.getStatus()).isEqualTo(200);

        MockHttpServletResponse getLabelResponse = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/labels/" + labelId)
                        .header(AUTHORIZATION, token))
                .andReturn()
                .getResponse();

        assertThat(getLabelResponse.getStatus()).isEqualTo(200);
    }

    @Test
    void testUpdateTaskWithoutLogin() throws Exception {
        MockHttpServletResponse putResponse = mockMvc
                .perform(MockMvcRequestBuilders.put("/api/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils
                                .buildTaskJsonFromFile(TestUtils.TASKS_PATH + "/AddNewFeature.json",
                                        taskStatusId,
                                        userId,
                                        labelId))
                )
                .andReturn()
                .getResponse();

        assertThat(putResponse.getStatus()).isEqualTo(403);
    }

    @Test
    void testDeleteOtherUsersTask() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtils.readJsonFromFile(TestUtils.USERS_PATH + "/JackDoe.json"))
                )
                .andReturn()
                .getResponse();

        MockHttpServletResponse loginResponse = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.readJsonFromFile(TestUtils.USERS_LOGIN_PATH + "/JackDoe.json"))
                )
                .andReturn()
                .getResponse();

        String newToken = loginResponse.getContentAsString();

        MockHttpServletResponse deleteResponse = mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/tasks/" + taskId)
                        .header(AUTHORIZATION, newToken))
                .andReturn()
                .getResponse();

        assertThat(deleteResponse.getStatus()).isEqualTo(403);
    }

    @Test
    void testDeleteUserAssociatedWithTask() throws Exception {
        MockHttpServletResponse deleteResponse = mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/users/" + userId)
                        .header(AUTHORIZATION, token))
                .andReturn()
                .getResponse();

        assertThat(deleteResponse.getStatus()).isEqualTo(422);
    }

    @Test
    void testDeleteTaskStatusAssociatedWithTask() throws Exception {
        MockHttpServletResponse deleteResponse = mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/statuses/" + taskStatusId)
                        .header(AUTHORIZATION, token))
                .andReturn()
                .getResponse();

        assertThat(deleteResponse.getStatus()).isEqualTo(422);
    }

    @Test
    void testDeleteLabelAssociatedWithTask() throws Exception {
        MockHttpServletResponse deleteResponse = mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/labels/" + labelId)
                        .header(AUTHORIZATION, token))
                .andReturn()
                .getResponse();

        assertThat(deleteResponse.getStatus()).isEqualTo(422);
    }

    @Test
    void testDeleteOldTaskStatus() throws Exception {
        MockHttpServletResponse createTaskStatusResponse = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/statuses")
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.readJsonFromFile(TestUtils.TASK_STATUS_PATH + "/Old.json"))
                )
                .andReturn()
                .getResponse();

        assertThat(createTaskStatusResponse.getStatus()).isEqualTo(200);

        long newTaskStatusId = TestUtils.parseIdFromResponse(createTaskStatusResponse.getContentAsString());

        MockHttpServletResponse putResponse = mockMvc
                .perform(MockMvcRequestBuilders.put("/api/tasks/" + taskId)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils
                                .buildTaskJsonFromFile(TestUtils.TASKS_PATH + "/AddNewFeature.json",
                                        newTaskStatusId,
                                        userId,
                                        labelId))
                )
                .andReturn()
                .getResponse();

        assertThat(putResponse.getStatus()).isEqualTo(200);

        MockHttpServletResponse getResponse = mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/statuses/" + taskStatusId)
                        .header(AUTHORIZATION, token))
                .andReturn()
                .getResponse();

        assertThat(getResponse.getStatus()).isEqualTo(200);
    }
}
