package org.cokeke.policysim.services;

import org.cokeke.policysim.models.Game;
import org.cokeke.policysim.repos.GameRepository;
import org.cokeke.policysim.models.Round;
import org.cokeke.policysim.responses.BatchRoundPushResponse;
import org.cokeke.policysim.repos.RoundRepository;
import org.cokeke.policysim.dtos.SpringRoundDTO;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.cokeke.policysim.exceptions.GameNotFoundException;

@Service
public class RoundService {
    private final GameRepository gameRepo;
    private final RoundRepository roundRepo;

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

    public Round saveRound(Round r) {
        return roundRepo.save(r);
    }

    public List<Round> getRoundsByGameId(Long gameId) {
        Game game = gameRepo.findById(gameId).orElse(null);
        if (game == null) {
            throw new GameNotFoundException("Game not found");
        }
        return roundRepo.findByGame_Id(gameId);
    }

    public BatchRoundPushResponse pushRoundsToDB(List<SpringRoundDTO> rounds, Long gameId) {
        Game game = gameRepo.findById(gameId).orElseThrow(() -> new GameNotFoundException("Game doesnt seem to exist"));
        for (SpringRoundDTO r : rounds) {
            Round round = new Round(r.currentMonth(), r.stateId(), r.stateName(), r.population(), r.productivity(),
            r.publicCapital(), r.priceLevel(), r.lastInflation(), r.householdWealth(), r.governmentDebt(),
            r.stability(), r.taxRate(), r.spendingShare(), r.investmentShare(), r.transferShare(), game);

            roundRepo.save(round);
        }

        BatchRoundPushResponse resp = new BatchRoundPushResponse(game.getId(), 
        rounds.get(0).stateName(), LocalDateTime.now());

        return resp;
    }
}