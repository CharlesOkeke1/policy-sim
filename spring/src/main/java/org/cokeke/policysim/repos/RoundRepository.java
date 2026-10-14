package org.cokeke.policysim.repos;

import org.cokeke.policysim.models.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {
    List<Round> findByGame_Id(Long gameId);
}
