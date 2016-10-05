package com.kainos.connect4game.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;
import static java.util.UUID.randomUUID;

public class Game {

    @ApiModelProperty(value = "Unique ID of the game", required = true)
    private final UUID id;
    @ApiModelProperty(value = "Board used in the game", required = true)
    private final Board board;
    @ApiModelProperty(value = "List of players in the game", required = true)
    private final List<Player> players;
    //
    private Board.Field lastPopulatedField;

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

    public void addPlayer(Player player) {
        synchronized (players) {
            checkState(players.size() < 2, "Game cannot have more then 2 players");
            checkState(players.stream().noneMatch(p -> p.getColour().equals(player.getColour())), "Two players cannot choose the same colour");

            players.add(player);
        }
    }

    public void dropDisc(Player.Colour colour, int column) {
        synchronized (this) {
            checkState(lastPopulatedField == null || lastPopulatedField.getColour() != colour, "Single player cannot drop two discs in a row");

            lastPopulatedField = board.dropDisc(colour, column);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(id, game.id) &&
                Objects.equals(board, game.board) &&
                Objects.equals(players, game.players);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, board, players);
    }
}
