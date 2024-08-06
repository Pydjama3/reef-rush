package com.codingame.game.map_utils;

public interface MapGenerator {
    public void init(int width, int height, boolean isSymmetric);

    public int[][] generate();
}
