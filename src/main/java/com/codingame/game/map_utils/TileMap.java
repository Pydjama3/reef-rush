package com.codingame.game.map_utils;

import com.codingame.gameengine.module.entities.GraphicEntityModule;

import java.util.Random;

public class TileMap {


    public TileMap(Tileset tileset, MapGenerator mapGenerator, GraphicEntityModule graphicEntityModule) {

    }

    public TileMap() {

    }

    public static TileMap create(int width, int height, Tileset tileset, Random gameRandom, MapGenerator generator) {
        generator.init(width, height, tileset, gameRandom, true);
        return new TileMap();
    }

    public void get() {

    }
}
