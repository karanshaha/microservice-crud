package com.microservicecrud.dto;

public class AccountDto {

  private Long accountId;
  private Float balance;
  private String accountType;

  public Float getBalance() {
    return balance;
  }

  public void setBalance(Float balance) {
    this.balance = balance;
  }

  public String getAccountType() {
    return accountType;
  }

  public void setAccountType(String accountType) {
    this.accountType = accountType;
  }

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  @Override
  public String toString() {
    return "AccountDto{" +
      ", balance='" + balance +
      ", accountType='" + accountType + '\'' +
      '}';
  }

}
