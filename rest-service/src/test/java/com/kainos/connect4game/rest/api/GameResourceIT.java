package com.kainos.connect4game.rest.api;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.domain.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameResourceIT  {

    @LocalServerPort
    private int port;
    private RestTestClient client;

    private final Player redPlayer = new Player("John", Player.Colour.Red);
    private final Player yellowPlayer = new Player("Carl", Player.Colour.Yellow);

    @BeforeEach
    void setup() {
        if (client == null) {
            client = RestTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
        }
    }

    @Test
    void gameShouldEndWhenPlayerConnectsFourDiscs() {
        // John creates game
        var game = makeCreateGameRequest(redPlayer);
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
        return client.post().uri("/game/connect-4").body(player).exchange()
                .expectStatus().isOk()
                .expectBody(Game.class).returnResult().getResponseBody();
    }

    private Game makeJoinGameRequest(Game game, Player player) {
        return client.put().uri("/game/connect-4/%s/join".formatted(game.id())).body(player).exchange()
                .expectStatus().isOk()
                .expectBody(Game.class).returnResult().getResponseBody();
    }

    private Game makeDropDiscRequest(Game game, Player.Colour colour, int column) {
        return client.put().uri("/game/connect-4/%s/drop/%s/column/%s".formatted(game.id(), colour, column)).exchange()
                .expectStatus().isOk()
                .expectBody(Game.class).returnResult().getResponseBody();
    }

}
