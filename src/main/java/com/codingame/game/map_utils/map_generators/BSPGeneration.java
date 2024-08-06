package com.codingame.game.map_utils.map_generators;

import com.codingame.game.map_utils.Coordinates;
import com.codingame.game.map_utils.MapGenerator;

public class BSPGeneration implements MapGenerator {

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

        Leaf parent;
        Leaf[] children;

        public Leaf(Coordinates position, int width, int height, Leaf parent) {
            this.position = position;
            this.width = width;
            this.height = height;
            this.parent = parent;
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

            Leaf first_leaf = new Leaf(first_pos, first_width, first_height, this);
            Leaf second_leaf = new Leaf(second_pos, second_width, second_height, this);

            children = new Leaf[]{first_leaf, second_leaf};

            return children;
        }

        public Leaf[] splitY(int yPos) {
            int first_height = yPos;
            int second_height = height - first_height;

            int first_width = width;
            int second_width = width;

            Coordinates first_pos = position;
            Coordinates second_pos = position.add(0, first_height);

            Leaf first_leaf = new Leaf(first_pos, first_width, first_height, this);
            Leaf second_leaf = new Leaf(second_pos, second_width, second_height, this);

            children = new Leaf[]{first_leaf, second_leaf};

            return children;
        }

        public int getArea() {
            return width * height;
        }

        public Leaf[] getChildren() {
            return children;
        }

        public Leaf getOtherLeaf(Leaf sister) {
            Leaf otherSister = null;
            for(Leaf leaf : children) {
                if (!leaf.equals(sister)){
                    otherSister = leaf;
                }
            }
            return otherSister;
        }
    }

    private int width;
    private int height;
    private boolean isSymmetric;

    /* --- SETTINGS --- */
    private final int DEFAULT_DEPTH_QUOTIENT = 5; //TODO: find/set quotient

    public BSPGeneration() {
    }

    public void init(int width, int height, boolean isSymmetric, int depth) {
        this.width = width;
        this.height = height;
        this.isSymmetric = isSymmetric;
    }

    @Override
    public void init(int width, int height, boolean isSymmetric) {
        this.init(width, height, isSymmetric, Math.min(width, height) * DEFAULT_DEPTH_QUOTIENT);
    }

    @Override
    public int[][] generate() {
        return new int[0][];
    }
}
