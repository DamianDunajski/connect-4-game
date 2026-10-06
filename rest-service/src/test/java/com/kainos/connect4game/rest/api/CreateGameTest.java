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

        assertThat(firstGame.getId())
                .isNotEqualByComparingTo(secondGame.getId())
                .isNotNull();
    }

    @Test
    void shouldReturnGameWithProperlySizedBlankBoard() {
        Game game = makeCreateGameRequest(player);

        assertThat(game.getBoard().getFields())
                .hasSize(Board.NUMBER_OF_COLUMNS * Board.NUMBER_OF_ROWS)
                .filteredOn(field -> field.getColour() != null)
                .isEmpty();
    }

    @Test
    void shouldReturnGameWithFirstPlayerOnThePlayersList() {
        Game game = makeCreateGameRequest(player);

        assertThat(game.getPlayers()).containsOnly(player);
    }

    @Test
    void shouldAddCreatedGameToTheListOfGamesInProgress() {
        Game game = makeCreateGameRequest(player);

        assertThat(games).containsOnly(game);
    }

    private Game makeCreateGameRequest(Player player) {
        return resources.client().target(BASE_URL).request()
                .post(json(player), Game.class);
    }
}