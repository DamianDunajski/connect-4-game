package com.kainos.connect4game.rest.api;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.domain.Player;
import com.kainos.connect4game.domain.Player.Colour;
import com.kainos.connect4game.rest.api.base.BaseGameResourceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.WebApplicationException;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static jakarta.ws.rs.client.Entity.json;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        Game game = makeJoinGameRequest(existingGame.id(), secondPlayer);

        assertThat(game.id()).isEqualTo(existingGame.id());
        assertThat(game.players()).containsOnly(firstPlayer, secondPlayer);
    }

    @Test
    void shouldReturn404ResponseWhenGameDoesNotExist() {
        assertThatThrownBy(() -> makeJoinGameRequest(randomUUID(), secondPlayer))
                .isInstanceOf(WebApplicationException.class)
                .hasFieldOrPropertyWithValue("response.status", 404);
    }

    @Test
    void shouldReturn500ResponseWhenSecondPlayerChoosesTheSameColour() {
        assertThatThrownBy(() -> makeJoinGameRequest(existingGame.id(), new Player("Carl", Colour.Red)))
                .isInstanceOf(WebApplicationException.class)
                .hasFieldOrPropertyWithValue("response.status", 500);
    }

    @Test
    void shouldReturn500ResponseWhenThirdPlayerJoins() {
        games.computeIfPresent(existingGame.id(), (_, game) -> game.addPlayer(secondPlayer));

        assertThatThrownBy(() -> makeJoinGameRequest(existingGame.id(), new Player("Stephanie", Colour.Yellow)))
                .isInstanceOf(WebApplicationException.class)
                .hasFieldOrPropertyWithValue("response.status", 500);
    }

    private Game makeJoinGameRequest(UUID id, Player player) {
        return resources.client().target(BASE_URL + "/" + id + "/join").request()
                .put(json(player), Game.class);
    }
}