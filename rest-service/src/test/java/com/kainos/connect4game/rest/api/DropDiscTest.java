package com.kainos.connect4game.rest.api;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.domain.Game.Board;
import com.kainos.connect4game.domain.Player;
import com.kainos.connect4game.rest.api.base.BaseGameResourceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.assertj.RestTestClientResponse;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

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
        var game = assertThat(makeDropDiscRequest(existingGame.id(), firstPlayer.colour(), 0))
                .hasStatus(HttpStatus.OK)
                .bodyJson().convertTo(Game.class).actual();

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
        assertThat(makeDropDiscRequest(randomUUID(), firstPlayer.colour(), 0))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn400ResponseWhenDiscIsBeingDroppedOutsideTheBoard() {
        assertThat(makeDropDiscRequest(existingGame.id(), firstPlayer.colour(), -1))
                .hasStatus(HttpStatus.BAD_REQUEST);

        assertThat(makeDropDiscRequest(existingGame.id(), firstPlayer.colour(), 7))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturn409ResponseWhenDiscIsBeingDroppedIntoFullColumn() {
        games.computeIfPresent(existingGame.id(), (_, game) -> {
            for (int i = 0; i < Board.NUMBER_OF_ROWS; i++) {
                game = game.dropDisc(Player.Colour.values()[i % 2], 0);
            }
            return game;
        });

        assertThat(makeDropDiscRequest(existingGame.id(), secondPlayer.colour(), 0))
                .hasStatus(HttpStatus.CONFLICT);
    }

    private RestTestClientResponse makeDropDiscRequest(UUID id, Player.Colour colour, int column) {
        return RestTestClientResponse.from(
                client.put()
                        .uri("%s/%s/drop/%s/column/%d".formatted(BASE_URL, id, colour, column))
                        .exchange()
        );
    }
}