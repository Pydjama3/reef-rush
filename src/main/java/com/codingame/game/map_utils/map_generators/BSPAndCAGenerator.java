package com.codingame.game.map_utils.map_generators;

import com.codingame.game.map_utils.MapGenerator;

import java.util.Random;

public class BSPAndCAGenerator implements MapGenerator {
    private static final int DEFAULT_DEPTH = 7;

    private int width;
    private int height;
    private Random gameRandom;
    private int depth;
    private boolean putCoral;

    public BSPAndCAGenerator() {

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

    @Override
    public int[][] generate() {
        MapGenerator initGenerator = new BSPGenerator();
        initGenerator.init(width, height, gameRandom, false);
        int[][] initMap = initGenerator.generate();

        CellAutomataGenerator finalizer = new CellAutomataGenerator();
        finalizer.init(width, height, gameRandom, putCoral);

        return finalizer.generate(initMap);
    }
}
