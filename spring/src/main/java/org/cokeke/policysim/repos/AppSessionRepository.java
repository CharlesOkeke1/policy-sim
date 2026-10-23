package org.cokeke.policysim.repos;

import java.util.Optional;

import org.cokeke.policysim.models.AppSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppSessionRepository extends JpaRepository<AppSession, Long>{
    Optional<AppSession> getByRefreshToken(String token);
    Optional<AppSession> getByUserId(Long id);
}
