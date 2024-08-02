package com.codingame.game.map_utils;

import java.util.Vector;

public class Coordinates {
    int x;
    int y;

    /*
     *    +--> x
     *   /  _____________________
     *  y  /0,0/1,0/2,0/3,0/.../
     *    /0,1/1,1/___/___/___/
     *   /0,2/___/2,2/___/___/
     *  /0,3/___/___/3,3/___/
     * /.../___/___/___/.../
     * */

    public enum Axis {
        X,
        Y
    }

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates add(int dx, int dy) {
        return new Coordinates(x + dx, y + dy);
    }

    public Coordinates add(Coordinates point) {
        return new Coordinates(
                x + point.getX(),
                y + point.getY()
        );
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
