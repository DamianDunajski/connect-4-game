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
        if (board.getLastPopulatedField() != null && (areDiscsConnectedInRow(board) || areDiscsConnectedInColumn(board)
                || areDiscsConnectedDiagonalBottomTop(board) || areDiscsConnectedDiagonalTopBottom(board))) {
            return Optional.of(board.getLastPopulatedField().getColour());
        }

        return Optional.empty();
    }

    private boolean areDiscsConnectedInRow(Game.Board board) {
        Game.Board.Field.Location lastPopulatedLocation = board.getLastPopulatedField().getLocation();

        List<Game.Board.Field.Location> locationRange = new ArrayList<>(7);
        for (int column = lastPopulatedLocation.getColumn() - 3; column <= lastPopulatedLocation.getColumn() + 3; column++) {
            locationRange.add(new Game.Board.Field.Location(column, lastPopulatedLocation.getRow()));
        }

        Integer counter = board.getFields().stream()
                .filter(field -> locationRange.contains(field.getLocation()))
                .collect(new FieldCollector(board.getLastPopulatedField().getColour()));

        return counter >= WINNING_NUMBER_OF_DISCS;
    }

    private boolean areDiscsConnectedInColumn(Game.Board board) {
        Game.Board.Field.Location lastPopulatedLocation = board.getLastPopulatedField().getLocation();

        List<Game.Board.Field.Location> locationRange = new ArrayList<>(7);
        for (int row = lastPopulatedLocation.getRow() - 3; row <= lastPopulatedLocation.getRow() + 3; row++) {
            locationRange.add(new Game.Board.Field.Location(lastPopulatedLocation.getColumn(), row));
        }

        Integer counter = board.getFields().stream()
                .filter(field -> locationRange.contains(field.getLocation()))
                .collect(new FieldCollector(board.getLastPopulatedField().getColour()));

        return counter >= WINNING_NUMBER_OF_DISCS;
    }

    private boolean areDiscsConnectedDiagonalBottomTop(Game.Board board) {
        Game.Board.Field.Location lastPopulatedLocation = board.getLastPopulatedField().getLocation();

        int bottomRow = lastPopulatedLocation.getRow() + 3;

        List<Game.Board.Field.Location> locationRange = new ArrayList<>(7);
        for (int column = lastPopulatedLocation.getColumn() - 3; column <= lastPopulatedLocation.getColumn() + 3; column++) {
            locationRange.add(new Game.Board.Field.Location(column, bottomRow--));
        }

        Integer counter = board.getFields().stream()
                .filter(field -> locationRange.contains(field.getLocation()))
                .collect(new FieldCollector(board.getLastPopulatedField().getColour()));

        return counter >= WINNING_NUMBER_OF_DISCS;
    }

    private boolean areDiscsConnectedDiagonalTopBottom(Game.Board board) {
        Game.Board.Field.Location lastPopulatedLocation = board.getLastPopulatedField().getLocation();

        int topRow = lastPopulatedLocation.getRow() - 3;

        List<Game.Board.Field.Location> locationRange = new ArrayList<>(7);
        for (int column = lastPopulatedLocation.getColumn() - 3; column <= lastPopulatedLocation.getColumn() + 3; column++) {
            locationRange.add(new Game.Board.Field.Location(column, topRow++));
        }

        Integer counter = board.getFields().stream()
                .filter(field -> locationRange.contains(field.getLocation()))
                .collect(new FieldCollector(board.getLastPopulatedField().getColour()));

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
                if (field.getColour() == this.colour) {
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
