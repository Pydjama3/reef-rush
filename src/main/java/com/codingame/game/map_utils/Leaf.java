package com.codingame.game.map_utils;

public class Leaf {
    Coordinates position;
    int width;
    int height;

    /*
     * (x, y)
     *    +___w____
     *   /        /
     *  h        /
     * /________+
     *      (x+w, y+h)
     * */

    public Leaf(Coordinates position, int width, int height) {
        this.position = position;
        this.width = width;
        this.height = height;
    }

    public Coordinates.Axis getLongestAxis() {
        if (position.getX() > position.getY()) {
            return Coordinates.Axis.X;
        } else {
            return Coordinates.Axis.Y;
        }
    }

    public Leaf[] splitX(int xPos) {
        int first_height = height;
        int second_height = height;

        int first_width = xPos;
        int second_width = width - first_width;

        Coordinates first_pos = position;
        Coordinates second_pos = position.add(first_width, 0);

        Leaf first_leaf = new Leaf(first_pos, first_width, first_height);
        Leaf second_leaf = new Leaf(second_pos, second_width, second_height);

        return new Leaf[]{first_leaf, second_leaf};
    }

    public Leaf[] splitY(int yPos) {
        int first_height = yPos;
        int second_height = height - first_height;

        int first_width = width;
        int second_width = width;

        Coordinates first_pos = position;
        Coordinates second_pos = position.add(0, first_height);

        Leaf first_leaf = new Leaf(first_pos, first_width, first_height);
        Leaf second_leaf = new Leaf(second_pos, second_width, second_height);

        return new Leaf[]{first_leaf, second_leaf};
    }

    public int getArea() {
        return width * height;
    }
}
