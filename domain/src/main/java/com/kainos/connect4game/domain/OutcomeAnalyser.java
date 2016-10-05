package com.kainos.connect4game.domain;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OutcomeAnalyser {

    public static final int WINNING_NUMBER_OF_DISCS = 4;

    public Optional<Player.Colour> determineOutcome(Board board) {
        if (board.getLastPopulatedField() != null && (areDiscsConnectedInRow(board) || areDiscsConnectedInColumn(board))) {
            return Optional.of(board.getLastPopulatedField().getColour());
        }

        return Optional.empty();
    }

    private boolean areDiscsConnectedInRow(Board board) {
        List<Integer> columnRange = IntStream.rangeClosed(board.getLastPopulatedField().getColumn() - 3, board.getLastPopulatedField().getColumn() + 3).boxed()
                .collect(Collectors.toList());

        Integer counter = board.getFields().stream()
                .filter(field -> field.getRow() == board.getLastPopulatedField().getRow() && columnRange.contains(field.getColumn()))
                .sorted((f1, f2) -> Integer.compare(f1.getColumn(), f2.getColumn()))
                .collect(new FieldCollector(board.getLastPopulatedField().getColour()));

        return counter >= WINNING_NUMBER_OF_DISCS;
    }

    private boolean areDiscsConnectedInColumn(Board board) {
        List<Integer> rowRange = IntStream.rangeClosed(board.getLastPopulatedField().getRow() - 3, board.getLastPopulatedField().getRow() + 3).boxed()
                .collect(Collectors.toList());

        Integer counter = board.getFields().stream()
                .filter(field -> field.getColumn() == board.getLastPopulatedField().getColumn() && rowRange.contains(field.getColumn()))
                .sorted((f1, f2) -> Integer.compare(f1.getRow(), f2.getRow()))
                .collect(new FieldCollector(board.getLastPopulatedField().getColour()));

        return counter >= WINNING_NUMBER_OF_DISCS;
    }

    private class FieldCollector implements Collector<Board.Field, LongAdder, Integer> {

        private final Player.Colour colour;

        private FieldCollector(Player.Colour colour) {
            this.colour = colour;
        }

        @Override
        public Supplier<LongAdder> supplier() {
            return LongAdder::new;
        }

        @Override
        public BiConsumer<LongAdder, Board.Field> accumulator() {
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
