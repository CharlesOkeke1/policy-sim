package org.cokeke.policysim.services;

import org.cokeke.policysim.models.Game;
import org.cokeke.policysim.repos.GameRepository;
import org.cokeke.policysim.models.Round;
import org.cokeke.policysim.repos.RoundRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.cokeke.policysim.exceptions.GameNotFoundException;

@Service
public class RoundService {
    private final GameRepository gameRepo;
    private final RoundRepository roundRepo;

    @Autowired
    public RoundService(GameRepository gameRepository, RoundRepository roundRepository) {
        this.gameRepo = gameRepository;
        this.roundRepo = roundRepository;
    }

    public Round createRound(Long gameId, int currentMonth, String stateId, String stateName, double population, double productivity,
                            double publicCapital, double priceLevel, double lastInflation, double householdWealth,
                            double governmentDebt, double stability, double taxRate, double spendingShare,
                             double investmentShare, double transferShare) {
        Game game = gameRepo.findById(gameId).orElse(null);
        if (game == null) {
            throw new GameNotFoundException("Game not found");
        }
        Round round = new Round(currentMonth, stateId, stateName, population, productivity, publicCapital, priceLevel, lastInflation, householdWealth,
                                governmentDebt, stability, taxRate, spendingShare, investmentShare, transferShare, game);
        return roundRepo.save(round);
    }

    public Round getRoundById(Long id) {
        return roundRepo.findById(id).orElse(null);
    }

    public List<Round> getRoundsByGameId(Long gameId) {
        Game game = gameRepo.findById(gameId).orElse(null);
        if (game == null) {
            throw new GameNotFoundException("Game not found");
        }
        return roundRepo.findByGame_Id(gameId);
    }
}