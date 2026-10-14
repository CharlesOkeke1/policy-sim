package org.cokeke.policysim.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.cokeke.policysim.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}