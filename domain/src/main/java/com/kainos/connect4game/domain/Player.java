package com.kainos.connect4game.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class Player {

    @NotNull(message = "Player name cannot be null")
    @ApiModelProperty(value = "Name of the player", required = true, example = "John")
    private final String name;
    @NotNull(message = "Player colour cannot be null")
    @ApiModelProperty(value = "Colour selected by the player", required = true, example = "Red")
    private final Colour colour;

    @JsonCreator
    public Player(@JsonProperty("name") String name, @JsonProperty("colour") Colour colour) {
        checkNotNull(name, "Player name cannot be null");
        checkNotNull(colour, "Player colour cannot be null");

        this.name = name;
        this.colour = colour;
    }

    public String getName() {
        return name;
    }

    public Colour getColour() {
        return colour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(name, player.name) &&
                colour == player.colour;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, colour);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("colour", colour)
                .toString();
    }

    public enum Colour {
        Red, Yellow;
    }
}
