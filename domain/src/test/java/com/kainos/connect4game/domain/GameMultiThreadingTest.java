package com.kainos.connect4game.domain;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

public class GameMultiThreadingTest {

    private final Game game = new Game();

    @Test
    public void concurrentPlayerAdditionsShouldNotExceedPlayersLimit() {
        int playersCount = 4;

        ExecutorService executorService = Executors.newFixedThreadPool(playersCount / 2);
        CountDownLatch countDownLatch = new CountDownLatch(playersCount);

        for (int i = 0; i < playersCount; i++) {
            final int playerNumber = i;
            executorService.submit(() -> {
                try {
                    game.addPlayer(new Player("John", Player.Colour.values()[playerNumber % 2]));
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException ex) {
            Assertions.fail("Unexpected exception occurred", ex);
        }

        assertThat(game.getPlayers())
                .hasSize(2)
                .extracting(Player::getColour)
                .containsOnly(Player.Colour.Red, Player.Colour.Yellow);
    }


}
