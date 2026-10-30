package org.cokeke.policysim.repos;
import org.cokeke.policysim.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameSessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> getByEngineToken(String token);
}
