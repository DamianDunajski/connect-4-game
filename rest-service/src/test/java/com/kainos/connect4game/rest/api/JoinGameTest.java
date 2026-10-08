package com.kainos.connect4game.rest.api;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.domain.Player;
import com.kainos.connect4game.domain.Player.Colour;
import com.kainos.connect4game.rest.api.base.BaseGameResourceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.assertj.RestTestClientResponse;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

class JoinGameTest extends BaseGameResourceTest {

    private final Player firstPlayer = new Player("John", Colour.Red);
    private final Player secondPlayer = new Player("Carl", Colour.Yellow);

    private final Game existingGame = new Game(firstPlayer);

    @BeforeEach
    void initGames() {
        games.put(existingGame.id(), existingGame);
    }

    @Test
    void shouldReturnUpdatedGameWithSecondPlayerOnThePlayersList() {
        assertThat(makeJoinGameRequest(existingGame.id(), secondPlayer))
                .hasStatus(HttpStatus.OK)
                .bodyJson().convertTo(Game.class).satisfies(game -> {
                    assertThat(game.id()).isEqualTo(existingGame.id());
                    assertThat(game.players()).containsOnly(firstPlayer, secondPlayer);
                });
    }

    @Test
    void shouldReturn404ResponseWhenGameDoesNotExist() {
        assertThat(makeJoinGameRequest(randomUUID(), secondPlayer))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn409ResponseWhenSecondPlayerChoosesTheSameColour() {
        assertThat(makeJoinGameRequest(existingGame.id(), new Player("Carl", Colour.Red)))
                .hasStatus(HttpStatus.CONFLICT);
    }

    @Test
    void shouldReturn409ResponseWhenThirdPlayerJoins() {
        games.computeIfPresent(existingGame.id(), (_, game) -> game.addPlayer(secondPlayer));

        assertThat(makeJoinGameRequest(existingGame.id(), new Player("Stephanie", Colour.Yellow)))
                .hasStatus(HttpStatus.CONFLICT);
    }

    private RestTestClientResponse makeJoinGameRequest(UUID id, Player player) {
        return RestTestClientResponse.from(
                client.put()
                        .uri("%s/%s/join".formatted(BASE_URL, id))
                        .body(player)
                        .exchange()
        );
    }
}