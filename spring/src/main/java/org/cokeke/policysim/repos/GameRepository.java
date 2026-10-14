package org.cokeke.policysim.repos;

import org.cokeke.policysim.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByCompleted(Boolean status);
    List<Game> findByUserId(Long userId);
}