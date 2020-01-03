package com.microservicecrud.service;

import com.microservicecrud.dto.AccountDto;
import com.microservicecrud.dto.UserDto;
import com.microservicecrud.model.Account;
import com.microservicecrud.model.User;
import com.microservicecrud.repository.AccountRepository;
import com.microservicecrud.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;

@Transactional
@Service
public class UserService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  AccountRepository accountRepository;

  public UserDto saveUser(UserDto userDto) {
    User user = new User(userDto.getFirstName(), userDto.getLastName(), userDto.getPhoneNumber(),
      userDto.getAddress(), userDto.getEmailId());

    List<Account> userAccount = getAccounts(userDto, user);
    user.setAccounts(userAccount);

    userRepository.save(user);
    return userDto;
  }

  public UserDto updateUser(UserDto userDto, Long userId) {
    Optional<User> optionalUser = userRepository.findById(userId);

    if (optionalUser.isPresent()) {
      User existingUser = optionalUser.get();
      existingUser.setFirstName(userDto.getFirstName() != null ? userDto.getFirstName() : existingUser.getFirstName());
      existingUser.setLastName(userDto.getLastName() != null ? userDto.getLastName() : existingUser.getLastName());
      existingUser.setPhoneNumber(userDto.getPhoneNumber() != null ? userDto.getPhoneNumber() : existingUser.getPhoneNumber());
      existingUser.setEmailId(userDto.getEmailId() != null ? userDto.getEmailId() : existingUser.getEmailId());
      existingUser.setAddress(userDto.getAddress() != null ? userDto.getAddress() : existingUser.getAddress());

      List<AccountDto> accountDtoLst = userDto.getAccount();
      List<Account> userAccount = getAccounts(userDto, existingUser);

      List<Account> finalUserAccount = null;
      if (accountDtoLst != null && accountDtoLst.size() > 0) {
        finalUserAccount = new ArrayList<>();
        for (Account acc : userAccount) {
          Optional<Account> existingAcc = accountRepository.findById(acc.getAccountId());
          if (existingAcc.isPresent()) {
            Account account = existingAcc.get();
            account.setAccountType(acc.getAccountType() != null ? acc.getAccountType() : account.getAccountType());
            account.setBalance(acc.getBalance() != null ? acc.getBalance() : account.getBalance());
            accountRepository.save(account);
            finalUserAccount.add(account);
          } else {
            finalUserAccount.add(acc);
          }

        }

      } else if (accountDtoLst != null && accountDtoLst.isEmpty()) {
        accountRepository.deleteByUserUserId(userId);
      } else if (CollectionUtils.isEmpty(finalUserAccount)) {
        finalUserAccount = accountRepository.getAccountByUserUserId(userId);
      }

      existingUser.setAccounts(finalUserAccount);
      List<User> userList = new ArrayList<>();

      User updatedUser = userRepository.saveAndFlush(existingUser);
      userList.add(updatedUser);
      List<UserDto> userDtoList = convertEntityToDto(userList);
      return userDtoList.get(0);
    }
    return null;
  }

  public User updateUserPartially(UserDto userDto) {
    ModelMapper mapper = new ModelMapper();
    User user = mapper.map(userDto, User.class);
    return userRepository.save(user);
  }

  public void deleteUser(Long userId) {
    userRepository.deleteById(userId);
  }

  public UserDto findUserById(Long userId) {
    Optional op = userRepository.findById(userId);
    List<User> userList = new ArrayList<>();
    if (op.isPresent()) {
      User user = (User) op.get();
      userList.add(user);
      return convertEntityToDto(userList).get(0);
    }
    return null;
  }

  public List<UserDto> findAll() {
    List<User> allUsers = userRepository.findAll();
    return convertEntityToDto(allUsers);
  }

  private List<Account> getAccounts(UserDto userDto, User user) {
    ModelMapper model = new ModelMapper();
    List<AccountDto> userDtoAccounts = userDto.getAccount();
    List<Account> userAccount = new ArrayList<>();

    if (!CollectionUtils.isEmpty(userDtoAccounts)) {
      for (AccountDto accountDto : userDtoAccounts) {
        Account account;
        account = model.map(accountDto, Account.class);
        account.setUser(user);
        userAccount.add(account);
      }
      List<Account> accounts = user.getAccounts();

      if (accounts != null) {
        for (Account acc : accounts) {
          if (!userAccount.contains(acc)) {
            userAccount.add(acc);
          }
        }
      }
    }
    return userAccount;
  }

  private List<UserDto> convertEntityToDto(List<User> allUsers) {
    List<UserDto> userDtoList = new ArrayList<>();
    if (!CollectionUtils.isEmpty(allUsers)) {

      ModelMapper model = new ModelMapper();
      ModelMapper model1 = new ModelMapper();

      for (User user : allUsers) {
        UserDto userDto;
        List<AccountDto> accountDtos = new ArrayList<>();
        userDto = model.map(user, UserDto.class);

        List<Account> accounts = user.getAccounts();
        if (!CollectionUtils.isEmpty(accounts)) {
          for (Account account : accounts) {
            model1.map(account, AccountDto.class);
            accountDtos.add(model1.map(account, AccountDto.class));
          }
        }

        userDto.setAccount(accountDtos);
        userDtoList.add(userDto);
      }
    }
    return userDtoList;
  }
}

