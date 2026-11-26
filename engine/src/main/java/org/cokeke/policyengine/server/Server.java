package org.cokeke.policyengine.server;

import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.cokeke.policysimcontracts.GameContractService;
import org.cokeke.policyengine.common.EnvConfig;
import org.cokeke.policyengine.db.RedisDB;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Server extends WebSocketServer {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<WebSocket, GameConnectionHandler> handlers = new ConcurrentHashMap<>();
    private final GameContractService contractService;
    private final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
    .connectTimeout(Duration.ofSeconds(5)).build();
    private AtomicInteger SOCK_CONNS = new AtomicInteger(0);

    public Server(GameContractService svc) {
        super(new InetSocketAddress(8081), 16);
        this.contractService = svc;
        this.setConnectionLostTimeout(120);
    }

    @Override
    public void onStart() {
        System.out.println("Engine server started on port 8081 at: " + LocalDateTime.now().toString());
    }

    @Override
    public void onOpen(WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {
        System.out.println("Client connected");
        GameConnectionHandler handler = new GameConnectionHandler(this.contractService, this.mapper, this.client);
        handlers.put(conn, handler);
        SOCK_CONNS.getAndIncrement();
        System.out.println("Active connections: [" + SOCK_CONNS + "]");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        String type = null;
        try {            
            GameConnectionHandler handler = handlers.get(conn);

            if (handler == null) {
                conn.send("Connection handler not found");
                return;
            }
            JsonNode request = mapper.readTree(message);

            type = request.get("type").asText();
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
        } catch(Exception e) {
            e.printStackTrace();
            ObjectNode err = mapper.createObjectNode();
            err.put("type", type); 
            err.put("success", false);
            err.put("error", e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            conn.send(err.toString());
        }
    } 
    

    @Override
    public void onClose(
            WebSocket conn,
            int code,
            String reason,
            boolean remote) {
        handlers.remove(conn);
        SOCK_CONNS.getAndDecrement();
        System.out.println("Connection: [" + conn.toString().substring(0,20) + "] has been disconnected leaving, [Active Connections]: [" + SOCK_CONNS + "]");
        //System.out.println("Client disconnected because [" + reason + "]");
    }

    public void shutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                this.stop();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                this.client.close();
            }
        }));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();

        if (conn != null) {
            RequestService service = handlers.get(conn).getRequestService();
            if (service != null) service.cleanUp();
        }
    }


    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        String secret = EnvConfig.get("CONTRACTS_SECRET"); 

        GameContractService contractService = new GameContractService(
            Base64.getDecoder().decode(secret),
            mapper,
            EnvConfig.get("CONTRACTS_ISSUER"),
            EnvConfig.get("CONTRACTS_AUDIENCE"),
            300
        );

        RedisDB.init();
        new Server(contractService).start();  
    }
}