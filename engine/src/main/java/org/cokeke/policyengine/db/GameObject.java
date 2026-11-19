package org.cokeke.policyengine.db;

import org.cokeke.policyengine.models.SpringRoundDTO;
import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private Long gameId;
    private List<Round> rounds;

    public GameObject() {}

    public GameObject(Long gameId, List<Round> rounds) {
        this.gameId = gameId;
        this.rounds = rounds;
    }

    public Long getGameId() {
        return gameId;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public List<SpringRoundDTO> getRoundsAsSpringDTO() {
        List<SpringRoundDTO> out = new ArrayList<>();
        for (Round round : this.rounds) {
            out.add(round.toSpringRoundDTO());
        }

        return out;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    public void addRound(Round round) {

    }
}
