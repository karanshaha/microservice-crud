package com.microservicecrud.repository;

import com.microservicecrud.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

  void deleteByUserUserId(Long userId);

  List<Account> getAccountByUserUserId(Long userId);
}
