package org.cokeke.policysim.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import org.cokeke.policysim.models.Token;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    Optional<Token> findByUserId(Long id);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.userId = :userId")
    int deleteByUserId(@Param("userId") Long userId);
}
