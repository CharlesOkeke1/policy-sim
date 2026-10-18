package org.cokeke.policyengine.server;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.cokeke.policysimcontracts.GameContractService;
import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Server extends WebSocketServer {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<WebSocket, GameConnectionHandler> handlers = new ConcurrentHashMap<>();
    private final GameContractService contractService;


    public Server(GameContractService svc) {
        super(new InetSocketAddress(8081));
        this.contractService = svc;
    }

    @Override
    public void onStart() {
        System.out.println("Engine server started on port 8081 at: " + LocalDateTime.now().toString());
    }

    @Override
    public void onOpen(WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {
        System.out.println("Client connected");
        GameConnectionHandler handler = new GameConnectionHandler(this.contractService);
        handlers.put(conn, handler);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {            
            GameConnectionHandler handler = handlers.get(conn);

            if (handler == null) {
                conn.send("Connection handler not found");
                return;
            }
            JsonNode request = mapper.readTree(message);

            String type = request.get("type").asText();
            JsonNode payload = request.get("payload");

            Object result = handler.handle(type, payload);

            String response = mapper.writeValueAsString(
                Map.of(
                    "type", type,
                    "success", true,
                    "data", result == null
                        ? Collections.emptyMap()
                        : result
                )
            );

            conn.send(response);

        } catch (Exception e) {
            //conn.send("{\"success\":false,\"error\":\"Request failed\"}");
            conn.send(e.getMessage());
        }
    }

    @Override
    public void onClose(
            WebSocket conn,
            int code,
            String reason,
            boolean remote) {
        System.out.println("Client disconnected");
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        String secret = "fib6U1yiBuXQQ8j60kyccdg7YfLzGknvuNjHo4WfqVA=";

        GameContractService contractService = new GameContractService(
            Base64.getDecoder().decode(secret),
            mapper,
            "spring-backend",
            "game-engine",
            300
        );

        new Server(contractService).start();       
    }
}