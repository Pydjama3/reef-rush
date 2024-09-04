package com.codingame.game.map_utils;

public class Coordinates {
    private int x;
    private int y;

    /*
     *    +--> x
     *   /  _____________________
     *  y  /0,0/1,0/2,0/3,0/.../
     *    /0,1/1,1/___/___/___/
     *   /0,2/___/2,2/___/___/
     *  /0,3/___/___/3,3/___/
     * /.../___/___/___/.../
     * */

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

    public Coordinates subtract(Coordinates point) {
        return new Coordinates(
                x - point.getX(),
                y - point.getY()
        );
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isSuperiorTo(Coordinates pos) {
        return this.x > pos.x && this.y > pos.y;
    }

    public double distanceTo(Coordinates pos) {
        return Math.sqrt(
                Math.pow(this.x - pos.x, 2)
                        + Math.pow(this.y - pos.y, 2)
        );
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == Coordinates.class)
            return ((Coordinates) obj).x == this.x && ((Coordinates) obj).y == this.y;
        else
            return super.equals(obj);
    }

    public enum Axis {
        X,
        Y
    }
}
