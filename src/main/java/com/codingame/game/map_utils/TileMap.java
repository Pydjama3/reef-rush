package com.codingame.game.map_utils;

import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import static com.codingame.game.Constants.*;

public class TileMap {
    private final int width;
    private final int height;

    private final int[][] simpleMap;

    private final int[][] renderedMap;
    private final Map<Coordinates, Integer> coralMap;

    private final Random gameRandom;

    public TileMap(int[][] map, Random gameRandom) {
        this.simpleMap = map;
        this.gameRandom = gameRandom;

        this.width = map[0].length;
        this.height = map.length;

        renderedMap = new int[height][width];
        coralMap = new Hashtable<>();

        setRenderedTilesIndicesAndCoralCounts();
    }

    public static TileMap create(int width, int height, Random gameRandom, MapGenerator generator) {
        generator.init(width, height, gameRandom, PUT_CORAL);
        return new TileMap(generator.generate(), gameRandom);
    }

    private void setRenderedTilesIndicesAndCoralCounts() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int imgId = -1;
                if (simpleMap[y][x] == WALL_VALUE) {
                    if (y > 0) {
                        if (simpleMap[y - 1][x] == HOLLOW_VALUE) {
                            imgId = SAND_TOP_INDICES[gameRandom.nextInt(SAND_TOP_INDICES.length)];
                        } else {
                            imgId = SAND_FILL_INDICES[gameRandom.nextInt(SAND_FILL_INDICES.length)];
                        }
                    } else {
                        imgId = SAND_TOP_INDICES[gameRandom.nextInt(SAND_TOP_INDICES.length)];
                    }
                } else if (simpleMap[y][x] == CORAL_VALUE) {
                    imgId = CORAL_INDICES[gameRandom.nextInt(CORAL_INDICES.length)];

                    if (!coralMap.containsKey(new Coordinates(x, y))) {

                        int coral_count = Math.round(CORAL_COUNT_FUNCTION.apply(gameRandom.nextDouble()).floatValue());
                        coralMap.put(
                                new Coordinates(x, y),
                                coral_count
                        );

                        coralMap.put(
                                new Coordinates(width - x - 1, y),
                                coral_count
                        );
                    }
                }
                renderedMap[y][x] = imgId;
            }
        }
    }

    public int[][] getRenderedMap() {
        return renderedMap;
    }

    public int getPlasticCount(Coordinates pos) {
        return coralMap.getOrDefault(pos, 0);
    }

    public void setPlasticCount(Coordinates pos, int val) {
        coralMap.put(pos, val);
    }

    public int get(Coordinates pos) {
        return simpleMap[pos.getY()][pos.getX()];
    }

    public int[] getFurthestInDirection(Coordinates initPos, int dx, int dy) {
        int x = initPos.getX();
        int y = initPos.getY();

        int i = 0;
        while (0 <= x + dx * i && x + dx * i < width && 0 <= y + dy * i && y + dy * i < height) {
            if (simpleMap[y + dy * i][x + dx * i] == WALL_VALUE)
                break;

            i++;
        }

        int object = WALL_VALUE;
        if ((0 <= (x + (dx * i))) && ((x + (dx * i)) < width)
                && (0 <= (y + (dy * i))) && ((y + (dy * i)) < height)) {
            object = simpleMap[y + dy * i][x + dx * i];
        } else {
            if (y + dy * i < 0) {
                object = SURFACE_VALUE;
            }
        }

        return new int[]{object, i - 1};
    }

    public Coordinates[] getCoralPos() {
        return coralMap.keySet().toArray(new Coordinates[0]);
    }
}
