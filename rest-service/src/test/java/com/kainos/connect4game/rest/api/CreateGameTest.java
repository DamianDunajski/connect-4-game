package com.kainos.connect4game.rest.api;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.domain.Game.Board;
import com.kainos.connect4game.domain.Player;
import com.kainos.connect4game.rest.api.base.BaseGameResourceTest;
import org.junit.jupiter.api.Test;

import static jakarta.ws.rs.client.Entity.json;
import static org.assertj.core.api.Assertions.assertThat;

class CreateGameTest extends BaseGameResourceTest {

    private final Player player = new Player("John", Player.Colour.Red);

    @Test
    void shouldReturnGamesWithUniqueIDs() {
        Game firstGame = makeCreateGameRequest(player);
        Game secondGame = makeCreateGameRequest(player);

        assertThat(firstGame.id())
                .isNotEqualByComparingTo(secondGame.id())
                .isNotNull();
    }

    @Test
    void shouldReturnGameWithProperlySizedBlankBoard() {
        Game game = makeCreateGameRequest(player);

        assertThat(game.board().fields())
                .hasSize(Board.NUMBER_OF_COLUMNS * Board.NUMBER_OF_ROWS)
                .filteredOn(field -> field.colour() != null)
                .isEmpty();
    }

    @Test
    void shouldReturnGameWithFirstPlayerOnThePlayersList() {
        Game game = makeCreateGameRequest(player);

        assertThat(game.players()).containsOnly(player);
    }

    @Test
    void shouldAddCreatedGameToTheListOfGamesInProgress() {
        Game game = makeCreateGameRequest(player);

        assertThat(games).containsValues(game);
    }

    private Game makeCreateGameRequest(Player player) {
        return resources.client().target(BASE_URL).request()
                .post(json(player), Game.class);
    }
}