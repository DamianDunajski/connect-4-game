package com.kainos.connect4game.rest.api;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.domain.Game.Board;
import com.kainos.connect4game.domain.Player;
import com.kainos.connect4game.rest.api.base.BaseGameResourceTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.assertj.RestTestClientResponse;

import static org.assertj.core.api.Assertions.assertThat;

class CreateGameTest extends BaseGameResourceTest {

    private final Player player = new Player("John", Player.Colour.Red);

    @Test
    void shouldReturnGamesWithUniqueIDs() {
        var firstGame = assertThat(makeCreateGameRequest(player))
                .hasStatus(HttpStatus.OK)
                .bodyJson().convertTo(Game.class).actual();
        var secondGame = assertThat(makeCreateGameRequest(player))
                .hasStatus(HttpStatus.OK)
                .bodyJson().convertTo(Game.class).actual();

        assertThat(firstGame.id())
                .isNotEqualByComparingTo(secondGame.id())
                .isNotNull();
    }

    @Test
    void shouldReturnGameWithProperlySizedBlankBoard() {
        var game = assertThat(makeCreateGameRequest(player))
                .hasStatus(HttpStatus.OK)
                .bodyJson().convertTo(Game.class).actual();

        assertThat(game.board().fields())
                .hasSize(Board.NUMBER_OF_COLUMNS * Board.NUMBER_OF_ROWS)
                .filteredOn(field -> field.colour() != null)
                .isEmpty();
    }

    @Test
    void shouldReturnGameWithFirstPlayerOnThePlayersList() {
        var game = assertThat(makeCreateGameRequest(player))
                .hasStatus(HttpStatus.OK)
                .bodyJson().convertTo(Game.class).actual();

        assertThat(game.players()).containsOnly(player);
    }

    @Test
    void shouldAddCreatedGameToTheListOfGamesInProgress() {
        var game = assertThat(makeCreateGameRequest(player))
                .hasStatus(HttpStatus.OK)
                .bodyJson().convertTo(Game.class).actual();

        assertThat(games).containsValues(game);
    }

    private RestTestClientResponse makeCreateGameRequest(Player player) {
        return RestTestClientResponse.from(
                client.post()
                        .uri(BASE_URL)
                        .body(player)
                        .exchange()
        );
    }
}