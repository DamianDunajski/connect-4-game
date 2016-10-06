package com.kainos.connect4game.rest.api;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.domain.Game.Board;
import com.kainos.connect4game.domain.Player;
import com.kainos.connect4game.rest.api.base.BaseGameResourceTest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static javax.ws.rs.client.Entity.json;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DropDiscTest extends BaseGameResourceTest {

    private final Player firstPlayer = new Player("John", Player.Colour.Red);
    private final Player secondPlayer = new Player("Carl", Player.Colour.Yellow);

    private final Game existingGame = new Game(firstPlayer, secondPlayer);

    @Before
    public void initGames() {
        games.add(existingGame);
    }

    @Test
    public void shouldReturnUpdatedGameWithDroppedDiscReflectedOnTheBoard() {
        Game game = makeDropDiscRequest(existingGame.getId(), firstPlayer.getColour(), 0);

        assertThat(game.getId()).isEqualTo(existingGame.getId());
        assertThat(game.getBoard().getFields())
                .filteredOn(field -> field.getLocation().getColumn() == 0 && field.getColour() != null)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("location.column", 0)
                .hasFieldOrPropertyWithValue("location.row", 5)
                .hasFieldOrPropertyWithValue("colour", Player.Colour.Red);
    }

    @Test
    public void shouldReturn404ResponseWhenGameDoesNotExist() {
        assertThatThrownBy(() -> makeDropDiscRequest(randomUUID(), firstPlayer.getColour(), 0))
                .isInstanceOf(WebApplicationException.class)
                .hasFieldOrPropertyWithValue("response.status", 404);
    }

    @Test
    public void shouldReturn500ResponseWhenDiscIsBeingDroppedOutsideTheBoard() {
        assertThatThrownBy(() -> makeDropDiscRequest(existingGame.getId(), firstPlayer.getColour(), -1))
                .isInstanceOf(WebApplicationException.class)
                .hasFieldOrPropertyWithValue("response.status", 500);

        assertThatThrownBy(() -> makeDropDiscRequest(existingGame.getId(), firstPlayer.getColour(), 7))
                .isInstanceOf(WebApplicationException.class)
                .hasFieldOrPropertyWithValue("response.status", 500);
    }

    @Test
    public void shouldReturn500ResponseWhenDiscIsBeingDroppedIntoFullColumn() {
        for (int i = 0; i < Board.NUMBER_OF_ROWS; i++) {
            existingGame.dropDisc(Player.Colour.values()[i % 2], 0);
        }

        assertThatThrownBy(() -> makeDropDiscRequest(existingGame.getId(), secondPlayer.getColour(), 0))
                .isInstanceOf(WebApplicationException.class)
                .hasFieldOrPropertyWithValue("response.status", 500);
    }

    private Game makeDropDiscRequest(UUID id, Player.Colour colour, int column) {
        return resources.client().target(BASE_URL + "/" + id + "/drop/" + colour + "/column/" + column).request()
                .put(json(""), Game.class);
    }
}