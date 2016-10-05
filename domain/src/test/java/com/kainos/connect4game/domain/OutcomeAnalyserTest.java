package com.kainos.connect4game.domain;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static com.kainos.connect4game.domain.OutcomeAnalyserTest.Drop.redIntoColumn;
import static com.kainos.connect4game.domain.OutcomeAnalyserTest.Drop.yellowIntoColumn;

@RunWith(Parameterized.class)
public class OutcomeAnalyserTest {

    private static final OutcomeAnalyser analyser = new OutcomeAnalyser();

    @Parameterized.Parameter(0)
    public List<Drop> discDrops;
    @Parameterized.Parameter(1)
    public Optional<Player.Colour> outcome;

    @Parameterized.Parameters(name = "dropping {0} should result in {1}")
    public static Collection<Object[]> scenarios() {
        return Arrays.asList(new Object[][]{
                {newArrayList(), Optional.empty()},
                // discs connected in a row
                {newArrayList(redIntoColumn(0), redIntoColumn(1), redIntoColumn(2), redIntoColumn(3)), Optional.of(Player.Colour.Red)},
                {newArrayList(redIntoColumn(1), redIntoColumn(2), redIntoColumn(3), redIntoColumn(4)), Optional.of(Player.Colour.Red)},
                {newArrayList(redIntoColumn(2), redIntoColumn(3), redIntoColumn(4), redIntoColumn(5)), Optional.of(Player.Colour.Red)},
                {newArrayList(redIntoColumn(3), redIntoColumn(4), redIntoColumn(5), redIntoColumn(6)), Optional.of(Player.Colour.Red)},
                // discs connected in a column
                {newArrayList(redIntoColumn(0), redIntoColumn(0), redIntoColumn(0), redIntoColumn(0)), Optional.of(Player.Colour.Red)},
                {newArrayList(yellowIntoColumn(0), redIntoColumn(0), redIntoColumn(0), redIntoColumn(0), redIntoColumn(0)), Optional.of(Player.Colour.Red)},
                {newArrayList(yellowIntoColumn(0), yellowIntoColumn(0), redIntoColumn(0), redIntoColumn(0), redIntoColumn(0), redIntoColumn(0)), Optional.of(Player.Colour.Red)}
        });
    }

    @Test
    public void winningColourShouldMatchExpectation() {
        Board board = new Board();
        for (Drop drop : discDrops) {
            board.dropDisc(drop.colour, drop.column);
        }

        Assertions.assertThat(analyser.determineOutcome(board))
                .isEqualTo(outcome);
    }

    static class Drop {
        private final Player.Colour colour;
        private final int column;

        private Drop(Player.Colour colour, int column) {
            this.colour = colour;
            this.column = column;
        }

        static Drop redIntoColumn(int column) {
            return new Drop(Player.Colour.Red, column);
        }

        static Drop yellowIntoColumn(int column) {
            return new Drop(Player.Colour.Yellow, column);
        }

        @Override
        public String toString() {
            return String.format("%s disc into column %d", colour, column).toLowerCase();
        }
    }

}