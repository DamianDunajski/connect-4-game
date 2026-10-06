package com.kainos.connect4game.rest.api.base;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.rest.api.GameResource;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(DropwizardExtensionsSupport.class)
public abstract class BaseGameResourceTest {

    public static final String BASE_URL = "/game/connect-4";

    protected static final List<Game> games = new ArrayList<>();

    public static final ResourceExtension resources = ResourceExtension.builder()
            .addResource(new GameResource(games))
            .build();

    @BeforeEach
    public void init() {
        games.clear();
    }

}
