package com.microservicecrud.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User {

  @javax.persistence.Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "userId")
  private long userId;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "phone_number", nullable = false)
  private Long phoneNumber;

  @Column(name = "address", nullable = false)
  private String address;

  @Column(name = "email_id", nullable = false)
  private String emailId;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "user" , cascade = CascadeType.ALL )
  //@JsonManagedReference
  @JsonIgnore
  private List<Account> accounts;

  public  User(){

  }

  public User(String fisrtName, String lastName, Long phoneNumber, String address, String emailId){
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

  public List<Account> getAccounts() {
    return accounts;
  }

  public void setAccounts(List<Account> accounts) {
    this.accounts = accounts;
  }

  @Override
  public String toString() {
    return "User{" +
      "userId=" + userId +
      ", firstName='" + firstName + '\'' +
      ", lastName='" + lastName + '\'' +
      ", phoneNumber=" + phoneNumber +
      ", address='" + address + '\'' +
      ", emailId='" + emailId + '\'' +
      ", accounts=" + accounts +
      '}';
  }
}
