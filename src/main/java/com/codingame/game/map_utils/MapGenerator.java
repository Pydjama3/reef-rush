package com.codingame.game.map_utils;

import com.codingame.game.Player;
import com.codingame.gameengine.core.MultiplayerGameManager;

public interface MapGenerator {
    public void init(int width, int height, Tileset tileset, MultiplayerGameManager<Player> gameManager);

    public int[][] generate();
}
