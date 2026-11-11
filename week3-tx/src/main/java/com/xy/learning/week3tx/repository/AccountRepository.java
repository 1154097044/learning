package com.xy.learning.week3tx.repository;


import com.xy.learning.week3tx.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * 账户 Repository：方法名派生查询 findByEmail 同款套路，这里按 name 查账户。
 */
public interface AccountRepository extends JpaRepository<Account,Long> {

    Optional<Account> findByName(String name);
}
