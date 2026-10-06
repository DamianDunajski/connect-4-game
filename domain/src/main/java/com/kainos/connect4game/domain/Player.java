package com.kainos.connect4game.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import static java.util.Objects.requireNonNull;

public record Player(
        @Schema(description = "Name of the player", example = "John")
        String name,
        @Schema(description = "Colour selected by the player", example = "Red")
        Colour colour
) {

    public Player(String name, Colour colour) {
        requireNonNull(name, "Player name cannot be null");
        requireNonNull(colour, "Player colour cannot be null");
        this.name = name;
        this.colour = colour;
    }

    public enum Colour {
        Red, Yellow;
    }
}
