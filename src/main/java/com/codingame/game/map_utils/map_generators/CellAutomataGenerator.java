package com.codingame.game.map_utils.map_generators;

import com.codingame.game.Constants;
import com.codingame.game.map_utils.MapFinaliser;
import com.codingame.game.map_utils.MapGenerator;

import java.util.Random;

import static com.codingame.game.Constants.HOLLOW_VALUE;
import static com.codingame.game.Constants.WALL_VALUE;

public class CellAutomataGenerator implements MapGenerator {

    public static final int FIRST_R1_COUNT = 5;
    public static final int FIRST_R2_COUNT = 2;
    public static final int SECOND_R1_COUNT = 5;
    private final int DEFAULT_DEPTH = 7;
    private final float INITIAL_PROBA = .45f;
    private final float CORAL_PROBA = Constants.BASE_CORAL_PROBA;
    private final float STEPS_SEGMENTATION = .6f;

    private int width;
    private int height;
    private Random gameRandom;
    private int depth;
    private boolean putCoral;

    public CellAutomataGenerator() {
    }

    public void init(int width, int height, Random gameRandom, int depth, boolean putCoral) {
        this.width = width;
        this.height = height;
        this.gameRandom = gameRandom;
        this.depth = depth;
        this.putCoral = putCoral;
    }

    @Override
    public void init(int width, int height, Random gameRandom, boolean putCoral) {
        this.init(width, height, gameRandom, DEFAULT_DEPTH, putCoral);
    }

    public int[][] generate(int[][] initMap) {
        int[][] map;

        if (initMap == null) {
            map = new int[height][width];
        } else {
            if (initMap.length == height && initMap[0].length == width) {
                map = initMap;
            } else {
                throw new RuntimeException("Dimensions of initMap don't correspond to arguments passed in init()");
            }
        }

        for (int i = initMap == null ? -1 : 0; i < depth; i++) {
            int[][] nextMap = new int[height][width];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (i < 0) {
                        if (x < width / 2) {
                            float r = gameRandom.nextFloat();
                            if (r <= INITIAL_PROBA)
                                nextMap[y][x] = WALL_VALUE;
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

                        if (i < (depth * STEPS_SEGMENTATION)) {

                            int countR2 = 0;
                            for (int dy = Math.max(0, y - 2); dy < Math.min(y + 3, height); dy++) {
                                for (int dx = Math.max(0, x - 2); dx < Math.min(x + 3, width); dx++) {
                                    countR2 += map[dy][dx];
                                }
                            }

                            nextMap[y][x] = countR1 >= FIRST_R1_COUNT || countR2 <= FIRST_R2_COUNT ? WALL_VALUE : HOLLOW_VALUE;
                        } else {
                            nextMap[y][x] = countR1 >= SECOND_R1_COUNT ? WALL_VALUE : HOLLOW_VALUE;
                        }
                    }
                }
            }
//            System.out.println(
//                    Arrays.deepToString(map)
//                            .replace("], [", "],\n[")
//                            .replace("0", " ")
//                            .replace("1", "#")
//                            + "\n"
//            );

            map = nextMap;
        }

        MapFinaliser.putSpawns(map);

        if (putCoral) {
            MapFinaliser.putCoralAndBonuses(map, gameRandom, CORAL_PROBA);
        }

        return map;
    }

    @Override
    public int[][] generate() {
        return generate(null);
    }
}
