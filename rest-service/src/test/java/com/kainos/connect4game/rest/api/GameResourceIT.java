package com.kainos.connect4game.rest.api;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.domain.Player;
import com.kainos.connect4game.rest.api.base.BaseGameResourceIT;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;

import javax.ws.rs.client.Client;

import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.client.Entity.text;
import static org.assertj.core.api.Assertions.assertThat;

public class GameResourceIT extends BaseGameResourceIT {

    private final Client client = new JerseyClientBuilder().build();

    private final Player redPlayer = new Player("John", Player.Colour.Red);
    private final Player yellowPlayer = new Player("Carl", Player.Colour.Yellow);

    @Test
    public void gameShouldEndWhenPlayerConnectsFourDiscs() {
        // John creates game
        Game game = makeCreateGameRequest(redPlayer);
        assertThat(game.getPlayers()).hasSize(1);

        // Carl joins the game
        game = makeJoinGameRequest(game, yellowPlayer);
        assertThat(game.getPlayers()).hasSize(2);

        // Players play the game
        game = makeDropDiscRequest(game, yellowPlayer.getColour(), 3);
        assertThat(game.getOutcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.getColour(), 2);
        assertThat(game.getOutcome()).isNull();
        game = makeDropDiscRequest(game, yellowPlayer.getColour(), 4);
        assertThat(game.getOutcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.getColour(), 5);
        assertThat(game.getOutcome()).isNull();
        game = makeDropDiscRequest(game, yellowPlayer.getColour(), 3);
        assertThat(game.getOutcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.getColour(), 3);
        assertThat(game.getOutcome()).isNull();
        game = makeDropDiscRequest(game, yellowPlayer.getColour(), 4);
        assertThat(game.getOutcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.getColour(), 2);
        assertThat(game.getOutcome()).isNull();
        game = makeDropDiscRequest(game, yellowPlayer.getColour(), 5);
        assertThat(game.getOutcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.getColour(), 1);
        assertThat(game.getOutcome()).isNull();
        game = makeDropDiscRequest(game, yellowPlayer.getColour(), 6);
        assertThat(game.getOutcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.getColour(), 6);
        assertThat(game.getOutcome()).isNull();
        game = makeDropDiscRequest(game, yellowPlayer.getColour(), 2);
        assertThat(game.getOutcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.getColour(), 6);
        assertThat(game.getOutcome()).isNull();
        game = makeDropDiscRequest(game, yellowPlayer.getColour(), 3);
        assertThat(game.getOutcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.getColour(), 4);
        assertThat(game.getOutcome()).isNull();
        game = makeDropDiscRequest(game, yellowPlayer.getColour(), 5);
        assertThat(game.getOutcome()).isNull();
        game = makeDropDiscRequest(game, redPlayer.getColour(), 4);
        assertThat(game.getOutcome().getWinner()).isEqualTo(redPlayer);
    }

    private Game makeCreateGameRequest(Player player) {
        return client.target(String.format("http://localhost:%d/game/connect-4", RULE.getLocalPort())).request().post(json(player), Game.class);
    }

    private Game makeJoinGameRequest(Game game, Player player) {
        return client.target(String.format("http://localhost:%d/game/connect-4/%s/join", RULE.getLocalPort(), game.getId())).request().put(json(player), Game.class);
    }

    private Game makeDropDiscRequest(Game game, Player.Colour colour, int column) {
        return client.target(String.format("http://localhost:%d/game/connect-4/%s/drop/%s/column/%s", RULE.getLocalPort(), game.getId(), colour, column)).request().put(text(""), Game.class);
    }

}
