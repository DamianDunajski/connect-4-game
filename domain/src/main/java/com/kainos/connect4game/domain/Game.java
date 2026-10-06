package com.kainos.connect4game.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kainos.connect4game.domain.Game.Board.Field.Location;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.*;
import java.util.stream.Collectors;
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
        @Schema(description = "Outcome of the game")
        Outcome outcome
) {

    private static final OutcomeAnalyser analyser = new OutcomeAnalyser();

    public Game(Player... players) {
        this(randomUUID(), new Board(), List.of(players), null);
    }

    public Game(UUID id, Board board, List<Player> players, Outcome outcome) {
        checkPlayers(players);
        this.id = id;
        this.board = board;
        this.players = players;
        this.outcome = outcome;
    }

    private void checkPlayers(List<Player> players) {
        if (players.size() > 2) {
            throw new IllegalStateException("Game cannot have more than 2 players");
        }
        Map<Player.Colour, List<Player>> playersByColour = players.stream().collect(Collectors.groupingBy(Player::colour));
        if (playersByColour.values().stream().anyMatch(groupedPlayers -> groupedPlayers.size() > 1)) {
            throw new IllegalStateException("Two players cannot choose the same colour");
        }
    }

    public Game addPlayer(Player player) {
        return new Game(
                id,
                board,
                Stream.concat(this.players.stream(), Stream.of(player)).toList(),
                outcome
        );
    }

    public Game dropDisc(Player.Colour colour, int column) {
        if (outcome != null) {
            throw new IllegalStateException("Game has already ended");
        }
        if (board.lastPopulatedField != null && board.lastPopulatedField.colour == colour) {
            throw new IllegalStateException("Single player cannot drop two discs in a row");
        }

        Board updatedBoard = board.dropDisc(colour, column);

        return analyser.determineOutcome(updatedBoard)
                .map(winningColour -> {
                    Player winner = players.stream()
                            .filter(player -> player.colour() == winningColour)
                            .findFirst()
                            .orElseThrow();
                    return new Game(
                            id,
                            updatedBoard,
                            players,
                            new Outcome(winner)
                    );
                }).orElseGet(() -> new Game(
                        id,
                        updatedBoard,
                        players,
                        null
                ));

    }

    public record Board(
            @Schema(description = "List of the fields on the board")
            List<Field> fields,
            @Schema(description = "Field populated by last player's move")
            Field lastPopulatedField
    ) {

        public static final int NUMBER_OF_COLUMNS = 7;
        public static final int NUMBER_OF_ROWS = 6;

        Board() {
            List<Field> fields = new ArrayList<>(NUMBER_OF_COLUMNS * NUMBER_OF_ROWS);
            for (int column = 0; column < NUMBER_OF_COLUMNS; column++) {
                for (int row = 0; row < NUMBER_OF_ROWS; row++) {
                    fields.add(new Field(new Location(column, row)));
                }
            }
            this(List.copyOf(fields), null);
        }

        Board dropDisc(Player.Colour colour, int column) {
            requireNonNull(colour, "Colour of the disc cannot be null");
            if (column < 0 || column >= NUMBER_OF_COLUMNS) {
                throw new IllegalArgumentException("Column " + column + " does not exist on the board");
            }

            OptionalInt lastOccupiedRow = findLastOccupiedRow(column);
            lastOccupiedRow.ifPresent((row) -> {
                if (row == 0) {
                    throw new IllegalStateException("Column " + column + " is already full");
                }
            });

            Location discLocation = findNextAvailableField(column, lastOccupiedRow).location;

            return new Board(
                    fields.stream().map(field ->
                            field.location().equals(discLocation)
                                    ? new Field(field.location(), colour)
                                    : field
                    ).toList(),
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

        private Field findNextAvailableField(int column, OptionalInt lastOccupiedRow) {
            return fields.stream()
                    .filter(field -> field.location.column == column && field.location.row == lastOccupiedRow.orElse(Board.NUMBER_OF_ROWS) - 1)
                    .findFirst()
                    .get();
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Field(
                @Schema(description = "Location of the field on the board")
                Location location,
                @Schema(description = "Colour of the field (null means field not filled)")
                Player.Colour colour
        ) {
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
    }
}
