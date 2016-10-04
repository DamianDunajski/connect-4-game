package com.kainos.connect4game.rest.api;

import com.kainos.connect4game.domain.Board;
import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.domain.Player;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static com.kainos.connect4game.rest.utils.DomainUtils.player;
import static javax.ws.rs.client.Entity.entity;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateGameTest extends BaseGameResourceTest {

    private final Player player = player("John", Player.Colour.Red);

    @Test
    public void shouldReturnGamesWithUniqueIDs() {
        Game firstGame = makeCreateGameRequest(player);
        Game secondGame = makeCreateGameRequest(player);

        assertThat(firstGame.getId())
                .isNotEqualByComparingTo(secondGame.getId())
                .isNotNull();
    }

    @Test
    public void shouldReturnGameWithProperlySizedBlankBoard() {
        Game game = makeCreateGameRequest(player);

        assertThat(game.getBoard().getFields())
                .hasSize(Board.NUMBER_OF_COLUMNS * Board.NUMBER_OF_ROWS)
                .filteredOn(field -> field.getColour() != null)
                .isEmpty();
    }

    @Test
    public void shouldReturnGameWithFirstPlayerOnThePlayersList() {
        Game game = makeCreateGameRequest(player);

        assertThat(game.getPlayers()).containsOnly(player);
    }

    @Test
    public void shouldAddCreatedGameToTheListOfGamesInProgress() {
        Game game = makeCreateGameRequest(player);

        assertThat(games).containsOnly(game);
    }

    private Game makeCreateGameRequest(Player player) {
        return resources.client().target(BASE_URL).request()
                .post(entity(player, MediaType.APPLICATION_JSON_TYPE), Game.class);
    }
}