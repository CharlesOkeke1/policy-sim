package org.cokeke.policyengine.db;
import java.util.List;
import org.cokeke.policyengine.models.SpringRoundDTO;



public interface GameRepository {
    void saveRound(Long gameId, List<SpringRoundDTO> rounds);
    GameObject getGame(Long gameId);
    List<Round> getMonth(Long gameId, int currentMonth);
    Long deleteGame(Long gameId);
    boolean gameExists(Long gameId);
}