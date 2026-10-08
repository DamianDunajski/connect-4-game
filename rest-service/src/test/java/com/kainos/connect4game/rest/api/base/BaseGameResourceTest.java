package com.kainos.connect4game.rest.api.base;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.rest.api.GameResource;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.client.RestTestClient;

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
            this.client = RestTestClient.bindToController(new GameResource(games)).build();
        }
    }

}
