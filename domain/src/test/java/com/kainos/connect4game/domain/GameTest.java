package com.kainos.connect4game.domain;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameTest {

    @Test
    void shouldThrowAnExceptionWhenSecondPlayerHasChosenTheSameColourAsFirstPlayer() {
        Game game = new Game().addPlayer(new Player("John", Player.Colour.Red));

        assertThatThrownBy(() -> game.addPlayer(new Player("Carl", Player.Colour.Red)))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("Two players cannot choose the same colour");
    }

    @Test
    void shouldThrowAnExceptionWhenThirdPlayerIsBeingAdded() {
        Game game = new Game()
                .addPlayer(new Player("John", Player.Colour.Red))
                .addPlayer(new Player("Carl", Player.Colour.Yellow));

        assertThatThrownBy(() -> game.addPlayer(new Player("Stephanie", Player.Colour.Yellow)))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("Game cannot have more than 2 players");
    }

    @Test
    void shouldThrowAnExceptionWhenTheSamePlayerIsMakingTwoConsecutiveDrops() {
        Player player = new Player("John", Player.Colour.Red);
        Game game = new Game(player).dropDisc(player.colour(), 0);

        assertThatThrownBy(() -> game.dropDisc(player.colour(), 0))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("Single player cannot drop two discs in a row");
    }

    @Test
    void shouldThrowAnExceptionWhenOutcomeHasBeenDeterminedButPlayersContinueDroppingDiscs() {
        Player firstPlayer = new Player("John", Player.Colour.Red);
        Player secondPlayer = new Player("Carl", Player.Colour.Yellow);
        Game game = new Game(firstPlayer, secondPlayer)
                .dropDisc(firstPlayer.colour(), 0)
                .dropDisc(secondPlayer.colour(), 0)
                .dropDisc(firstPlayer.colour(), 1)
                .dropDisc(secondPlayer.colour(), 1)
                .dropDisc(firstPlayer.colour(), 2)
                .dropDisc(secondPlayer.colour(), 2)
                .dropDisc(firstPlayer.colour(), 3);

        assertThatThrownBy(() -> game.dropDisc(secondPlayer.colour(), 3))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("Game has already ended");
    }

    @Test
    void shouldHaveOutcomeWhenFourDiscsHasBeenConnected() {
        Player firstPlayer = new Player("John", Player.Colour.Red);
        Player secondPlayer = new Player("Carl", Player.Colour.Yellow);
        Game game = new Game(firstPlayer, secondPlayer)
                .dropDisc(firstPlayer.colour(), 0)
                .dropDisc(secondPlayer.colour(), 0)
                .dropDisc(firstPlayer.colour(), 1)
                .dropDisc(secondPlayer.colour(), 1)
                .dropDisc(firstPlayer.colour(), 2)
                .dropDisc(secondPlayer.colour(), 2)
                .dropDisc(firstPlayer.colour(), 3);

        assertThat(game.outcome())
                .isEqualTo(new Game.Outcome(firstPlayer));
    }

}