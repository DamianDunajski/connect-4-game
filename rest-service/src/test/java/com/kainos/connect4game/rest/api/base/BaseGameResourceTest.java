package com.kainos.connect4game.rest.api.base;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.rest.api.GameResource;
import com.kainos.connect4game.rest.api.GlobalExceptionHandler;
import com.kainos.connect4game.rest.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseGameResourceTest {

    public static final String BASE_URL = "/game/connect-4";

    protected final ConcurrentHashMap<UUID, Game> games = new ConcurrentHashMap<>();
    protected RestTestClient client;

    @BeforeEach
    public void init() {
        games.clear();
        if (this.client == null) {
            this.client = RestTestClient.bindToController(new GameResource(new GameRepository() {
                        @Override
                        public Optional<Game> findGameById(UUID id) {
                            return Optional.ofNullable(games.get(id));
                        }

                        @Override
                        public Game saveGame(Game game) {
                            games.put(game.id(), game);
                            return game;
                        }
                    }))
                    .configureServer(server -> {
                        server.setControllerAdvice(new GlobalExceptionHandler());
                    })
                    .build();
        }
    }

}
