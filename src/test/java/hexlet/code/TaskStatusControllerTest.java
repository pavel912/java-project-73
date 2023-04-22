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
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TaskStatusControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private long taskStatusId;

    private String token;

    @BeforeEach
    public void createUserAndTaskStatus() throws Exception {
        mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtils.readJsonFromFile(TestUtils.USERS_PATH + "/JohnSmith.json"))
                )
                .andReturn()
                .getResponse();


        MockHttpServletResponse loginResponse = mockMvc
                .perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.readJsonFromFile(TestUtils.USERS_LOGIN_PATH + "/JohnSmith.json"))
                )
                .andReturn()
                .getResponse();

        token = loginResponse.getContentAsString();

        MockHttpServletResponse createTaskStatusResponse = mockMvc
                .perform(post("/api/statuses")
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.readJsonFromFile(TestUtils.TASK_STATUS_PATH + "/New.json"))
                )
                .andReturn()
                .getResponse();

        assertThat(createTaskStatusResponse.getStatus()).isEqualTo(201);

        taskStatusId = TestUtils.parseIdFromResponse(createTaskStatusResponse.getContentAsString());
    }

    @Test
    void testGetTaskStatus() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/api/statuses/" + taskStatusId))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).contains("New");
    }

    @Test
    void testGetAllTaskStatuses() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/api/statuses"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).contains("New");
    }

    @Test
    void testUpdateTaskStatus() throws Exception {
        MockHttpServletResponse updateResponse = mockMvc
                .perform(put("/api/statuses/" + taskStatusId)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.readJsonFromFile(TestUtils.TASK_STATUS_PATH + "/Old.json"))
                )
                .andReturn()
                .getResponse();

        assertThat(updateResponse.getStatus()).isEqualTo(200);

        MockHttpServletResponse response = mockMvc
                .perform(get("/api/statuses"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).contains("Old");
    }

    @Test
    void testDeleteTaskStatus() throws Exception {
        MockHttpServletResponse deleteResponse = mockMvc
                .perform(delete("/api/statuses/" + taskStatusId)
                        .header(AUTHORIZATION, token))
                .andReturn()
                .getResponse();

        assertThat(deleteResponse.getStatus()).isEqualTo(200);

        MockHttpServletResponse response = mockMvc
                .perform(get("/api/statuses"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).doesNotContain("New");
    }
}
