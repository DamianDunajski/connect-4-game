package com.kainos.connect4game.domain;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BoardTest {

    @Test
    public void droppedDiscMustOccupyNextAvailableSpaceInTheColumn() throws Exception {
        Board board = new Board();

        board.dropDisc(Player.Colour.Red, 0);
        assertThat(board.getFields())
                .filteredOn(field -> field.getColumn() == 0 && field.getColour() != null)
                .containsOnly(new Board.Field(0, 5, Player.Colour.Red));

        board.dropDisc(Player.Colour.Yellow, 0);
        assertThat(board.getFields())
                .filteredOn(field -> field.getColumn() == 0 && field.getColour() != null)
                .containsOnly(new Board.Field(0, 4, Player.Colour.Yellow), new Board.Field(0, 5, Player.Colour.Red));
    }

    @Test
    public void colourOfTheDiscBeingDroppedCannotBeNull() throws Exception {
        Board board = new Board();

        Assertions.assertThatThrownBy(() -> board.dropDisc(null, 0))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Colour of the disc cannot be null");
    }

    @Test
    public void discCannotBeDroppedOutsideTheBoard() throws Exception {
        Board board = new Board();

        Assertions.assertThatThrownBy(() -> board.dropDisc(Player.Colour.Red, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column -1 does not exist on the board");

        Assertions.assertThatThrownBy(() -> board.dropDisc(Player.Colour.Red, 7))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column 7 does not exist on the board");
    }

    @Test
    public void discCannotBeDroppedIntoFullColumn() throws Exception {
        Board board = new Board();

        for (int i = 0; i < Board.NUMBER_OF_ROWS; i++) {
            board.dropDisc(Player.Colour.Red, 0);
        }

        Assertions.assertThatThrownBy(() -> board.dropDisc(Player.Colour.Yellow, 0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Column 0 is already full");
    }
}