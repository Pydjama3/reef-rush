package com.codingame.game.map_utils.map_generators;

import com.codingame.game.Player;
import com.codingame.game.map_utils.MapGenerator;
import com.codingame.game.map_utils.Tileset;
import com.codingame.gameengine.core.MultiplayerGameManager;

public class CellAutomataGenerator implements MapGenerator {

    private final int DEFAULT_DEPTH = 7;
    private final float INITIAL_PROBA = .45f;

    int width;
    int height;
    boolean isSymmetric;
    Tileset tileset;
    MultiplayerGameManager<Player> gameManager;
    int depth;

    public CellAutomataGenerator() {
    }

    public void init(int width, int height, Tileset tileset, MultiplayerGameManager<Player> gameManager, int depth) {
        this.width = width;
        this.height = height;
        this.tileset = tileset;
        this.gameManager = gameManager;
        this.depth = depth;
    }

    @Override
    public void init(int width, int height, Tileset tileset, MultiplayerGameManager<Player> gameManager) {
        this.init(width, height, tileset, gameManager, DEFAULT_DEPTH);
    }

    @Override
    public int[][] generate() {
        int[][] map = new int[height][width];

        for (int i = -1; i < depth; i++) {
            int[][] nextMap = new int[height][width];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (i < 0) {
                        if (x < width / 2) {
                            float r = gameManager.getRandom().nextFloat();
                            if (r <= INITIAL_PROBA)
                                nextMap[y][x] = 1;
                        } else {
                            nextMap[y][x] = nextMap[y][width - x - 1];
                        }

                    } else {
                        int countR1 = 0;
                        for (int dy = Math.max(0, y - 1); dy < Math.min(y + 2, height); dy++) {
                            for (int dx = Math.max(0, x - 1); dx < Math.min(x + 2, width); dx++) {
                                countR1 += map[dy][dx];
                            }
                        }

                        if (i < (depth * .6)) {

                            int countR2 = 0;
                            for (int dy = Math.max(0, y - 2); dy < Math.min(y + 3, height); dy++) {
                                for (int dx = Math.max(0, x - 2); dx < Math.min(x + 3, width); dx++) {
                                    countR2 += map[dy][dx];
                                }
                            }

                            nextMap[y][x] = countR1 >= 5 || countR2 <= 2 ? 1 : 0;
                        } else {
                            nextMap[y][x] = countR1 >= 5 ? 1 : 0;
                        }
                        /*
                        if y < WIDTH / 10 and x < WIDTH / 10: (10%)
                            nextmap[x[y = 0;
                         */

                    }
                }
            }
            map = nextMap;
        }
        return map;
    }
}
