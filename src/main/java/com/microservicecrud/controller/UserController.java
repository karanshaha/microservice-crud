package com.microservicecrud.controller;

import com.microservicecrud.dto.AccountDto;
import com.microservicecrud.dto.UserDto;
import com.microservicecrud.model.AccountType;
import com.microservicecrud.model.User;
import com.microservicecrud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api")
@Api(value = "User Management System", description = "Operations pertaining to user in user Management System")
public class UserController {

  @Autowired
  UserService userService;

  @ApiOperation(value = "View a list of available users", response = List.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully retrieved list"),
    @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
  })

  @GetMapping("/users")
  public List<UserDto> getAllUsers() {
    return userService.findAll();
  }

  @ApiOperation(value = "Get an user by Id")
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully retrieved user"),
    @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
  })
  @GetMapping("/users/{id}")
  public ResponseEntity getUserById(
    @ApiParam(value = "User id from which user object will retrieve", required = true)
    @PathVariable(value = "id") Long userId) {
    UserDto user = userService.findUserById(userId);
    if (user != null) {
      return ResponseEntity.ok().body(user);
    }
    return ResponseEntity.ok().body("User not found for this id ::" + userId);
  }

  @ApiOperation(value = "Add a user", notes = "Account type we do support is 'savings' and 'salaried' as text.")
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully added user"),
    @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
  })

  @PostMapping("/users")
  public ResponseEntity createUser(
    @ApiParam(value = "User object store in database table", required = true) @Valid @RequestBody UserDto userDto) {
    List<AccountDto> account = userDto.getAccount();

    if (!CollectionUtils.isEmpty(account) && account.size() > 0) {

      List<AccountDto> allowedAccTypes = account.stream()
        .filter(acc -> acc.getAccountType().equals(AccountType.SAVINGS.toString()) || acc.getAccountType().equals(AccountType.SALARIED.toString()))
        .collect(Collectors.toList());

      List<AccountDto> notAllowedAccTypes = account.stream()
        .filter(acc -> !acc.getAccountType().equals(AccountType.SAVINGS.toString()) && !acc.getAccountType().equals(AccountType.SALARIED.toString()))
        .collect(Collectors.toList());

      if (!CollectionUtils.isEmpty(notAllowedAccTypes) && notAllowedAccTypes.size() > 0) {
        return ResponseEntity.ok().body("Please check the account type !! We do only support 'savings' and 'salaried' type of accounts.");
      }

      if (!CollectionUtils.isEmpty(allowedAccTypes) && allowedAccTypes.size() > 0) {
        userDto.setAccount(allowedAccTypes);
      }

      UserDto savedUser = userService.saveUser(userDto);
      return ResponseEntity.ok().body(savedUser);
    }
    return ResponseEntity.ok().body("At least one account must be associated with user while creating user.");
  }

  @ApiOperation(value = "Update a user", notes = "Account type we do support is 'savings' and 'salaried' as text.")
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully updated user"),
    @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
  })

  @PutMapping("/users/{id}")
  public ResponseEntity updateUser(
    @ApiParam(value = "Id of the user to update", required = true) @PathVariable Long id,
    @ApiParam(value = "User object store in database table", required = true) @Valid @RequestBody UserDto userDto) {
    List<AccountDto> account = userDto.getAccount();

    if (!CollectionUtils.isEmpty(account) && account.size() > 0) {
      List<AccountDto> allowedAccTypes = account.stream()
        .filter(acc -> acc.getAccountType().equals(AccountType.SAVINGS.toString()) || acc.getAccountType().equals(AccountType.SALARIED.toString()))
        .collect(Collectors.toList());

      List<AccountDto> notAllowedAccTypes = account.stream()
        .filter(acc -> !acc.getAccountType().equals(AccountType.SAVINGS.toString()) && !acc.getAccountType().equals(AccountType.SALARIED.toString()))
        .collect(Collectors.toList());

      if (!CollectionUtils.isEmpty(notAllowedAccTypes) && notAllowedAccTypes.size() > 0) {
        return ResponseEntity.ok().body("Please check the account type !! We do only support 'savings' and 'salaried' type of accounts.");
      }

      if (!CollectionUtils.isEmpty(allowedAccTypes) && allowedAccTypes.size() > 0) {
        userDto.setAccount(allowedAccTypes);
      }
    }

    UserDto savedUser = userService.updateUser(userDto, id);
    if (savedUser != null) {
      return ResponseEntity.ok().body(savedUser);
    }
    return ResponseEntity.ok().body("The given user for updation was not found !!" + id);
  }

  @ApiOperation(value = "Partial Update a user")
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully updated user"),
    @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
  })

  @PatchMapping("/users/{id}")
  public ResponseEntity patchUser(
    @ApiParam(value = "Field that are updated  and to be stored in database table", required = true) @Valid @RequestBody Map<Object, Object> fields,
    @ApiParam(value = "Id of the user to update", required = true) @PathVariable Long id
  ) {
    UserDto savedUser = userService.findUserById(id);

    if (savedUser != null) {

      fields.forEach((k, v) -> {
        Field field = ReflectionUtils.findField(UserDto.class, (String) k);
        field.setAccessible(true);
        ReflectionUtils.setField(field, savedUser, v);
      });
      User user = userService.updateUserPartially(savedUser);
      return ResponseEntity.ok().body(user);
    }
    return ResponseEntity.ok().body("The given user for updation was not found !!" + id);
  }

  @ApiOperation(value = "Delete a user")
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successfully deleted user"),
    @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
  })

  @DeleteMapping("/users/{id}")
  public ResponseEntity deleteUser(@ApiParam(value = "User Id by which user will be deleted", required = true)
  @PathVariable(value = "id") Long userId) {
    try {
      userService.deleteUser(userId);
      return ResponseEntity.ok().body("User with id " + userId + " deleted successfully.");
    } catch (EmptyResultDataAccessException e) {
      return ResponseEntity.ok().body("User you are trying to delete does not exist !");
    }
  }

  /*private boolean validateAccType(List<AccountDto> account, List<AccountDto> allowedAccTypes, List<AccountDto> notAllowedAccTypes) {

    if (!CollectionUtils.isEmpty(account) && account.size() > 0) {
      allowedAccTypes = account.stream()
        .filter(acc -> acc.getAccountType().equals(AccountType.SAVINGS.toString()) || acc.getAccountType().equals(AccountType.SALARIED.toString()))
        .collect(Collectors.toList());

      notAllowedAccTypes = account.stream()
        .filter(acc -> !acc.getAccountType().equals(AccountType.SAVINGS.toString()) && !acc.getAccountType().equals(AccountType.SALARIED.toString()))
        .collect(Collectors.toList());

      if (!CollectionUtils.isEmpty(notAllowedAccTypes) && notAllowedAccTypes.size() > 0) {
        return false;
      }
      if (!CollectionUtils.isEmpty(allowedAccTypes) && allowedAccTypes.size() > 0) {
        return true;
      }
    }
    return false;
  }*/
}

