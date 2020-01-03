package com.microservicecrud.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

public class UserDto {

  @JsonIgnore
  private long userId;

  private String firstName;

  private String lastName;

  private Long phoneNumber;

  private String address;

  private String emailId;

  private List<AccountDto> account;

  public UserDto() {

  }

  public UserDto(String fisrtName, String lastName, Long phoneNumber, String address, String emailId) {
    this.firstName = fisrtName;
    this.lastName = lastName;
    this.phoneNumber = phoneNumber;
    this.address = address;
    this.emailId = emailId;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Long getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(Long phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getEmailId() {
    return emailId;
  }

  public void setEmailId(String emailId) {
    this.emailId = emailId;
  }

  public List<AccountDto> getAccount() {
    return account;
  }

  public void setAccount(List<AccountDto> account) {
    this.account = account;
  }

  @Override
  public String toString() {
    return "UserDto{" +
      ", firstName='" + firstName +
      ", lastName='" + lastName + '\'' +
      ", phoneNumber=" + phoneNumber +
      ", address='" + address + '\'' +
      ", emailId='" + emailId + '\'' +
      ", AccountDto=" + account +
      '}';
  }
}

