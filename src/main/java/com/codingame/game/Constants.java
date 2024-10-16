package com.codingame.game;

import com.codingame.game.map_utils.Coordinates;
import com.codingame.game.map_utils.MapGenerator;
import com.codingame.game.map_utils.map_generators.BSPAndCAGenerator;
import com.codingame.gameengine.module.entities.World;

import java.util.function.DoubleFunction;

public class Constants {
    /* --- DISPLAY --- */
    public static final int VIEWER_WIDTH = World.DEFAULT_WIDTH;
    public static final int VIEWER_HEIGHT = World.DEFAULT_HEIGHT;
    public static final float SKY_OFFSET = .8f;

    /* Main Tileset */
    public static final String MAIN_TS_IMG_SOURCE = "SpriteSheet.png";
    public static final String MAIN_TS_NAME = "underwater_sheet";
    public static final int MAIN_TS_TILE_SIZE = 128;
    public static final int MAIN_TS_TILE_COUNT = 90;
    public static final int MAIN_TS_IMG_PER_ROW = 18;

    public static final int[] SAND_FILL_INDICES = new int[]{0, 1, 18, 19};
    public static final int[] SAND_TOP_INDICES = new int[]{2, 3, 4, 5, 6, 7, 8, 9, 20, 21, 22, 23, 24, 25, 26, 27};
    public static final int[] WATER_INDICES = new int[]{88};
    public static final int[] SKY_INDICES = new int[]{89};
    public static final int[] CORAL_INDICES = new int[]{10, 11, 12, 13, 14, 15, 16, 17, 28, 29, 30, 31, 32, 33, 34, 35};

    /* Background */
    public static final String BG_IMG_SOURCE = "sea.jpg";
    public static final int BG_WIDTH = 2048;
    public static final int BG_HEIGHT = 1536;

    /* Submarine */
    public static final String SUBMARINE_IMG_SOURCE = "Atlantis/Atlantis2_SpriteAnimation.png";
    public static final String SUBMARINE_NAME = "submarine";
    public static final int SUBMARINE_TILE_WIDTH = 96;
    public static final int SUBMARINE_TILE_HEIGHT = 64;
    public static final int SUBMARINE_TILE_COUNT = 9;
    public static final int SUBMARINE_IMG_PER_ROW = 2;

    public static final int RED_COLOR = 0xFF1111;
    public static final int BLUE_COLOR = 0x1111FF;


    /* --- TERRAIN GENERATION --- */
    public static final int MIN_MAP_SIZE_EXPONENT = 4; // 5;
    public static final int MAX_MAP_SIZE_EXPONENT = 7;
    public static final int MAP_IS_POWER_OF = 2;
    public static final MapGenerator BASE_MAP_GENERATOR = new BSPAndCAGenerator();
    public static final boolean PUT_CORAL = true;

    public static final int HOLLOW_VALUE = 0;   // should stay 0 :)
    public static final int WALL_VALUE = 1;
    public static final int CORAL_VALUE = 2;
    public static final int SUBMARINE_VALUE = 3;
    public static final int SURFACE_VALUE = -1;

    public static final Coordinates RELATIVE_SPAWN_POS = new Coordinates(0, 0);
    public static final float BASE_CORAL_PROBA = 1 / 3f;

    public static final int MIN_CORAL = 5;
    public static final int MAX_CORAL = 20;
    public static final DoubleFunction<Double> CORAL_COUNT_FUNCTION = (x) -> Math.exp(x * Math.log((double) MAX_CORAL / MIN_CORAL)) * MIN_CORAL;

    /* --- GAME --- */
    public static final int MIN_TURNS = 100;
    public static final int TURN_COEFF = 200;


    /* --- PLAYER --- */
    public static Integer MAX_OXYGEN_CAPACITY = 42;
    public static Integer MIN_OXYGEN_CAPACITY = 5;
}
