package com.kainos.connect4game.domain;


import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

public class BoardMultiThreadingTest {

    private final Game game = new Game();

    @Test
    public void concurrentDiscDropsShouldBeProperlyReflectedOnTheBoard() {
        int discsCount = Board.NUMBER_OF_COLUMNS * Board.NUMBER_OF_ROWS;

        ExecutorService executorService = Executors.newFixedThreadPool(discsCount / 2);
        CountDownLatch countDownLatch = new CountDownLatch(discsCount);

        for (int i = 0; i < Board.NUMBER_OF_COLUMNS; i++) {
            for (int j = 0; j < Board.NUMBER_OF_ROWS; j++) {
                final int column = i;
                executorService.submit(() -> {
                    try {
                        game.getBoard().dropDisc(Player.Colour.Red, column);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException ex) {
            Assertions.fail("Unexpected exception occurred", ex);
        }

        assertThat(game.getBoard().getFields())
                .filteredOn(field -> field.getColour() == Player.Colour.Red)
                .hasSize(discsCount);
    }

}
