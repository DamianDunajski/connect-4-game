package com.kainos.connect4game.rest.api;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.domain.Player;
import com.kainos.connect4game.domain.Player.Colour;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import java.util.UUID;

import static com.kainos.connect4game.rest.utils.DomainUtils.game;
import static com.kainos.connect4game.rest.utils.DomainUtils.player;
import static java.util.UUID.randomUUID;
import static javax.ws.rs.client.Entity.json;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JoinGameTest extends BaseGameResourceTest {

    private final Player firstPlayer = player("John", Colour.Red);
    private final Player secondPlayer = player("Carl", Colour.Yellow);

    private final Game existingGame = game(firstPlayer);

    @Before
    public void initGames() {
        games.add(existingGame);
    }

    @Test
    public void shouldReturnUpdatedGameWithSecondPlayerOnThePlayersList() {
        Game game = makeJoinGameRequest(existingGame.getId(), secondPlayer);

        assertThat(game.getId()).isEqualTo(existingGame.getId());
        assertThat(game.getPlayers()).containsOnly(firstPlayer, secondPlayer);
    }

    @Test
    public void shouldReturn404ResponseWhenGameDoesNotExist() {
        assertThatThrownBy(() -> makeJoinGameRequest(randomUUID(), secondPlayer))
                .isInstanceOf(WebApplicationException.class)
                .hasFieldOrPropertyWithValue("response.status", 404);
    }

    @Test
    public void shouldReturn500ResponseWhenSecondPlayerChoosesTheSameColour() {
        assertThatThrownBy(() -> makeJoinGameRequest(existingGame.getId(), player("Carl", Colour.Red)))
                .isInstanceOf(WebApplicationException.class)
                .hasFieldOrPropertyWithValue("response.status", 500);
    }

    @Test
    public void shouldReturn500ResponseWhenThirdPlayerJoins() {
        existingGame.addPlayer(secondPlayer);

        assertThatThrownBy(() -> makeJoinGameRequest(existingGame.getId(), player("Stephanie", Colour.Yellow)))
                .isInstanceOf(WebApplicationException.class)
                .hasFieldOrPropertyWithValue("response.status", 500);
    }

    private Game makeJoinGameRequest(UUID id, Player player) {
        return resources.client().target(BASE_URL + "/" + id + "/join").request()
                .put(json(player), Game.class);
    }
}