package com.microservicecrud.model;

public enum AccountType {
  SAVINGS("savings"), SALARIED("salaried");

  private final String text;

  AccountType(final String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

 /* public static AccountType getByValue(String val) {
    for (AccountType c : values()) {
      if (c.value.equals(val)) {
        return c;
      }
    }
  }*/
}
