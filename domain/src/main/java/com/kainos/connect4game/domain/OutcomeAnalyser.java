package com.kainos.connect4game.domain;

import java.util.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class OutcomeAnalyser {

    public static final int WINNING_NUMBER_OF_DISCS = 4;

    public Optional<Player.Colour> determineOutcome(Game.Board board) {
        if (board.lastPopulatedField() != null && (areDiscsConnectedInRow(board) || areDiscsConnectedInColumn(board)
                || areDiscsConnectedDiagonalBottomTop(board) || areDiscsConnectedDiagonalTopBottom(board))) {
            return Optional.of(board.lastPopulatedField().colour());
        }

        return Optional.empty();
    }

    private boolean areDiscsConnectedInRow(Game.Board board) {
        Game.Board.Field.Location lastPopulatedLocation = board.lastPopulatedField().location();

        List<Game.Board.Field.Location> locationRange = new ArrayList<>(7);
        for (int column = lastPopulatedLocation.column() - 3; column <= lastPopulatedLocation.column() + 3; column++) {
            locationRange.add(new Game.Board.Field.Location(column, lastPopulatedLocation.row()));
        }

        Integer counter = board.fields().stream()
                .filter(field -> locationRange.contains(field.location()))
                .collect(new FieldCollector(board.lastPopulatedField().colour()));

        return counter >= WINNING_NUMBER_OF_DISCS;
    }

    private boolean areDiscsConnectedInColumn(Game.Board board) {
        Game.Board.Field.Location lastPopulatedLocation = board.lastPopulatedField().location();

        List<Game.Board.Field.Location> locationRange = new ArrayList<>(7);
        for (int row = lastPopulatedLocation.row() - 3; row <= lastPopulatedLocation.row() + 3; row++) {
            locationRange.add(new Game.Board.Field.Location(lastPopulatedLocation.column(), row));
        }

        Integer counter = board.fields().stream()
                .filter(field -> locationRange.contains(field.location()))
                .collect(new FieldCollector(board.lastPopulatedField().colour()));

        return counter >= WINNING_NUMBER_OF_DISCS;
    }

    private boolean areDiscsConnectedDiagonalBottomTop(Game.Board board) {
        Game.Board.Field.Location lastPopulatedLocation = board.lastPopulatedField().location();

        int bottomRow = lastPopulatedLocation.row() + 3;

        List<Game.Board.Field.Location> locationRange = new ArrayList<>(7);
        for (int column = lastPopulatedLocation.column() - 3; column <= lastPopulatedLocation.column() + 3; column++) {
            locationRange.add(new Game.Board.Field.Location(column, bottomRow--));
        }

        Integer counter = board.fields().stream()
                .filter(field -> locationRange.contains(field.location()))
                .collect(new FieldCollector(board.lastPopulatedField().colour()));

        return counter >= WINNING_NUMBER_OF_DISCS;
    }

    private boolean areDiscsConnectedDiagonalTopBottom(Game.Board board) {
        Game.Board.Field.Location lastPopulatedLocation = board.lastPopulatedField().location();

        int topRow = lastPopulatedLocation.row() - 3;

        List<Game.Board.Field.Location> locationRange = new ArrayList<>(7);
        for (int column = lastPopulatedLocation.column() - 3; column <= lastPopulatedLocation.column() + 3; column++) {
            locationRange.add(new Game.Board.Field.Location(column, topRow++));
        }

        Integer counter = board.fields().stream()
                .filter(field -> locationRange.contains(field.location()))
                .collect(new FieldCollector(board.lastPopulatedField().colour()));

        return counter >= WINNING_NUMBER_OF_DISCS;
    }

    private class FieldCollector implements Collector<Game.Board.Field, LongAdder, Integer> {

        private final Player.Colour colour;

        private FieldCollector(Player.Colour colour) {
            this.colour = colour;
        }

        @Override
        public Supplier<LongAdder> supplier() {
            return LongAdder::new;
        }

        @Override
        public BiConsumer<LongAdder, Game.Board.Field> accumulator() {
            return (counter, field) -> {
                if (field.colour() == this.colour) {
                    counter.increment();
                } else if (counter.intValue() < WINNING_NUMBER_OF_DISCS) {
                    counter.reset();
                }
            };
        }

        @Override
        public BinaryOperator<LongAdder> combiner() {
            return (left, right) -> {
                left.add(right.longValue());
                return left;
            };
        }

        @Override
        public Function<LongAdder, Integer> finisher() {
            return LongAdder::intValue;
        }

        @Override
        public Set<Collector.Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }

}
