package com.kainos.connect4game.rest.api;

import com.codahale.metrics.annotation.Timed;
import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.domain.Player;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
    @ApiOperation(value = "Create game", code = 204)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Game has been created"),
            @ApiResponse(code = 500, message = "Error occurred - game has not been created")
    })
    public Game createGame(@ApiParam(name = "player", value = "First player in the game", required = true) @NotNull @Valid Player player) {
        Game game = new Game();
        game.addPlayer(player);

        this.gamesInProgress.add(game);

        return game;
    }
}
