package com.kainos.connect4game.rest.repository;

import com.kainos.connect4game.domain.Game;

import java.util.Optional;
import java.util.UUID;

public interface GameRepository {

    Optional<Game> findGameById(UUID id);

    Game saveGame(Game game);

}
