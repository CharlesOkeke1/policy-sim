package org.cokeke.policysim.services;

import org.cokeke.policysimcontracts.GameContractService;
import org.springframework.stereotype.Service;

@Service
public class GameSessionService {
    private final GameContractService contractService;

    public GameSessionService(GameContractService contractService){
        this.contractService = contractService;
    }

    public GameContractService getContractService() { return this.contractService; }
}