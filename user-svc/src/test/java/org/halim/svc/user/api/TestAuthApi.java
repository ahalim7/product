package org.halim.svc.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.halim.svc.user.api.data.UserTestDataFactory;
import org.halim.svc.user.domain.dto.AuthRequest;
import org.halim.svc.user.domain.dto.CreateUserRequest;
import org.halim.svc.user.domain.dto.UserView;
import org.halim.svc.user.util.JsonHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.halim.svc.user.util.JsonHelper.fromJson;
import static java.lang.System.currentTimeMillis;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestAuthApi {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;
  private final UserTestDataFactory userTestDataFactory;

  private final String password = "Test12345_";

  @Autowired
  public TestAuthApi(MockMvc mockMvc, ObjectMapper objectMapper, UserTestDataFactory userTestDataFactory) {
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
    this.userTestDataFactory = userTestDataFactory;
  }

  @Test
  public void testLoginSuccess() throws Exception {
    UserView userView = userTestDataFactory.createUser(String.format("test.user.%d@nix.io", currentTimeMillis()),
      "Test User", password);

    AuthRequest request = new AuthRequest(userView.username(), password);

    MvcResult createResult = this.mockMvc
      .perform(post("/api/public/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonHelper.toJson(objectMapper, request)))
      .andExpect(status().isOk())
      .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
      .andReturn();

    UserView authUserView = JsonHelper.fromJson(objectMapper, createResult.getResponse().getContentAsString(), UserView.class);
    assertEquals(userView.id(), authUserView.id(), "User ids must match!");
  }

  @Test
  public void testLoginFail() throws Exception {
    UserView userView = userTestDataFactory.createUser(String.format("test.user.%d@nix.io", currentTimeMillis()),
      "Test User", password);

    AuthRequest request = new AuthRequest(userView.username(), "zxc");

    this.mockMvc
      .perform(post("/api/public/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonHelper.toJson(objectMapper, request)))
      .andExpect(status().isUnauthorized())
      .andExpect(header().doesNotExist(HttpHeaders.AUTHORIZATION))
      .andReturn();
  }

  @Test
  public void testRegisterSuccess() throws Exception {
    CreateUserRequest goodRequest = new CreateUserRequest(
      String.format("test.user.%d@nix.com", currentTimeMillis()),
      "Test User A",
      password
    );

    MvcResult createResult = this.mockMvc
      .perform(post("/api/public/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonHelper.toJson(objectMapper, goodRequest)))
      .andExpect(status().isOk())
      .andReturn();

    UserView userView = JsonHelper.fromJson(objectMapper, createResult.getResponse().getContentAsString(), UserView.class);
    assertNotNull(userView.id(), "User id must not be null!");
    assertEquals(goodRequest.fullName(), userView.fullName(), "User fullname  update isn't applied!");
  }

  @Test
  public void testRegisterFail() throws Exception {
    CreateUserRequest badRequest = new CreateUserRequest(
      "invalid.username", "", ""
    );

    this.mockMvc
      .perform(post("/api/public/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonHelper.toJson(objectMapper, badRequest)))
      .andExpect(status().isBadRequest())
      .andExpect(content().string(containsString("Method argument validation failed")));
  }

}
