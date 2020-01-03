package com.microservicecrud.service;

import com.microservicecrud.dto.AccountDto;
import com.microservicecrud.dto.UserDto;
import com.microservicecrud.model.Account;
import com.microservicecrud.model.User;
import com.microservicecrud.repository.AccountRepository;
import com.microservicecrud.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserService.class)
class UserServiceTest {

  @Autowired
  UserService userService;

  @MockBean
  UserRepository userRepository;

  @MockBean
  AccountRepository accountRepository;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() {
    reset(userRepository, accountRepository);
  }

  @Test
  void saveUser() {

    UserDto userDto = createUserDTODummyList().get(0);
    userDto.setUserId(0);
    User user = new User(userDto.getFirstName(), userDto.getLastName(), userDto.getPhoneNumber(),
      userDto.getAddress(), userDto.getEmailId());
    user.setUserId(0);

    when(userRepository.save(user)).thenReturn(user);
    userDto = userService.saveUser(userDto);

    assertEquals(userDto.getUserId(), user.getUserId());

  }

  @Test
  void updateUserWithLastNameAndAccBalance() {
    UserDto userDto = createUserDTODummyList().get(0);

    User user = new User(userDto.getFirstName(), userDto.getLastName(), userDto.getPhoneNumber(),
      userDto.getAddress(), userDto.getEmailId());
    List<AccountDto> accounts = userDto.getAccount();
    AccountDto accountDto = accounts.get(0);

    Account acc = new Account();
    acc.setAccountId(accountDto.getAccountId());
    acc.setAccountType(accountDto.getAccountType());
    acc.setBalance(accountDto.getBalance());
    acc.setUser(user);

    when(userRepository.findById(userDto.getUserId())).thenReturn(java.util.Optional.of(user));
    when(accountRepository.findById(1L)).thenReturn(java.util.Optional.of(acc));
    when(userRepository.saveAndFlush(user)).thenReturn(user);

    userDto.setLastName("ModifiedLastName");
    userDto.getAccount().get(0).setBalance(5000f);
    UserDto actual = userService.updateUser(userDto, 1L);

    assertEquals(userDto.getLastName(), actual.getLastName());
    assertEquals(userDto.getAccount().get(0).getBalance(), actual.getAccount().get(0).getBalance());

  }

  @Test
  void updateUserWithNewAccount() {
    UserDto userDto = createUserDTODummyList().get(0);

    User user = new User(userDto.getFirstName(), userDto.getLastName(), userDto.getPhoneNumber(),
      userDto.getAddress(), userDto.getEmailId());
    userDto.setAccount(null);

    AccountDto acc = new AccountDto();
    acc.setAccountId(1L);
    acc.setAccountType("savings");
    acc.setBalance(600F);

    List<AccountDto> accounts = new ArrayList<>();
    accounts.add(acc);

    when(userRepository.findById(userDto.getUserId())).thenReturn(java.util.Optional.of(user));
    when(userRepository.saveAndFlush(user)).thenReturn(user);

    userDto.setAccount(accounts);
    UserDto actual = userService.updateUser(userDto, 1L);

    assertEquals(userDto.getAccount().get(0).getBalance(), actual.getAccount().get(0).getBalance());
    assertEquals(1, actual.getAccount().size());

  }

  @Test
  void updateUserDeleteAccounts() {
    UserDto userDto = createUserDTODummyList().get(0);

    User user = new User(userDto.getFirstName(), userDto.getLastName(), userDto.getPhoneNumber(),
      userDto.getAddress(), userDto.getEmailId());
    userDto.setAccount(null);

    when(userRepository.findById(userDto.getUserId())).thenReturn(java.util.Optional.of(user));
    when(userRepository.saveAndFlush(user)).thenReturn(user);

    List<AccountDto> accounts = new ArrayList<>();
    userDto.setAccount(accounts);
    UserDto actual = userService.updateUser(userDto, 1L);

    assertTrue(actual.getAccount().size() == 0);

  }

  @Test
  void updateUserFirstNameAndJustBringAccounts() {
    UserDto userDto = createUserDTODummyList().get(0);

    User user = new User(userDto.getFirstName(), userDto.getLastName(), userDto.getPhoneNumber(),
      userDto.getAddress(), userDto.getEmailId());
    userDto.setAccount(null);

    List<Account> accounts = new ArrayList<>();
    Account acc = new Account();
    acc.setAccountId(1);
    acc.setAccountType("savings");
    acc.setBalance(400f);
    acc.setUser(user);
    accounts.add(acc);

    when(userRepository.findById(userDto.getUserId())).thenReturn(java.util.Optional.of(user));
    when(userRepository.saveAndFlush(user)).thenReturn(user);
    when(accountRepository.getAccountByUserUserId(1l)).thenReturn(accounts);

    userDto.setAccount(null);
    userDto.setFirstName("KK");
    UserDto actual = userService.updateUser(userDto, 1L);
    Float amt = actual.getAccount().get(0).getBalance();
    Float expectedAmt = 400f;
    assertTrue(actual.getAccount().size() == 1);
    assertEquals(expectedAmt, amt);
  }

  @Test
  void updateUserPartially() {
    UserDto userDto = createUserDTODummyList().get(0);

    User user = new User(userDto.getFirstName(), userDto.getLastName(), userDto.getPhoneNumber(),
      userDto.getAddress(), userDto.getEmailId());
    user.setUserId(1);
    user.setAddress("USA");
    when(userRepository.save(any(User.class))).thenReturn(user);
    userDto.setAddress("USA");
    User actuallUser = userService.updateUserPartially(userDto);

    assertEquals("USA", actuallUser.getAddress());
  }

  @Test
  void deleteUser() {

    userRepository.deleteById(1l);
  }

  @Test
  void findUserById() {
    UserDto userDto = createUserDTODummyList().get(0);
    User user = new User(userDto.getFirstName(), userDto.getLastName(), userDto.getPhoneNumber(),
      userDto.getAddress(), userDto.getEmailId());
    user.setUserId(1);

    when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));

    UserDto actualUserDto = userService.findUserById(1L);
    assertEquals(1, actualUserDto.getUserId());
    assertEquals("karan@g.com", actualUserDto.getEmailId());

  }

  @Test
  void findAll() {
    List<UserDto> userDtos = createUserDTODummyList();
    UserDto userDto = userDtos.get(0);
    User user = new User(userDto.getFirstName(), userDto.getLastName(), userDto.getPhoneNumber(),
      userDto.getAddress(), userDto.getEmailId());
    user.setUserId(1);
    List<User> users = new ArrayList<>();
    users.add(user);

    when(userRepository.findAll()).thenReturn(users);

    List<UserDto> userDtos1 = userService.findAll();
    assertNotNull(userDtos1);
    assertEquals(1, userDtos1.get(0).getUserId());

  }

  private List<UserDto> createUserDTODummyList() {
    UserDto userDto = new UserDto("Karan", "shaha", 56565656555L, "Pune", "karan@g.com");
    userDto.setUserId(1);
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
}