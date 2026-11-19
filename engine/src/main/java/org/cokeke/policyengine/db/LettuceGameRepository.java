package org.cokeke.policyengine.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.lettuce.core.RedisException;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.cokeke.policyengine.models.SpringRoundDTO;

public class LettuceGameRepository implements GameRepository {
    private final RedisCommands<String, String> redisCommands;
    private final ObjectMapper mapper;
    
    public LettuceGameRepository(RedisCommands<String, String> redisCommands) {
        this.redisCommands = redisCommands;
        this.mapper = new ObjectMapper();
    }

    @Override
    public GameObject getGame(Long gameId) {
        String key = "game:" + gameId + ":rounds";
        List<Round> listOut = new ArrayList<>();
        try {
            Map<String, String> months = redisCommands.hgetall(key);
            months.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(Comparator.comparingInt(Integer::parseInt)))
            .forEach(entry -> {
                try {
                    List<Round> roundsList = mapper.readValue(
                        entry.getValue(),
                        new TypeReference<List<Round>>() {}
                    );

                    listOut.addAll(roundsList);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });

            GameObject game = new GameObject(gameId, listOut);
            return game;
        } catch (RedisException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void saveRound(Long gameId, List<SpringRoundDTO> rounds) {
        List<Round> monthRounds = new ArrayList<>();
        int currentMonth = rounds.get(rounds.size() - 1).currentMonth();

        for (SpringRoundDTO round : rounds) {
            monthRounds.add(new Round(round));
        }

        try {
            String serializedRounds = mapper.writeValueAsString(monthRounds);

            String key = "game:" + gameId + ":rounds";

            redisCommands.hset(
                key,
                String.valueOf(currentMonth),
                serializedRounds
            );
        } catch (RedisException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override 
    public List<Round> getMonth(Long gameId, int currentMonth) {
        String key = "game:" + gameId + ":rounds";

        try {
            String months = redisCommands.hget(key, String.valueOf(currentMonth));
            List<Round> rounds = mapper.readValue(
                months,
                new TypeReference<List<Round>>() {}
            );
            return rounds;
        } catch (RedisException | JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override 
    public Long deleteGame(Long gameId) {
        String key = "game:" + gameId + ":rounds";

        try {
            Long count = redisCommands.del(key);

            if (count == 0) throw new RedisOperationFailedException("Deleting game with id: [" + gameId + "] failed."); 
            return count;
        } catch (RedisException e) {
            e.printStackTrace();
        }

        return -2L;
    }


    @Override 
    public boolean gameExists(Long gameId) {
        try {
            boolean exists = redisCommands.exists("game:" + gameId + ":rounds") > 0;
            return exists;
        } catch (RedisException e) {
            e.printStackTrace();
            throw new RedisOperationFailedException("Couldnt check if game with id: [" + gameId + "] exists.");
        }
    }
}
