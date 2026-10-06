package com.kainos.connect4game.domain;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameTest {

    @Test
    void shouldThrowAnExceptionWhenSecondPlayerHasChosenTheSameColourAsFirstPlayer() {
        Game game = new Game();
        game.addPlayer(new Player("John", Player.Colour.Red));

        assertThatThrownBy(() -> game.addPlayer(new Player("Carl", Player.Colour.Red)))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("Two players cannot choose the same colour");
    }

    @Test
    void shouldThrowAnExceptionWhenThirdPlayerIsBeingAdded() {
        Game game = new Game();
        game.addPlayer(new Player("John", Player.Colour.Red));
        game.addPlayer(new Player("Carl", Player.Colour.Yellow));

        assertThatThrownBy(() -> game.addPlayer(new Player("Stephanie", Player.Colour.Yellow)))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("Game cannot have more then 2 players");
    }

    @Test
    void shouldThrowAnExceptionWhenTheSamePlayerIsMakingTwoConsecutiveDrops() {
        Player player = new Player("John", Player.Colour.Red);
        Game game = new Game(player);
        game.dropDisc(player.getColour(), 0);

        assertThatThrownBy(() -> game.dropDisc(player.getColour(), 0))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("Single player cannot drop two discs in a row");
    }

    @Test
    void shouldThrowAnExceptionWhenOutcomeHasBeenDeterminedButPlayersContinueDroppingDiscs() {
        Player firstPlayer = new Player("John", Player.Colour.Red);
        Player secondPlayer = new Player("Carl", Player.Colour.Yellow);
        Game game = new Game(firstPlayer, secondPlayer);
        game.dropDisc(firstPlayer.getColour(), 0);
        game.dropDisc(secondPlayer.getColour(), 0);
        game.dropDisc(firstPlayer.getColour(), 1);
        game.dropDisc(secondPlayer.getColour(), 1);
        game.dropDisc(firstPlayer.getColour(), 2);
        game.dropDisc(secondPlayer.getColour(), 2);
        game.dropDisc(firstPlayer.getColour(), 3);

        assertThatThrownBy(() -> game.dropDisc(secondPlayer.getColour(), 3))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("Game has already ended");
    }

    @Test
    void shouldHaveOutcomeWhenFourDiscsHasBeenConnected() {
        Player firstPlayer = new Player("John", Player.Colour.Red);
        Player secondPlayer = new Player("Carl", Player.Colour.Yellow);
        Game game = new Game(firstPlayer, secondPlayer);
        game.dropDisc(firstPlayer.getColour(), 0);
        game.dropDisc(secondPlayer.getColour(), 0);
        game.dropDisc(firstPlayer.getColour(), 1);
        game.dropDisc(secondPlayer.getColour(), 1);
        game.dropDisc(firstPlayer.getColour(), 2);
        game.dropDisc(secondPlayer.getColour(), 2);
        game.dropDisc(firstPlayer.getColour(), 3);

        assertThat(game.getOutcome())
                .isEqualTo(new Game.Outcome(firstPlayer));
    }

}