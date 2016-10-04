package com.kainos.connect4game.rest.utils;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.domain.Player;

public class DomainUtils {

    public static Player player(String name, Player.Colour colour) {
        return new Player(name, colour);
    }

    public static Game game(Player... players) {
        Game game = new Game();
        for (Player player: players) {
            game.addPlayer(player);
        }
        return game;
    }
}
