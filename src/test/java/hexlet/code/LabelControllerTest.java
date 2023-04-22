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
public class LabelControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private long labelId;

    private String token;

    @BeforeEach
    public void createUserAndLabel() throws Exception {
        MockHttpServletResponse createUserResponse = mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtils.readJsonFromFile(TestUtils.USERS_PATH + "/JohnSmith.json"))
                )
                .andReturn()
                .getResponse();


        MockHttpServletResponse loginResponse = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.readJsonFromFile(TestUtils.USERS_LOGIN_PATH + "/JohnSmith.json"))
                )
                .andReturn()
                .getResponse();

        token = loginResponse.getContentAsString();

        MockHttpServletResponse createLabelResponse = mockMvc
                .perform(MockMvcRequestBuilders.post("/api/labels")
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.readJsonFromFile(TestUtils.LABELS_PATH + "/Bug.json"))
                )
                .andReturn()
                .getResponse();

        assertThat(createLabelResponse.getStatus()).isEqualTo(201);

        labelId = TestUtils.parseIdFromResponse(createLabelResponse.getContentAsString());
    }

    @Test
    void testGetAllLabels() throws Exception {
        MockHttpServletResponse getResponse = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/labels"))
                .andReturn()
                .getResponse();

        assertThat(getResponse.getStatus()).isEqualTo(200);
        assertThat(getResponse.getContentAsString()).contains("Bug");
    }

    @Test
    void testGetTask() throws Exception {
        MockHttpServletResponse getResponse = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/labels/" + labelId))
                .andReturn()
                .getResponse();

        assertThat(getResponse.getStatus()).isEqualTo(200);
        assertThat(getResponse.getContentAsString()).contains("Bug");
    }

    @Test
    void testUpdateLabel() throws Exception {
        MockHttpServletResponse putResponse = mockMvc
                .perform(MockMvcRequestBuilders.put("/api/labels/" + labelId)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.readJsonFromFile(TestUtils.LABELS_PATH + "/Feature.json"))
                )
                .andReturn()
                .getResponse();

        assertThat(putResponse.getStatus()).isEqualTo(200);

        MockHttpServletResponse getResponse = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/labels"))
                .andReturn()
                .getResponse();

        assertThat(getResponse.getStatus()).isEqualTo(200);
        assertThat(getResponse.getContentAsString()).contains("Feature");
    }

    @Test
    void testDeleteTask() throws Exception {
        MockHttpServletResponse deleteResponse = mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/labels/" + labelId)
                        .header(AUTHORIZATION, token))
                .andReturn()
                .getResponse();

        assertThat(deleteResponse.getStatus()).isEqualTo(200);

        MockHttpServletResponse getLabelsResponse = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/labels"))
                .andReturn()
                .getResponse();

        assertThat(getLabelsResponse.getStatus()).isEqualTo(200);
        assertThat(getLabelsResponse.getContentAsString()).doesNotContain("Bug");
    }
}
