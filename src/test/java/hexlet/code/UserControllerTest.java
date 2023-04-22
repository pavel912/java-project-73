package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import hexlet.code.utils.TestUtils;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private long firstUserId;
    private long secondUserId;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void createTestUsers() throws Exception {

        String fileContent = TestUtils
                .readJsonFromFile(
                        TestUtils.USERS_PATH
                                + "/JohnSmith.json");

        MockHttpServletResponse createResponseJohn = mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(fileContent)
                )
                .andReturn()
                .getResponse();

        assertThat(createResponseJohn.getStatus()).isEqualTo(201);

        firstUserId = TestUtils.parseIdFromResponse(createResponseJohn.getContentAsString());

        fileContent = TestUtils
                .readJsonFromFile(
                        TestUtils.USERS_PATH
                                + "/JackDoe.json");

        MockHttpServletResponse createResponseJack = mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(fileContent)
                )
                .andReturn()
                .getResponse();

        assertThat(createResponseJack.getStatus()).isEqualTo(201);

        secondUserId = TestUtils.parseIdFromResponse(createResponseJack.getContentAsString());
    }

    @Test
    void testGetUser() throws Exception {
        String fileContent = TestUtils
                .readJsonFromFile(
                TestUtils.USERS_PATH
                        + "/JohnSmith.json");

        MockHttpServletResponse loginResponse = mockMvc
                .perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fileContent)
                )
                .andReturn()
                .getResponse();

        assertThat(loginResponse.getStatus()).isEqualTo(200);

        String token = loginResponse.getContentAsString();

        MockHttpServletResponse response = mockMvc
                .perform(get("/api/users/" + firstUserId)
                        .header(AUTHORIZATION, token))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("John", "Smith");
    }

    @Test
    void testGetUsers() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/api/users"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("John", "Smith");
        assertThat(response.getContentAsString()).contains("Jack", "Doe");
    }

    @Test
    void testCreateUserPositive() throws Exception {
        String fileContent = TestUtils
                .readJsonFromFile(
                TestUtils.USERS_PATH
                        + "/JacksonBind.json");

        MockHttpServletResponse postResponse = mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(fileContent)
                )
                .andReturn()
                .getResponse();

        assertThat(postResponse.getStatus()).isEqualTo(201);

        MockHttpServletResponse getResponse = mockMvc
                .perform(get("/api/users"))
                .andReturn()
                .getResponse();

        assertThat(getResponse.getStatus()).isEqualTo(200);
        assertThat(getResponse.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(getResponse.getContentAsString()).contains("Jackson", "Bind");
    }

    @Test
    void testUpdateUserPositive() throws Exception {
        String fileContent = TestUtils
                .readJsonFromFile(
                TestUtils.USERS_LOGIN_PATH
                        + "/JohnSmith.json");

        MockHttpServletResponse loginResponse = mockMvc
                .perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fileContent)
                )
                .andReturn()
                .getResponse();

        assertThat(loginResponse.getStatus()).isEqualTo(200);

        String token = loginResponse.getContentAsString();

        fileContent = TestUtils
                .readJsonFromFile(
                TestUtils.USERS_PATH
                        + "/MikeSmith.json");

        MockHttpServletResponse postResponse = mockMvc
                .perform(
                        put("/api/users/" + firstUserId)
                                .header(AUTHORIZATION, token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(fileContent)
                )
                .andReturn()
                .getResponse();

        assertThat(postResponse.getStatus()).isEqualTo(200);

        MockHttpServletResponse getResponse = mockMvc
                .perform(get("/api/users"))
                .andReturn()
                .getResponse();

        assertThat(getResponse.getStatus()).isEqualTo(200);
        assertThat(getResponse.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(getResponse.getContentAsString()).contains("Mike");
        assertThat(getResponse.getContentAsString()).doesNotContain("John");
    }

    @Test
    void testDeleteUserPositive() throws Exception {
        String fileContent = TestUtils
                .readJsonFromFile(
                TestUtils.USERS_LOGIN_PATH
                        + "/JackDoe.json");

        MockHttpServletResponse loginResponse = mockMvc
                .perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fileContent)
                )
                .andReturn()
                .getResponse();

        assertThat(loginResponse.getStatus()).isEqualTo(200);

        String token = loginResponse.getContentAsString();

        MockHttpServletResponse postResponse = mockMvc
                .perform(delete("/api/users/" + secondUserId)
                        .header(AUTHORIZATION, token))
                .andReturn()
                .getResponse();

        assertThat(postResponse.getStatus()).isEqualTo(200);

        MockHttpServletResponse getResponse = mockMvc
                .perform(get("/api/users"))
                .andReturn()
                .getResponse();

        assertThat(getResponse.getStatus()).isEqualTo(200);
        assertThat(getResponse.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(getResponse.getContentAsString()).doesNotContain("Jack", "Doe");
    }

    @Test
    void testCreateUserIncorrectData() throws Exception {
        String fileContent = TestUtils
                .readJsonFromFile(
                TestUtils.USERS_PATH
                        + "/incorrectData.json");

        MockHttpServletResponse postResponse = mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(fileContent)
                )
                .andReturn()
                .getResponse();

        assertThat(postResponse.getStatus()).isEqualTo(422);

        MockHttpServletResponse getResponse = mockMvc
                .perform(get("/api/users"))
                .andReturn()
                .getResponse();

        assertThat(getResponse.getStatus()).isEqualTo(200);
        assertThat(getResponse.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(getResponse.getContentAsString()).doesNotContain("incorrect");
    }

    @Test
    void testCreateUserWithSameLogin() throws Exception {
        String fileContent = TestUtils
                .readJsonFromFile(
                TestUtils.USERS_PATH
                        + "/MikeSmith.json");

        MockHttpServletResponse postResponse = mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(fileContent)
                )
                .andReturn()
                .getResponse();

        assertThat(postResponse.getStatus()).isEqualTo(422);

        MockHttpServletResponse getResponse = mockMvc
                .perform(get("/api/users"))
                .andReturn()
                .getResponse();

        assertThat(getResponse.getStatus()).isEqualTo(200);
        assertThat(getResponse.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(getResponse.getContentAsString()).doesNotContain("Mike");
    }

    @Test
    void testUpdateUserIncorrectData() throws Exception {
        String fileContent = TestUtils
                .readJsonFromFile(
                TestUtils.USERS_LOGIN_PATH
                        + "/JohnSmith.json");

        MockHttpServletResponse loginResponse = mockMvc
                .perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fileContent)
                )
                .andReturn()
                .getResponse();

        assertThat(loginResponse.getStatus()).isEqualTo(200);

        String token = loginResponse.getContentAsString();

        fileContent = TestUtils
                .readJsonFromFile(
                TestUtils.USERS_PATH
                        + "/incorrectData.json");

        MockHttpServletResponse putResponse = mockMvc
                .perform(
                        put("/api/users/" + firstUserId)
                                .header(AUTHORIZATION, token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(fileContent)
                )
                .andReturn()
                .getResponse();

        assertThat(putResponse.getStatus()).isEqualTo(422);

        MockHttpServletResponse getResponse = mockMvc
                .perform(get("/api/users"))
                .andReturn()
                .getResponse();

        assertThat(getResponse.getStatus()).isEqualTo(200);
        assertThat(getResponse.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(getResponse.getContentAsString()).doesNotContain("incorrect");
    }

    @Test
    void testUpdateOtherUser() throws Exception {
        String fileContent = TestUtils
                .readJsonFromFile(
                TestUtils.USERS_LOGIN_PATH
                        + "/JohnSmith.json");

        MockHttpServletResponse loginResponse = mockMvc
                .perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fileContent)
                )
                .andReturn()
                .getResponse();

        assertThat(loginResponse.getStatus()).isEqualTo(200);

        String token = loginResponse.getContentAsString();

        fileContent = TestUtils
                .readJsonFromFile(
                TestUtils.USERS_PATH
                        + "/JohnSmith.json");

        MockHttpServletResponse putResponse = mockMvc
                .perform(
                        put("/api/users/" + secondUserId)
                                .header(AUTHORIZATION, token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(fileContent)
                )
                .andReturn()
                .getResponse();

        assertThat(putResponse.getStatus()).isEqualTo(403);
    }

    @Test
    void testUpdateUserWithSameLogin() throws Exception {
        String fileContent = TestUtils
                .readJsonFromFile(
                TestUtils.USERS_LOGIN_PATH
                        + "/JackDoe.json");

        MockHttpServletResponse loginResponse = mockMvc
                .perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fileContent)
                )
                .andReturn()
                .getResponse();

        assertThat(loginResponse.getStatus()).isEqualTo(200);

        String token = loginResponse.getContentAsString();

        fileContent = TestUtils
                .readJsonFromFile(
                TestUtils.USERS_PATH
                        + "/MikeSmith.json");

        MockHttpServletResponse putResponse = mockMvc
                .perform(
                        put("/api/users/" + secondUserId)
                                .header(AUTHORIZATION, token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(fileContent)
                )
                .andReturn()
                .getResponse();

        assertThat(putResponse.getStatus()).isEqualTo(422);

        MockHttpServletResponse getResponse = mockMvc
                .perform(get("/api/users"))
                .andReturn()
                .getResponse();

        assertThat(getResponse.getStatus()).isEqualTo(200);
        assertThat(getResponse.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(getResponse.getContentAsString()).doesNotContain("Mike");
    }

    @Test
    void testDeleteOtherUser() throws Exception {
        String fileContent = TestUtils
                .readJsonFromFile(
                TestUtils.USERS_LOGIN_PATH
                        + "/JackDoe.json");

        MockHttpServletResponse loginResponse = mockMvc
                .perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fileContent)
                )
                .andReturn()
                .getResponse();

        assertThat(loginResponse.getStatus()).isEqualTo(200);

        String token = loginResponse.getContentAsString();

        MockHttpServletResponse postResponse = mockMvc
                .perform(delete("/api/users/" + firstUserId)
                        .header(AUTHORIZATION, token))
                .andReturn()
                .getResponse();

        assertThat(postResponse.getStatus()).isEqualTo(403);
    }

    @Test
    void testDeleteWithoutToken() throws Exception {
        MockHttpServletResponse postResponse = mockMvc
                .perform(delete("/api/users/" + firstUserId))
                .andReturn()
                .getResponse();

        assertThat(postResponse.getStatus()).isEqualTo(403);
    }

    @Test
    void testDeleteWithIncorrectToken() throws Exception {
        MockHttpServletResponse postResponse = mockMvc
                .perform(delete("/api/users/" + firstUserId)
                        .header(AUTHORIZATION, "FSDFSDFSFSDFSF"))
                .andReturn()
                .getResponse();

        assertThat(postResponse.getStatus()).isEqualTo(403);
    }

}
