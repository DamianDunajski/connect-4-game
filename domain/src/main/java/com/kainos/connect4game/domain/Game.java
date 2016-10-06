package com.kainos.connect4game.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.kainos.connect4game.domain.Game.Board.Field.Location;
import io.swagger.annotations.ApiModelProperty;

import java.util.*;

import static com.google.common.base.Preconditions.*;
import static java.util.UUID.randomUUID;

public class Game {

    private static final OutcomeAnalyser analyser = new OutcomeAnalyser();

    @ApiModelProperty(value = "Unique ID of the game", required = true)
    private final UUID id;
    @ApiModelProperty(value = "Board used in the game", required = true)
    private final Board board;
    @ApiModelProperty(value = "List of players in the game", required = true)
    private final List<Player> players;
    @ApiModelProperty(value = "Outcome of the game")
    private Outcome outcome;

    public Game(Player... players) {
        this(randomUUID(), new Board(), new ArrayList<Player>());

        for (Player player : players) {
            addPlayer(player);
        }
    }

    public Game(@JsonProperty("id") UUID id, @JsonProperty("board") Board board, @JsonProperty("players") List<Player> players) {
        this.id = id;
        this.board = board;
        this.players = players;
    }

    public UUID getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public void addPlayer(Player player) {
        synchronized (players) {
            checkState(players.size() < 2, "Game cannot have more then 2 players");
            checkState(players.stream().noneMatch(p -> p.getColour().equals(player.getColour())), "Two players cannot choose the same colour");

            players.add(player);
        }
    }

    public void dropDisc(Player.Colour colour, int column) {
        synchronized (this) {
            checkState(board.getLastPopulatedField() == null || board.getLastPopulatedField().getColour() != colour, "Single player cannot drop two discs in a row");
            checkState(outcome == null, "Game has already ended");

            board.dropDisc(colour, column);

            analyser.determineOutcome(board).ifPresent((winningColour) -> {
                Player winner = players.stream()
                        .filter(player -> player.getColour() == winningColour)
                        .findFirst()
                        .get();

                outcome = new Outcome(winner);
            });
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(id, game.id) &&
                Objects.equals(board, game.board) &&
                Objects.equals(players, game.players) &&
                Objects.equals(outcome, game.outcome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, board, players, outcome);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("board", board)
                .add("players", players)
                .add("outcome", outcome)
                .toString();
    }

    public static class Board {

        public static final int NUMBER_OF_COLUMNS = 7;
        public static final int NUMBER_OF_ROWS = 6;

        @ApiModelProperty(value = "List of the fields on the board", required = true)
        private final List<Field> fields;
        @ApiModelProperty(value = "Field populated by last player's move")
        private Field lastPopulatedField;

        Board() {
            this.fields = new ArrayList<>(NUMBER_OF_COLUMNS * NUMBER_OF_ROWS);

            for (int column = 0; column < NUMBER_OF_COLUMNS; column++) {
                for (int row = 0; row < NUMBER_OF_ROWS; row++) {
                    this.fields.add(new Field(new Location(column, row)));
                }
            }
        }

        public List<Field> getFields() {
            return Collections.unmodifiableList(fields);
        }

        public Field getLastPopulatedField() {
            return lastPopulatedField;
        }

        Field dropDisc(Player.Colour colour, int column) {
            synchronized (this) {
                checkNotNull(colour, "Colour of the disc cannot be null");
                checkArgument(column >= 0 && column < NUMBER_OF_COLUMNS, "Column " + column + " does not exist on the board");

                OptionalInt lastOccupiedRow = findLastOccupiedRow(column);

                lastOccupiedRow.ifPresent((row) -> checkState(row != 0, "Column " + column + " is already full"));

                Field nextAvailableField = findNextAvailableField(column, lastOccupiedRow);
                nextAvailableField.colour = colour;

                return lastPopulatedField = nextAvailableField;
            }
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Board board = (Board) o;
            return Objects.equals(fields, board.fields) &&
                    Objects.equals(lastPopulatedField, board.lastPopulatedField);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fields, lastPopulatedField);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("fields", fields)
                    .add("lastPopulatedField", lastPopulatedField)
                    .toString();
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Field {

            @ApiModelProperty(value = "Location of the field on the board", required = true)
            private Location location;
            @ApiModelProperty(value = "Colour of the field (null means field not filled)")
            private Player.Colour colour;

            Field(Location location) {
                this(location, null);
            }

            @JsonCreator
            Field(@JsonProperty("location") Location location, @JsonProperty("colour") Player.Colour colour) {
                this.location = location;
                this.colour = colour;
            }

            public Location getLocation() {
                return location;
            }

            public Player.Colour getColour() {
                return colour;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Field field = (Field) o;
                return Objects.equals(location, field.location) &&
                        colour == field.colour;
            }

            @Override
            public int hashCode() {
                return Objects.hash(location, colour);
            }

            @Override
            public String toString() {
                return MoreObjects.toStringHelper(this)
                        .add("location", location)
                        .add("colour", colour)
                        .toString();
            }

            public static class Location {

                @ApiModelProperty(value = "Number of column (starting with 0 - top left corner)", required = true)
                private int column;
                @ApiModelProperty(value = "Number of row (starting with 0 - top left corner)", required = true)
                private int row;

                @JsonCreator
                Location(@JsonProperty("column") int column, @JsonProperty("row") int row) {
                    this.column = column;
                    this.row = row;
                }

                public int getColumn() {
                    return column;
                }

                public int getRow() {
                    return row;
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    Location location = (Location) o;
                    return column == location.column &&
                            row == location.row;
                }

                @Override
                public int hashCode() {
                    return Objects.hash(column, row);
                }

                @Override
                public String toString() {
                    return MoreObjects.toStringHelper(this)
                            .add("column", column)
                            .add("row", row)
                            .toString();
                }
            }
        }
    }

    public static class Outcome {

        @ApiModelProperty(value = "Player who won the game", notes = "Draw is represented as an outcome without winner (winner is null)")
        private final Player winner;

        @JsonCreator
        Outcome(@JsonProperty("winner") Player winner) {
            this.winner = winner;
        }

        public Player getWinner() {
            return winner;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Outcome outcome = (Outcome) o;
            return Objects.equals(winner, outcome.winner);
        }

        @Override
        public int hashCode() {
            return Objects.hash(winner);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("winner", winner)
                    .toString();
        }
    }
}
