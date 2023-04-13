package hexlet.code;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
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
import com.github.database.rider.junit5.api.DBRider;
import com.github.database.rider.core.api.dataset.DataSet;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DBRider
@DataSet("users.yml")
class TaskManagerApplicationTests {

	@Autowired
	private MockMvc mockMvc;

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
		MockHttpServletResponse response = mockMvc
				.perform(get("/api/users/1"))
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
		MockHttpServletResponse postResponse = mockMvc
				.perform(
						put("/api/users/1")
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
		MockHttpServletResponse postResponse = mockMvc
				.perform(delete("/api/users/2"))
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
		MockHttpServletResponse postResponse = mockMvc
				.perform(
						put("/api/users/1")
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
		MockHttpServletResponse postResponse = mockMvc
				.perform(
						put("/api/users/2")
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
	void testDeleteNonExistentUser() throws Exception {
		MockHttpServletResponse postResponse = mockMvc
				.perform(delete("/api/users/6"))
				.andReturn()
				.getResponse();

		assertThat(postResponse.getStatus()).isEqualTo(422);
	}

}
