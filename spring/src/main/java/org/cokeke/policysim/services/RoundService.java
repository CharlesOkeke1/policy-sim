package org.cokeke.policysim.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.cokeke.policysim.dtos.RoundWithoutGame;
import org.cokeke.policysim.dtos.SpringRoundDTO;
import org.cokeke.policysim.exceptions.GameNotFoundException;
import org.cokeke.policysim.models.Game;
import org.cokeke.policysim.models.Round;
import org.cokeke.policysim.repos.GameRepository;
import org.cokeke.policysim.repos.RoundRepository;
import org.cokeke.policysim.responses.BatchRoundPushResponse;
import org.springframework.stereotype.Service;

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

    public List<RoundWithoutGame> getRoundsWithoutGameObjectByGameId(Long gameId) {
        Game game = gameRepo.findById(gameId).orElse(null);
        if (game == null) {
            throw new GameNotFoundException("Game not found");
        }
        return roundListToRoundListWithoutGameObject(roundRepo.findByGame_Id(gameId));
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

    public List<RoundWithoutGame> roundListToRoundListWithoutGameObject(List<Round> rounds) {
        List<RoundWithoutGame> out = new ArrayList<>();
        for (Round round : rounds) {
            RoundWithoutGame rwg = new RoundWithoutGame(round.getId(), round.getCurrentMonth(),
            round.getStateId(), round.getStateName(), round.getPopulation(), 
            round.getProductivity(), round.getPublicCapital(), round.getPriceLevel(), 
            round.getLastInflation(), round.getHouseholdWealth(), round.getGovernmentDebt(), 
            round.getStability(), round.getTaxRate(), round.getSpendingShare(), 
            round.getInvestmentShare(), round.getTransferShare());

            out.add(rwg);
        }

        return out;
    }
}