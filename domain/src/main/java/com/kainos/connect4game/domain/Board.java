package com.kainos.connect4game.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Board {

    public static final int NUMBER_OF_COLUMNS = 7;
    public static final int NUMBER_OF_ROWS = 6;

    @ApiModelProperty(value = "List of the fields on the board", required = true)
    private List<Field> fields;

    Board() {
        this.fields = new ArrayList<>(NUMBER_OF_COLUMNS * NUMBER_OF_ROWS);

        for (int column = 0; column < NUMBER_OF_COLUMNS; column++) {
            for (int row = 0; row < NUMBER_OF_ROWS; row++) {
                this.fields.add(new Field(column, row));
            }
        }
    }

    public List<Field> getFields() {
        return Collections.unmodifiableList(fields);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Objects.equals(fields, board.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Field {

        // location
        @ApiModelProperty(value = "Number of column (starting with 0 - top left corner)", required = true)
        private int column;
        @ApiModelProperty(value = "Number of row (starting with 0 - top left corner)", required = true)
        private int row;
        // status
        @ApiModelProperty(value = "Colour of the field (null means field not filled)", required = false)
        private Player.Colour colour;

        @JsonCreator
        Field(@JsonProperty("column") int column,
                      @JsonProperty("row") int row) {
            this.column = column;
            this.row = row;
        }

        public int getColumn() {
            return column;
        }

        public int getRow() {
            return row;
        }

        public Player.Colour getColour() {
            return colour;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Field field = (Field) o;
            return column == field.column &&
                    row == field.row &&
                    colour == field.colour;
        }

        @Override
        public int hashCode() {
            return Objects.hash(column, row, colour);
        }
    }
}
