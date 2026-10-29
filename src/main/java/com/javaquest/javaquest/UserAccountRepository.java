
package com.javaquest.javaquest;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository
        extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<UserAccount> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
