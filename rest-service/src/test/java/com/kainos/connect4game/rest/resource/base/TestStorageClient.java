package com.kainos.connect4game.rest.resource.base;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.rest.restclient.StorageClient;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TestStorageClient extends StorageClient {

    private final ConcurrentHashMap<UUID, Game> games;

    public TestStorageClient(ConcurrentHashMap<UUID, Game> games) {
        super(null); // ignored
        this.games = games;
    }

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
