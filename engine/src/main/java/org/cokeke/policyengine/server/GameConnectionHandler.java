package org.cokeke.policyengine.server;

import org.cokeke.policysimcontracts.GameContractService;
import com.fasterxml.jackson.databind.JsonNode;

public class GameConnectionHandler {
    private final RequestService requestService;

    public GameConnectionHandler(GameContractService svc) {
        this.requestService = new RequestService(svc);
    }

    public Object handle(String type, JsonNode payload) {
        return requestService.handle(type, payload);
    }
}