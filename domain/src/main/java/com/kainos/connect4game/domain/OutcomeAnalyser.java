package com.kainos.connect4game.domain;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OutcomeAnalyser {

    public static final int WINNING_NUMBER_OF_DISCS = 4;

    public Optional<Player.Colour> determineOutcome(Game.Board board) {
        if (board.lastPopulatedField() instanceof Game.Board.Field(var location, var colour)
                && isWinningMove(board, location, colour)) {
            return Optional.of(colour);
        }

        return Optional.empty();
    }

    private boolean isWinningMove(Game.Board board, Game.Board.Field.Location location, Player.Colour colour) {
        return Stream.of(
                areDiscsConnectedInRow(board, location, colour),
                areDiscsConnectedInColumn(board, location, colour),
                areDiscsConnectedDiagonalBottomTop(board, location, colour),
                areDiscsConnectedDiagonalTopBottom(board, location, colour)
        ).anyMatch(Boolean::booleanValue);
    }

    private boolean areDiscsConnectedInRow(Game.Board board, Game.Board.Field.Location location, Player.Colour colour) {
        return hasWinningSequence(board, colour,
                col -> new Game.Board.Field.Location(col, location.row()),
                location.column());
    }

    private boolean areDiscsConnectedInColumn(Game.Board board, Game.Board.Field.Location location, Player.Colour colour) {
        return hasWinningSequence(board, colour,
                row -> new Game.Board.Field.Location(location.column(), row),
                location.row());
    }

    private boolean areDiscsConnectedDiagonalBottomTop(Game.Board board, Game.Board.Field.Location location, Player.Colour colour) {
        return hasWinningSequence(board, colour,
                i -> new Game.Board.Field.Location(location.column() + i, location.row() - i),
                0);
    }

    private boolean areDiscsConnectedDiagonalTopBottom(Game.Board board, Game.Board.Field.Location location, Player.Colour colour) {
        return hasWinningSequence(board, colour,
                i -> new Game.Board.Field.Location(location.column() + i, location.row() + i),
                0);
    }

    private boolean hasWinningSequence(Game.Board board, Player.Colour colour,
                                       Function<Integer, Game.Board.Field.Location> locationGenerator,
                                       int centerIndex) {
        Map<Game.Board.Field.Location, Player.Colour> boardMap = board.fields().stream()
                .filter(f -> f.colour() != null)
                .collect(Collectors.toMap(Game.Board.Field::location, Game.Board.Field::colour));

        return IntStream.rangeClosed(-3, 3)
                .mapToObj(i -> locationGenerator.apply(centerIndex + i))
                .map(boardMap::get)
                .gather(Gatherers.windowSliding(WINNING_NUMBER_OF_DISCS))
                .anyMatch(window -> window.stream().allMatch(c -> c == colour));
    }

}
