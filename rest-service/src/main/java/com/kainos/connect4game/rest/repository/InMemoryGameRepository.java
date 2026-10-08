package com.kainos.connect4game.rest.repository;

import com.kainos.connect4game.domain.Game;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryGameRepository implements GameRepository {

    private final ConcurrentHashMap<UUID, Game> games = new ConcurrentHashMap<>();

    @Override
    public Optional<Game> findGameById(UUID id) {
        return Optional.ofNullable(games.get(id));
    }

    @Override
    public Game saveGame(Game game) {
        games.put(game.id(), game);
        return game;
    }
}
