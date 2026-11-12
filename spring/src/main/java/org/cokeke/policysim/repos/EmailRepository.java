package org.cokeke.policysim.repos;

import org.cokeke.policysim.models.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    List<Email> findByUserId(Long userId);
}