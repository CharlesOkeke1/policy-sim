package org.cokeke.policysim.repos;

import org.cokeke.policysim.models.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {
    List<Round> findByGame_Id(Long gameId);
    
    @Query(value = "SELECT * FROM rounds WHERE game_id IN (SELECT id FROM games WHERE user_id = :userId)", nativeQuery = true)
    Optional<List<Round>> getUsersGamesWithTheirRounds(@Param ("userId") Long userId);
}
