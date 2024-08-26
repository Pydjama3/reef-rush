package com.codingame.game.map_utils;

import java.util.Random;

public interface MapGenerator {
    public void init(int width, int height, Tileset tileset, Random gameRandom, boolean putCoral);

    public int[][] generate();
}
