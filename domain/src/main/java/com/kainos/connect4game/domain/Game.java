package com.kainos.connect4game.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kainos.connect4game.domain.Game.Board.Field.Location;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;

public record Game(
        @Schema(description = "Unique ID of the game")
        UUID id,
        @Schema(description = "Board used in the game")
        Board board,
        @Schema(description = "List of players in the game")
        List<Player> players,
        @Schema(description = "Outcome of the game", nullable = true)
        Outcome outcome
) {

    private static final OutcomeAnalyser analyser = new OutcomeAnalyser();

    public Game {
        checkPlayers(players);
        requireNonNull(id);
        requireNonNull(board);
        players = List.copyOf(players);
    }

    public Game(Player... players) {
        this(randomUUID(), new Board(), List.of(players), null);
    }

    private void checkPlayers(List<Player> players) {
        if (players.size() > 2) {
            throw new IllegalStateException("Game cannot have more than 2 players");
        }
        long uniqueColours = players.stream().map(Player::colour).distinct().count();
        if (uniqueColours != players.size()) {
            throw new IllegalStateException("Two players cannot choose the same colour");
        }
    }

    public Game addPlayer(Player player) {
        return new Game(id, board, Stream.concat(players.stream(), Stream.of(player)).toList(), outcome);
    }

    public Game dropDisc(Player.Colour colour, int column) {
        if (outcome != null) {
            throw new IllegalStateException("Game has already ended");
        }
        if (players.size() < 2) {
            throw new IllegalStateException("Game requires 2 players to start");
        }
        if (players.stream().noneMatch(p -> p.colour() == colour)) {
            throw new IllegalArgumentException("Player with colour " + colour + " is not in this game");
        }
        if (board.lastPopulatedField() instanceof Board.Field(_, var lastColour) && lastColour == colour) {
            throw new IllegalStateException("Single player cannot drop two discs in a row");
        }

        Board updatedBoard = board.dropDisc(colour, column);

        Outcome outcome = analyser.determineOutcome(updatedBoard)
                .flatMap(winningColour ->
                        players.stream()
                                .filter(player -> player.colour() == winningColour)
                                .findFirst()
                )
                .map(Outcome::new)
                .orElse(null);

        return new Game(id, updatedBoard, players, outcome);

    }

    public record Board(
            @Schema(description = "List of the fields on the board")
            List<Field> fields,
            @Schema(description = "Field populated by last player's move", nullable = true)
            Field lastPopulatedField
    ) {

        public static final int NUMBER_OF_COLUMNS = 7;
        public static final int NUMBER_OF_ROWS = 6;

        public Board {
            fields = List.copyOf(requireNonNull(fields));
        }

        Board() {
            this(initialFields(), null);
        }

        private static List<Field> initialFields() {
            List<Field> fields = new ArrayList<>(NUMBER_OF_COLUMNS * NUMBER_OF_ROWS);
            for (int row = 0; row < NUMBER_OF_ROWS; row++) {
                for (int col = 0; col < NUMBER_OF_COLUMNS; col++) {
                    fields.add(new Field(new Location(col, row)));
                }
            }
            return fields;
        }

        Board dropDisc(Player.Colour colour, int column) {
            requireNonNull(colour, "Colour of the disc cannot be null");
            if (column < 0 || column >= NUMBER_OF_COLUMNS) {
                throw new IllegalArgumentException("Column " + column + " does not exist on the board");
            }

            OptionalInt lastOccupiedRow = findLastOccupiedRow(column);
            if (lastOccupiedRow.isPresent() && lastOccupiedRow.getAsInt() == 0) {
                throw new IllegalStateException("Column " + column + " is already full");
            }

            Location discLocation = findNextAvailableField(column, lastOccupiedRow.orElse(Board.NUMBER_OF_ROWS)).location;

            return new Board(
                    fields.stream()
                            .map(field -> field.location().equals(discLocation)
                                    ? new Field(field.location(), colour)
                                    : field
                            )
                            .toList(),
                    new Field(
                            discLocation,
                            colour
                    )
            );
        }

        private OptionalInt findLastOccupiedRow(int column) {
            return fields.stream()
                    .filter(field -> field.location.column == column && field.colour != null)
                    .mapToInt(field -> field.location.row)
                    .min();
        }

        private Field findNextAvailableField(int column, int lastOccupiedRow) {
            return fields.stream()
                    .filter(field -> field.location.column == column && field.location.row == lastOccupiedRow - 1)
                    .findFirst()
                    .orElseThrow();
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Field(
                @Schema(description = "Location of the field on the board")
                Location location,
                @Schema(description = "Colour of the field (null means field not filled)", nullable = true)
                Player.Colour colour
        ) {

            public Field {
                requireNonNull(location);
            }

            Field(Location location) {
                this(location, null);
            }

            public record Location(
                    @Schema(description = "Number of column (starting with 0 - top left corner)")
                    int column,
                    @Schema(description = "Number of row (starting with 0 - top left corner)")
                    int row
            ) {
            }
        }
    }

    public record Outcome(
            @Schema(description = "Player who won the game (draw is represented as an outcome without winner (winner is null))")
            Player winner
    ) {

        public Outcome {
            requireNonNull(winner);
        }
    }
}
