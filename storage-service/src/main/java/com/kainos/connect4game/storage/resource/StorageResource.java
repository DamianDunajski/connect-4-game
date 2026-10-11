package com.kainos.connect4game.storage.resource;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.storage.repository.GameRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/storage")
@Tag(name = "Connect 4", description = "Game Storage API")
public class StorageResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageResource.class);

    private final GameRepository gameRepository;

    public StorageResource(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @GetMapping("/game/{id}")
    @Operation(summary = "Get game by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game found"),
            @ApiResponse(responseCode = "404", description = "Game not found"),
            @ApiResponse(responseCode = "500", description = "Error occurred - game has not been retrieved")
    })
    public Game findGameById(@Parameter(name = "id", description = "ID of the game", required = true) @PathVariable("id") UUID id) {
        LOGGER.info("Finding game with ID: {}", id);
        return gameRepository.findGameById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));
    }

    @PostMapping("/game")
    @Operation(summary = "Store game (if game already exists, it will be updated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game has been stored"),
            @ApiResponse(responseCode = "400", description = "Invalid game data"),
            @ApiResponse(responseCode = "500", description = "Error occurred - game has not been stored")
    })
    public Game saveGame(@Parameter(name = "game", description = "The game to be stored", required = true) @NotNull @Valid @RequestBody Game game) {
        LOGGER.info("Saving game with ID: {}", game.id());
        return gameRepository.saveGame(game);
    }

}
