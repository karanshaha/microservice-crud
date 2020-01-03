package com.microservicecrud.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicecrud.dto.AccountDto;
import com.microservicecrud.dto.UserDto;
import com.microservicecrud.model.Account;
import com.microservicecrud.model.User;
import com.microservicecrud.repository.UserRepository;
import com.microservicecrud.service.UserService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class)
class UserControllerTest {

  @MockBean
  UserService userService;

  @MockBean
  UserRepository userRepository;

  @Autowired
  private MockMvc mockMvc;

  private ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void getAllUsers() throws Exception {

    String uri = "http://localhost:8080/api/users";

    List<UserDto> userDtoList = createUserDTODummyList();
    List<User> users = createUserDummyList();

    String jsonResponse = "[{\"firstName\":\"Karan\",\"lastName\":\"shaha\",\"phoneNumber\":56565656555,\"address\":\"Pune\",\"emailId\":\"karan@g.com\",\"account\":[{\"accountId\":1,\"balance\":100.0,\"accountType\":\"savings\"}]}]";
    when(userService.findAll()).thenReturn(userDtoList);
    when(userRepository.findAll()).thenReturn(users);

    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri)
      .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

    assertEquals(200, mvcResult.getResponse().getStatus());
    assertEquals(jsonResponse, mvcResult.getResponse().getContentAsString());

  }

  @Test
  void getUserById() throws Exception {
    String uri = "http://localhost:8080/api/users/1";

    String jsonResponse = "{\"firstName\":\"Karan\",\"lastName\":\"shaha\",\"phoneNumber\":56565656555,\"address\":\"Pune\",\"emailId\":\"karan@g.com\",\"account\":[{\"accountId\":1,\"balance\":100.0,\"accountType\":\"savings\"}]}";
    List<UserDto> userDtoList = createUserDTODummyList();
    List<User> users = createUserDummyList();

    when(userService.findUserById(1L)).thenReturn(userDtoList.get(0));
    when(userRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(users.get(0)));

    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri)
      .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

    assertEquals(200, mvcResult.getResponse().getStatus());
    assertEquals(jsonResponse, mvcResult.getResponse().getContentAsString());

  }

  @Test
  void getUserByIdNotValidUser() throws Exception {
    String uri = "http://localhost:8080/api/users/3";

    String jsonResponse = "User not found for this id ::3";
    List<UserDto> userDtoList = createUserDTODummyList();
    List<User> users = createUserDummyList();

    when(userService.findUserById(1L)).thenReturn(userDtoList.get(0));
    when(userRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(users.get(0)));

    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri)
      .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

    assertEquals(200, mvcResult.getResponse().getStatus());
    assertEquals(jsonResponse, mvcResult.getResponse().getContentAsString());

  }

  @Test
  void createUserWithvalidAccType() throws Exception {
    String uri = "http://localhost:8080/api/users/";
    List<UserDto> userDtoList = createUserDTODummyList();

    UserDto userDto = userDtoList.get(0);

    String content = mapper.writeValueAsString(userDto);

    when(userService.saveUser(userDto)).thenReturn(userDto);

    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(uri)
      .contentType(MediaType.APPLICATION_JSON_VALUE).content(content)).andReturn();

    assertEquals(200, mvcResult.getResponse().getStatus());
  }

  @Test
  void createUserWithInvalidAccType() throws Exception {
    String uri = "http://localhost:8080/api/users/";
    List<UserDto> userDtoList = createUserDTODummyList();
    String expContent = "Please check the account type !! We do only support 'savings' and 'salaried' type of accounts.";

    UserDto userDto = userDtoList.get(0);
    AccountDto accountDto = userDto.getAccount().get(0);
    accountDto.setAccountType("sdsdfsdf");

    String content = mapper.writeValueAsString(userDto);

    when(userService.saveUser(userDto)).thenReturn(userDto);

    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(uri)
      .contentType(MediaType.APPLICATION_JSON_VALUE).content(content)).andReturn();

    assertEquals(200, mvcResult.getResponse().getStatus());
    assertEquals(expContent, mvcResult.getResponse().getContentAsString());
  }

  @Test
  void updateUser() throws Exception {
    String uri = "http://localhost:8080/api/users/0";
    String respContent = "{\"firstName\":\"Karan\",\"lastName\":\"shaha\",\"phoneNumber\":56565656555,\"address\":\"Pune\",\"emailId\":\"changed@mdoified.com\",\"account\":[{\"accountId\":1,\"balance\":100.0,\"accountType\":\"savings\"}]}";

    List<UserDto> userDtoList = createUserDTODummyList();
    UserDto userDto = userDtoList.get(0);
    userDto.setEmailId("changed@mdoified.com");
    //userDto.setUserId(1);
    String content = mapper.writeValueAsString(userDto);

    when(userService.updateUser(any(UserDto.class), any(Long.class))).thenReturn(userDto);

    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(uri)
      .contentType(MediaType.APPLICATION_JSON_VALUE).content(content)).andReturn();
    assertEquals(200, mvcResult.getResponse().getStatus());
    assertEquals(respContent, mvcResult.getResponse().getContentAsString());
  }


  @Test
  void updateUserForInvalidId() throws Exception {
    String uri = "http://localhost:8080/api/users/1";
    String respContent = "The given user for updation was not found !!1";

    List<UserDto> userDtoList = createUserDTODummyList();
    UserDto userDto = userDtoList.get(0);
    String content = mapper.writeValueAsString(userDto);

    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(uri)
      .contentType(MediaType.APPLICATION_JSON_VALUE).content(content)).andReturn();
    assertEquals(200, mvcResult.getResponse().getStatus());
    assertEquals(respContent, mvcResult.getResponse().getContentAsString());
  }

  @Test
  void patchUser() throws Exception {
    String uri = "http://localhost:8080/api/users/1";
    String respContent = "{\"userId\":0,\"firstName\":\"KK\",\"lastName\":\"SH\",\"phoneNumber\":56565656555,\"address\":\"Pune\",\"emailId\":\"karan@g.com\"}";

    HashMap<Object, Object> req = new HashMap<>();
    req.put("firstName", "KK");
    req.put("lastName", "SH");

    String content = null;
    try {
      content = mapper.writeValueAsString(req);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    User user = new User("KK", "SH", 56565656555L, "Pune", "karan@g.com");
    user.setUserId(0);

    when(userService.findUserById(1L)).thenReturn(createUserDTODummyList().get(0));

    when(userService.updateUserPartially(any(UserDto.class))).thenReturn(user);

    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(uri)
      .contentType(MediaType.APPLICATION_JSON_VALUE).content(content)).andReturn();

    assertEquals(200, mvcResult.getResponse().getStatus());
    assertEquals(respContent, mvcResult.getResponse().getContentAsString());

  }

  @Test
  void patchUserWithRandomIdThatDoesnotExist() throws Exception {
    String uri = "http://localhost:8080/api/users/12";
    String respContent = "The given user for updation was not found !!12";

    HashMap<Object, Object> req = new HashMap<>();
    req.put("firstName", "KK");
    req.put("lastName", "SH");

    String content = null;
    try {
      content = mapper.writeValueAsString(req);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    User user = new User("KK", "SH", 56565656555L, "Pune", "karan@g.com");
    user.setUserId(0);

    when(userService.findUserById(1L)).thenReturn(createUserDTODummyList().get(0));

    when(userService.updateUserPartially(any(UserDto.class))).thenReturn(user);

    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(uri)
      .contentType(MediaType.APPLICATION_JSON_VALUE).content(content)).andReturn();

    assertEquals(200, mvcResult.getResponse().getStatus());
    assertEquals(respContent, mvcResult.getResponse().getContentAsString());

  }

  @Test
  void deleteUser() throws Exception {
    String uri = "http://localhost:8080/api/users/10";
    String response = "User with id 10 deleted successfully.";
    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(uri)
      .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

    assertEquals(200, mvcResult.getResponse().getStatus());
    assertEquals(response, mvcResult.getResponse().getContentAsString());
  }

  private List<UserDto> createUserDTODummyList() {
    UserDto userDto = new UserDto("Karan", "shaha", 56565656555L, "Pune", "karan@g.com");

    List<AccountDto> accountDtoList = new ArrayList<>();
    List<UserDto> userDtoList = new ArrayList<>();

    AccountDto accountDto = new AccountDto();

    accountDto.setAccountId(1L);
    accountDto.setAccountType("savings");
    accountDto.setBalance(100F);

    accountDtoList.add(accountDto);

    userDto.setAccount(accountDtoList);
    userDtoList.add(userDto);

    return userDtoList;

  }

  private List<User> createUserDummyList() {
    User user = new User("Karan", "shaha", 56565656555L, "Pune", "karan@g.com");

    List<Account> accounts = new ArrayList<>();
    List<User> users = new ArrayList<>();

    Account account = new Account();

    account.setAccountId(1L);
    account.setAccountType("savings");
    account.setBalance(100F);

    accounts.add(account);

    user.setAccounts(accounts);

    users.add(user);

    return users;

  }
}