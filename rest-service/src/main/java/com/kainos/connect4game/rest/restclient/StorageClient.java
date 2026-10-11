package com.kainos.connect4game.rest.restclient;

import com.kainos.connect4game.domain.Game;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;
import java.util.UUID;

@Service
public class StorageClient {

    private final RestClient restClient;

    public StorageClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public Optional<Game> findGameById(UUID id) {
        try {
            return Optional.ofNullable(
                    restClient.get()
                            .uri("/storage/game/{id}", id)
                            .retrieve()
                            .body(Game.class)
            );
        } catch (HttpClientErrorException e) {
            return Optional.empty();
        }
    }

    public Game saveGame(Game game) {
        return restClient.post()
                .uri("/storage/game")
                .body(game)
                .retrieve()
                .body(Game.class);
    }

}
