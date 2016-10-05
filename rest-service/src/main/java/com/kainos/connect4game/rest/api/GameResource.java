package com.kainos.connect4game.rest.api;

import com.codahale.metrics.annotation.Timed;
import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.domain.Player;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/game/connect-4")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Connect 4", description = "Game API")
public class GameResource {

    private List<Game> gamesInProgress;

    public GameResource(List<Game> gamesInProgress) {
        this.gamesInProgress = gamesInProgress;
    }

    @Timed
    @POST
    @ApiOperation(value = "Create new game")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Game has been created"),
            @ApiResponse(code = 500, message = "Error occurred - game has not been created")
    })
    public Game createGame(@ApiParam(name = "player", value = "Player who starts new game", required = true) @NotNull @Valid Player player) {
        Game game = new Game();
        game.addPlayer(player);

        this.gamesInProgress.add(game);

        return game;
    }

    @Timed
    @PUT
    @Path("{id}/join")
    @ApiOperation(value = "Join existing game")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Game has been joined"),
            @ApiResponse(code = 404, message = "Game does not exist or has been already completed"),
            @ApiResponse(code = 500, message = "Error occurred - game has not been joined")
    })
    public Game joinGame(@ApiParam(name = "id", value = "ID of the game to join", required = true) @PathParam("id") UUID id,
                         @ApiParam(name = "player", value = "Player who joins existing game", required = true) @NotNull @Valid Player player) {
        Game game = findGameByID(id).orElseThrow(() -> new WebApplicationException(404));
        game.addPlayer(player);
        return game;
    }

    @Timed
    @PUT
    @Path("{id}/drop/{colour}/column/{column}")
    @ApiOperation(value = "Drop colour disc into column")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Disc has been dropped"),
            @ApiResponse(code = 404, message = "Game does not exist or has been already completed"),
            @ApiResponse(code = 500, message = "Error occurred - disc has not been dropped")
    })
    public Game dropDisc(@ApiParam(name = "id", value = "ID of the game", required = true) @PathParam("id") UUID id,
                         @ApiParam(name = "colour", value = "Colour of the disc being dropped", required = true) @PathParam("colour") Player.Colour colour,
                         @ApiParam(name = "column", value = "Column the disc being dropped into", required = true) @PathParam("column") int column) {
        Game game = findGameByID(id).orElseThrow(() -> new WebApplicationException(404));
        game.dropDisc(colour, column);
        return game;
    }


    private Optional<Game> findGameByID(UUID id) {
        return this.gamesInProgress.stream().filter(game -> game.getId().equals(id)).findFirst();
    }

}
