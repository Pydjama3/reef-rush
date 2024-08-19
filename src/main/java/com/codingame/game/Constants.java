package com.codingame.game;

import com.codingame.gameengine.module.entities.World;

public class Constants {
    public static final int VIEWER_WIDTH = World.DEFAULT_WIDTH;
    public static final int VIEWER_HEIGHT = World.DEFAULT_HEIGHT;
    public static final int CELL_SIZE = 128;
    public static final int CELL_OFFSET = CELL_SIZE / 2;

    public static Integer MAX_OXYGEN_CAPACITY = 30;
    public static Integer MIN_OXYGEN_CAPACITY = 5;
    public Integer MAX_CORAL_NUM;
    public Integer MIN_CORAL_NUM = 0;

//    public static final Map<Action, Coord> ACTION_MAP = new HashMap<>();
//
//    static {
//        ACTION_MAP.put(Action.UP, Coord.UP);
//        ACTION_MAP.put(Action.DOWN, Coord.DOWN);
//        ACTION_MAP.put(Action.STILL, Coord.ZERO);
//    }
}
