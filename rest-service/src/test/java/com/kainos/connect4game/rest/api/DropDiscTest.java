package com.kainos.connect4game.rest.api;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.domain.Game.Board;
import com.kainos.connect4game.domain.Player;
import com.kainos.connect4game.rest.api.base.BaseGameResourceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.WebApplicationException;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static jakarta.ws.rs.client.Entity.json;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DropDiscTest extends BaseGameResourceTest {

    private final Player firstPlayer = new Player("John", Player.Colour.Red);
    private final Player secondPlayer = new Player("Carl", Player.Colour.Yellow);

    private final Game existingGame = new Game(firstPlayer, secondPlayer);

    @BeforeEach
    void initGames() {
        games.put(existingGame.id(), existingGame);
    }

    @Test
    void shouldReturnUpdatedGameWithDroppedDiscReflectedOnTheBoard() {
        Game game = makeDropDiscRequest(existingGame.id(), firstPlayer.colour(), 0);

        assertThat(game.id()).isEqualTo(existingGame.id());
        assertThat(game.board().fields())
                .filteredOn(field -> field.location().column() == 0 && field.colour() != null)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("location.column", 0)
                .hasFieldOrPropertyWithValue("location.row", 5)
                .hasFieldOrPropertyWithValue("colour", Player.Colour.Red);
    }

    @Test
    void shouldReturn404ResponseWhenGameDoesNotExist() {
        assertThatThrownBy(() -> makeDropDiscRequest(randomUUID(), firstPlayer.colour(), 0))
                .isInstanceOf(WebApplicationException.class)
                .hasFieldOrPropertyWithValue("response.status", 404);
    }

    @Test
    void shouldReturn500ResponseWhenDiscIsBeingDroppedOutsideTheBoard() {
        assertThatThrownBy(() -> makeDropDiscRequest(existingGame.id(), firstPlayer.colour(), -1))
                .isInstanceOf(WebApplicationException.class)
                .hasFieldOrPropertyWithValue("response.status", 500);

        assertThatThrownBy(() -> makeDropDiscRequest(existingGame.id(), firstPlayer.colour(), 7))
                .isInstanceOf(WebApplicationException.class)
                .hasFieldOrPropertyWithValue("response.status", 500);
    }

    @Test
    void shouldReturn500ResponseWhenDiscIsBeingDroppedIntoFullColumn() {
        games.computeIfPresent(existingGame.id(), (_, game) -> {
            for (int i = 0; i < Board.NUMBER_OF_ROWS; i++) {
                game = game.dropDisc(Player.Colour.values()[i % 2], 0);
            }
            return game;
        });

        assertThatThrownBy(() -> makeDropDiscRequest(existingGame.id(), secondPlayer.colour(), 0))
                .isInstanceOf(WebApplicationException.class)
                .hasFieldOrPropertyWithValue("response.status", 500);
    }

    private Game makeDropDiscRequest(UUID id, Player.Colour colour, int column) {
        return resources.client().target(BASE_URL + "/" + id + "/drop/" + colour + "/column/" + column).request()
                .put(json(""), Game.class);
    }
}