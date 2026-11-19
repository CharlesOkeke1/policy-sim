package org.cokeke.policysim.dtos;

public record ResumeGameSvcRes(Long gameId, int seed, 
String difficulty, int noOfRounds, Long userId, String engineToken) {}