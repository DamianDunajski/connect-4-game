package com.kainos.connect4game.rest.api;

import com.kainos.connect4game.domain.Game;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Before;
import org.junit.ClassRule;

import java.util.ArrayList;
import java.util.List;

public class BaseGameResourceTest {

    public static final String BASE_URL = "/game/connect-4";

    protected static final List<Game> games = new ArrayList<>();

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new GameResource(games))
            .build();

    @Before
    public void init() {
        games.clear();
    }

}
