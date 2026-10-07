package com.kainos.connect4game.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.kainos.connect4game.domain.OutcomeAnalyserTest.Drop.redIntoColumn;
import static com.kainos.connect4game.domain.OutcomeAnalyserTest.Drop.yellowIntoColumn;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class OutcomeAnalyserTest {

    private static final OutcomeAnalyser analyser = new OutcomeAnalyser();

    public static Collection<Arguments> scenarios() {
        return List.of(
                arguments(List.of(), Optional.empty()),
                // discs connected in a row
                arguments(
                        List.of(redIntoColumn(0), redIntoColumn(1), redIntoColumn(2), redIntoColumn(3)),
                        Optional.of(Player.Colour.Red)
                ),
                arguments(
                        List.of(redIntoColumn(1), redIntoColumn(2), redIntoColumn(3), redIntoColumn(4)),
                        Optional.of(Player.Colour.Red)
                ),
                arguments(
                        List.of(redIntoColumn(2), redIntoColumn(3), redIntoColumn(4), redIntoColumn(5)),
                        Optional.of(Player.Colour.Red)
                ),
                arguments(
                        List.of(redIntoColumn(3), redIntoColumn(4), redIntoColumn(5), redIntoColumn(6)),
                        Optional.of(Player.Colour.Red)
                ),
                // discs connected in a column
                arguments(
                        List.of(redIntoColumn(0), redIntoColumn(0), redIntoColumn(0), redIntoColumn(0)),
                        Optional.of(Player.Colour.Red)
                ),
                arguments(
                        List.of(yellowIntoColumn(0), redIntoColumn(0), redIntoColumn(0), redIntoColumn(0),
                                redIntoColumn(0)),
                        Optional.of(Player.Colour.Red)
                ),
                arguments(
                        List.of(yellowIntoColumn(0), yellowIntoColumn(0), redIntoColumn(0), redIntoColumn(0),
                                redIntoColumn(0), redIntoColumn(0)),
                        Optional.of(Player.Colour.Red)
                ),
                // discs connected diagonal (bottom - top)
                arguments(
                        List.of(redIntoColumn(0), yellowIntoColumn(1), redIntoColumn(2), yellowIntoColumn(3),
                                redIntoColumn(1), yellowIntoColumn(2), redIntoColumn(3), redIntoColumn(2),
                                yellowIntoColumn(3), redIntoColumn(3)),
                        Optional.of(Player.Colour.Red)
                ),
                // discs connected diagonal (top - bottom)
                arguments(
                        List.of(yellowIntoColumn(0), redIntoColumn(1), yellowIntoColumn(2), redIntoColumn(3),
                                redIntoColumn(0), yellowIntoColumn(1), redIntoColumn(2), yellowIntoColumn(0),
                                redIntoColumn(1), redIntoColumn(0)),
                        Optional.of(Player.Colour.Red)
                )
        );
    }

    @ParameterizedTest(name = "dropping {0} should result in {1}")
    @MethodSource("scenarios")
    void winningColourShouldMatchExpectation(List<Drop> discDrops, Optional<Player.Colour> outcome) {
        Game.Board board = new Game.Board();
        for (Drop drop : discDrops) {
            board = board.dropDisc(drop.colour, drop.column);
        }

        Assertions.assertThat(analyser.determineOutcome(board))
                .isEqualTo(outcome);
    }

    record Drop(Player.Colour colour, int column) {

        static Drop redIntoColumn(int column) {
            return new Drop(Player.Colour.Red, column);
        }

        static Drop yellowIntoColumn(int column) {
            return new Drop(Player.Colour.Yellow, column);
        }

        @Override
        public String toString() {
            return "%s disc into column %d".formatted(colour, column).toLowerCase();
        }
    }

}