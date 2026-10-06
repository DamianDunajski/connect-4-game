package com.kainos.connect4game.rest.api;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.domain.Player;
import com.kainos.connect4game.rest.api.base.BaseGameResourceIT;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.client.Client;

import static jakarta.ws.rs.client.Entity.json;
import static jakarta.ws.rs.client.Entity.text;
import static org.assertj.core.api.Assertions.assertThat;

class GameResourceIT extends BaseGameResourceIT {

    private final Client client = new JerseyClientBuilder().build();

    private final Player redPlayer = new Player("John", Player.Colour.Red);
    private final Player yellowPlayer = new Player("Carl", Player.Colour.Yellow);

    @Test
    void gameShouldEndWhenPlayerConnectsFourDiscs() {
        // John creates game
        Game game = makeCreateGameRequest(redPlayer);
        assertThat(game.players()).hasSize(1);

        // Carl joins the game
        game = makeJoinGameRequest(game, yellowPlayer);
        assertThat(game.players()).hasSize(2);

        // Players play the game
        game = makeDropDiscRequest(game, yellowPlayer.colour(), 3);
        assertThat(game.outcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.colour(), 2);
        assertThat(game.outcome()).isNull();
        game = makeDropDiscRequest(game, yellowPlayer.colour(), 4);
        assertThat(game.outcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.colour(), 5);
        assertThat(game.outcome()).isNull();
        game = makeDropDiscRequest(game, yellowPlayer.colour(), 3);
        assertThat(game.outcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.colour(), 3);
        assertThat(game.outcome()).isNull();
        game = makeDropDiscRequest(game, yellowPlayer.colour(), 4);
        assertThat(game.outcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.colour(), 2);
        assertThat(game.outcome()).isNull();
        game = makeDropDiscRequest(game, yellowPlayer.colour(), 5);
        assertThat(game.outcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.colour(), 1);
        assertThat(game.outcome()).isNull();
        game = makeDropDiscRequest(game, yellowPlayer.colour(), 6);
        assertThat(game.outcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.colour(), 6);
        assertThat(game.outcome()).isNull();
        game = makeDropDiscRequest(game, yellowPlayer.colour(), 2);
        assertThat(game.outcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.colour(), 6);
        assertThat(game.outcome()).isNull();
        game = makeDropDiscRequest(game, yellowPlayer.colour(), 3);
        assertThat(game.outcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.colour(), 4);
        assertThat(game.outcome()).isNull();
        game = makeDropDiscRequest(game, yellowPlayer.colour(), 5);
        assertThat(game.outcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.colour(), 4);
        assertThat(game.outcome().winner()).isEqualTo(redPlayer);
    }

    private Game makeCreateGameRequest(Player player) {
        return client.target(String.format("http://localhost:%d/game/connect-4", RULE.getLocalPort())).request().post(json(player), Game.class);
    }

    private Game makeJoinGameRequest(Game game, Player player) {
        return client.target(String.format("http://localhost:%d/game/connect-4/%s/join", RULE.getLocalPort(), game.id())).request().put(json(player), Game.class);
    }

    private Game makeDropDiscRequest(Game game, Player.Colour colour, int column) {
        return client.target(String.format("http://localhost:%d/game/connect-4/%s/drop/%s/column/%s", RULE.getLocalPort(), game.id(), colour, column)).request().put(text(""), Game.class);
    }

}
