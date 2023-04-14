package hexlet.code;

import com.fasterxml.jackson.core.type.TypeReference;
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

import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskManagerApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	private long firstUserId;
	private long secondUserId;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	public void createTestUsers() throws Exception {
		MockHttpServletResponse createResponseJohn = mockMvc
				.perform(
						post("/api/users")
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"firstName\": \"John\","
										+ " \"lastName\": \"Smith\","
										+ " \"email\": \"jsmith@mail.ru\","
										+ " \"password\": \"jsmith\"}")
				)
				.andReturn()
				.getResponse();

		assertThat(createResponseJohn.getStatus()).isEqualTo(200);

		firstUserId = Long
				.parseLong(
						objectMapper
								.readValue(
										createResponseJohn
												.getContentAsString(),
										new TypeReference<
												Map<String, String>
												>() { }
								).get("id"));

		MockHttpServletResponse createResponseJack = mockMvc
				.perform(
						post("/api/users")
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"firstName\": \"Jack\","
										+ " \"lastName\": \"Doe\","
										+ " \"email\": \"killer@mail.ru\","
										+ " \"password\": \"qwerty\"}")
				)
				.andReturn()
				.getResponse();

		assertThat(createResponseJack.getStatus()).isEqualTo(200);

		secondUserId = Long
				.parseLong(
						objectMapper
								.readValue(
										createResponseJack
												.getContentAsString(),
										new TypeReference<
												Map<String, String>
												>() { }
								).get("id"));
	}

	@Test
	void testRootPage() throws Exception {
		MockHttpServletResponse response = mockMvc
				.perform(get("/welcome"))
				.andReturn()
				.getResponse();

		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsString()).contains("Welcome to Spring");
	}

	@Test
	void testGetUser() throws Exception {
		MockHttpServletResponse loginResponse = mockMvc
				.perform(post("/api/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\": \"jsmith@mail.ru\", "
								+ "\"password\": \"jsmith\"}"))
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
		MockHttpServletResponse postResponse = mockMvc
				.perform(
						post("/api/users")
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"firstName\": \"Jackson\","
										+ " \"lastName\": \"Bind\","
										+ " \"email\": \"bind@mail.ru\","
										+ " \"password\": \"1234\"}")
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
		assertThat(getResponse.getContentAsString()).contains("Jackson", "Bind");
	}

	@Test
	void testUpdateUserPositive() throws Exception {
		MockHttpServletResponse loginResponse = mockMvc
				.perform(post("/api/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\": \"jsmith@mail.ru\", "
								+ "\"password\": \"jsmith\"}"))
				.andReturn()
				.getResponse();

		assertThat(loginResponse.getStatus()).isEqualTo(200);

		String token = loginResponse.getContentAsString();

		MockHttpServletResponse postResponse = mockMvc
				.perform(
						put("/api/users/" + firstUserId)
								.header(AUTHORIZATION, token)
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"firstName\": \"Mike\","
										+ " \"lastName\": \"Smith\","
										+ " \"email\": \"jsmith@mail.ru\","
										+ " \"password\": \"jsmith\"}")
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
		MockHttpServletResponse loginResponse = mockMvc
				.perform(post("/api/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\": \"killer@mail.ru\", "
								+ "\"password\": \"qwerty\"}"))
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
		MockHttpServletResponse postResponse = mockMvc
				.perform(
						post("/api/users")
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"firstName\": \"incorrect\","
										+ " \"lastName\": \"incorrect\","
										+ " \"email\": \"bind\","
										+ " \"password\": \"1\"}")
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
		MockHttpServletResponse postResponse = mockMvc
				.perform(
						post("/api/users")
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"firstName\": \"Mike\","
										+ " \"lastName\": \"Smith\","
										+ " \"email\": \"jsmith@mail.ru\","
										+ " \"password\": \"jsmith\"}")
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
		MockHttpServletResponse loginResponse = mockMvc
				.perform(post("/api/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\": \"jsmith@mail.ru\", "
								+ "\"password\": \"jsmith\"}"))
				.andReturn()
				.getResponse();

		assertThat(loginResponse.getStatus()).isEqualTo(200);

		String token = loginResponse.getContentAsString();

		MockHttpServletResponse postResponse = mockMvc
				.perform(
						put("/api/users/" + firstUserId)
								.header(AUTHORIZATION, token)
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"firstName\": \"incorrect\","
										+ " \"lastName\": \"incorrect\","
										+ " \"email\": \"bind\","
										+ " \"password\": \"1\"}")
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
	void testUpdateUserWithSameLogin() throws Exception {
		MockHttpServletResponse loginResponse = mockMvc
				.perform(post("/api/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\": \"killer@mail.ru\", "
								+ "\"password\": \"qwerty\"}"))
				.andReturn()
				.getResponse();

		assertThat(loginResponse.getStatus()).isEqualTo(200);

		String token = loginResponse.getContentAsString();

		MockHttpServletResponse postResponse = mockMvc
				.perform(
						put("/api/users/" + secondUserId)
								.header(AUTHORIZATION, token)
								.contentType(MediaType.APPLICATION_JSON)
								.content("{\"firstName\": \"Mike\","
										+ " \"lastName\": \"Smith\","
										+ " \"email\": \"jsmith@mail.ru\","
										+ " \"password\": \"jsmith\"}")
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

}
