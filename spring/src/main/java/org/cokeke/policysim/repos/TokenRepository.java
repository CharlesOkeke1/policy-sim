package org.cokeke.policysim.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.cokeke.policysim.models.Token;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
}
