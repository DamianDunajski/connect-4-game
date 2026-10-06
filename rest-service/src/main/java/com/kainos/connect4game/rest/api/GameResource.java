package com.kainos.connect4game.rest.api;

import com.codahale.metrics.annotation.Timed;
import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.domain.Player;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Path("/game/connect-4")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Connect 4", description = "Game API")
public class GameResource {

    private final ConcurrentHashMap<UUID, Game> gamesInProgress;

    public GameResource(ConcurrentHashMap<UUID, Game> gamesInProgress) {
        this.gamesInProgress = gamesInProgress;
    }

    @Timed
    @POST
    @Operation(summary = "Create new game")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game has been created"),
            @ApiResponse(responseCode = "500", description = "Error occurred - game has not been created")
    })
    public Game createGame(@Parameter(name = "player", description = "Player who starts new game", required = true) @NotNull @Valid Player player) {
        Game game = new Game(player);
        this.gamesInProgress.put(game.id(), game);
        return game;
    }

    @Timed
    @PUT
    @Path("{id}/join")
    @Operation(summary = "Join existing game")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game has been joined"),
            @ApiResponse(responseCode = "404", description = "Game does not exist or has been already completed"),
            @ApiResponse(responseCode = "500", description = "Error occurred - game has not been joined")
    })
    public Game joinGame(@Parameter(name = "id", description = "ID of the game to join", required = true) @PathParam("id") UUID id,
                         @Parameter(name = "player", description = "Player who joins existing game", required = true) @NotNull @Valid Player player) {
        Game mutatedGame = this.gamesInProgress.computeIfPresent(id, (_, game) -> game.addPlayer(player));
        if (mutatedGame == null) {
            throw new WebApplicationException(404);
        }
        return mutatedGame;
    }

    @Timed
    @PUT
    @Path("{id}/drop/{colour}/column/{column}")
    @Operation(summary = "Drop colour disc into column")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disc has been dropped"),
            @ApiResponse(responseCode = "404", description = "Game does not exist or has been already completed"),
            @ApiResponse(responseCode = "500", description = "Error occurred - disc has not been dropped")
    })
    public Game dropDisc(@Parameter(name = "id", description = "ID of the game", required = true) @PathParam("id") UUID id,
                         @Parameter(name = "colour", description = "Colour of the disc being dropped", required = true) @PathParam("colour") Player.Colour colour,
                         @Parameter(name = "column", description = "Column the disc being dropped into", required = true) @PathParam("column") int column) {
        Game mutatedGame = gamesInProgress.computeIfPresent(id, (_, game) -> game.dropDisc(colour, column));
        if (mutatedGame == null) {
            throw new WebApplicationException(404);
        }
        return mutatedGame;
    }

}
