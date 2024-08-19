package com.codingame.game.map_utils.map_generators;

import com.codingame.game.Player;
import com.codingame.game.map_utils.MapGenerator;
import com.codingame.game.map_utils.Tileset;
import com.codingame.gameengine.core.MultiplayerGameManager;

public class BSPAndCAGenerator implements MapGenerator {
    private static final int DEFAULT_DEPTH = 7;

    private int width;
    private int height;
    private MultiplayerGameManager<Player> gameManager;
    private int depth;
    private Tileset tileset;

    BSPAndCAGenerator() {

    }

    public void init(int width, int height, Tileset tileset, MultiplayerGameManager<Player> gameManager, int depth) {
        this.width = width;
        this.height = height;
        this.gameManager = gameManager;
        this.depth = depth;
        this.tileset = tileset;
    }

    @Override
    public void init(int width, int height, Tileset tileset, MultiplayerGameManager<Player> gameManager) {
        this.init(width, height, tileset, gameManager, DEFAULT_DEPTH);
    }

    @Override
    public int[][] generate() {
        return new int[0][];
    }
}
