package org.cokeke.policyengine.server;

import java.net.http.HttpClient;

import org.cokeke.policysimcontracts.GameContractService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GameConnectionHandler {
    private final RequestService requestService;

    public GameConnectionHandler(GameContractService svc, ObjectMapper mapper, HttpClient client) {
        this.requestService = new RequestService(svc, mapper, client);
    }

    public Object handle(String type, JsonNode payload) {
        return requestService.handle(type, payload);
    }

    public RequestService getRequestService() { return this.requestService; }
}