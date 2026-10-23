package org.cokeke.policysim.services;


/**
 * The session service is here to tighten lose ends between spring and server authentication
 * whilst implementing a feature I've been ignoring.
 * 
 * The flow is simple. The client clicks on create game, A game is created and so is the contract. 
 * The client gets the contract string, while spring saves the contractId. Then the user, still in the same creategame function, 
 * gives the paylyoad, with the contract, the users auth token and other client game init data to the engine server. 
 * The server then uses the contract service to verify the contract and extracts the contract claims, which include the contract id, gameId and userId.
 * 
 * Then the this is where the session service and controller come in. The session service will have access to the game repo. With the gameId,
 * it finds the associated game. When the game is found, the games contractId is cross checked with the server provided contractId.
 * If they match, then the session service will create a new session, employing org.cokeke.policysim.common.TokenGenerator to generate a token and refresh token for the server.
 * These tokens are tied to the session and then the endpoint, "/api/session" will create a new session and hand it back to the server.
 * Only at this point can the game actually start. Hoping all this can be done under a second and at most 1500ms.
 */

public class SessionService {
    
}
