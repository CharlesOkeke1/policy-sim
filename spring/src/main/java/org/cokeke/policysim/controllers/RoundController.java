package org.cokeke.policysim.controllers;

import java.util.List;

import org.cokeke.policysim.common.ApiResponse;
import org.cokeke.policysim.dtos.RoundWithoutGame;
import org.cokeke.policysim.models.Round;
import org.cokeke.policysim.requests.BatchRoundUploadRequest;
import org.cokeke.policysim.requests.RoundRequest;
import org.cokeke.policysim.responses.BatchRoundPushResponse;
import org.cokeke.policysim.responses.RoundResponse;
import org.cokeke.policysim.services.RoundService;
import org.cokeke.policysim.exceptions.RoundNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Round", description = "Handles all round endpoints for mangaging game round data")

public class RoundController {
    private final RoundService roundService;

    public RoundController(RoundService roundService) {
        this.roundService = roundService;
    }

    @GetMapping("/api/games/{gameId}/rounds")
    public ResponseEntity<ApiResponse<List<RoundWithoutGame>>> getGameRounds(@PathVariable Long gameId) {
        List<RoundWithoutGame> rounds = roundService.getRoundsWithoutGameObjectByGameId(gameId);
        return ResponseEntity.ok(ApiResponse.ok(rounds));
    }

    @PostMapping("/api/games/{gameId}/rounds")
    public ResponseEntity<ApiResponse<RoundResponse>> createRound(@PathVariable Long gameId, @RequestBody RoundRequest req) {
        Round round = roundService.createRound(gameId, req.currentMonth(), req.stateId(), req.stateName(), req.population(), req.productivity(),
        req.publicCapital(), req.priceLevel(), req.lastInflation(), req.householdWealth(), req.governmentDebt(), req.stability(),
        req.taxRate(), req.spendingShare(), req.investmentShare(), req.transferShare(), req.output(), req.outputGap(),
        req.unemployment(), req.federalTax(), req.allocation(), req.spending(), req.transfers(), req.interest(), req.policy(),
        req.federalResourcePrice(), req.nationalNetWorth(), req.federalTaxShare(), req.roundScore(), req.roundRank());

        RoundResponse roundResponse = new RoundResponse(round.getId(), round.getCurrentMonth(), round.getGame().getId(),
        round.getStateId(), round.getStateName(), round.getPopulation(), round.getProductivity(), round.getPublicCapital(),
        round.getPriceLevel(), round.getLastInflation(), round.getHouseholdWealth(), round.getGovernmentDebt(),
        round.getStability(), round.getTaxRate(), round.getSpendingShare(), round.getInvestmentShare(), round.getTransferShare(),
        round.getOutput(), round.getOutputGap(), round.getUnemployment(), round.getFederalTax(), round.getAllocation(),
        round.getSpending(), round.getTransfers(), round.getInterest(), round.getPolicy(), round.getFederalResourcePrice(),
        round.getNationalNetWorth(), round.getFederalTaxShare(), round.getRoundScore(), round.getRoundRank());

        return ResponseEntity.ok(ApiResponse.ok(roundResponse));
    }

    @GetMapping("/api/games/rounds/{roundId}")
    public ResponseEntity<ApiResponse<RoundResponse>> getRound(@PathVariable Long roundId) {
        Round round = roundService.getRoundById(roundId);
        if (round == null) {
            throw new RoundNotFoundException("Round with id: [" + roundId + "] does not exist.");
        }
        RoundResponse roundResponse = new RoundResponse(round.getId(), round.getCurrentMonth(), round.getGame().getId(),
        round.getStateId(), round.getStateName(), round.getPopulation(), round.getProductivity(), round.getPublicCapital(),
        round.getPriceLevel(), round.getLastInflation(), round.getHouseholdWealth(), round.getGovernmentDebt(),
        round.getStability(), round.getTaxRate(), round.getSpendingShare(), round.getInvestmentShare(), round.getTransferShare(),
        round.getOutput(), round.getOutputGap(), round.getUnemployment(), round.getFederalTax(), round.getAllocation(),
        round.getSpending(), round.getTransfers(), round.getInterest(), round.getPolicy(), round.getFederalResourcePrice(),
        round.getNationalNetWorth(), round.getFederalTaxShare(), round.getRoundScore(), round.getRoundRank());

        return ResponseEntity.ok(ApiResponse.ok(roundResponse));
    }

    @PostMapping("/api/games/rounds/batchUpload")
    public ResponseEntity<ApiResponse<BatchRoundPushResponse>> batchUploadRounds(@RequestBody BatchRoundUploadRequest req) {
        BatchRoundPushResponse res = roundService.pushRoundsToDB(req.rounds(), req.gameId());
        ApiResponse<BatchRoundPushResponse> apiResponse = ApiResponse.ok(res);

        return ResponseEntity.ok(apiResponse);
    }
}