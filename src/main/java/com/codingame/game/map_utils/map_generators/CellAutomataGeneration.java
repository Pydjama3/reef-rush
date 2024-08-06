package com.codingame.game.map_utils.map_generators;

import com.codingame.game.map_utils.MapGenerator;

public class CellAutomataGeneration implements MapGenerator {

    private final int DEFAULT_DEPTH = 5;

    public void init(int width, int height, boolean isSymmetric, int depth){

    }

    @Override
    public void init(int width, int height, boolean isSymmetric) {
        this.init(width, height, isSymmetric, DEFAULT_DEPTH);
    }

    @Override
    public int[][] generate() {
        return new int[0][];
    }
}
