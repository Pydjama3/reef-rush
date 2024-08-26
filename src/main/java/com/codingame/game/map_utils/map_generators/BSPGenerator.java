package com.codingame.game.map_utils.map_generators;

import com.codingame.game.map_utils.Coordinates;
import com.codingame.game.map_utils.MapFinaliser;
import com.codingame.game.map_utils.MapGenerator;
import com.codingame.game.map_utils.Tileset;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BSPGenerator implements MapGenerator {

    public static final int MIN_SIZE = 2;
    private static final float CORAL_PROBA = 1f / 3;
    private final int DEFAULT_DEPTH_CONST = 4; //TODO: find/set quotient

    private int width;
    private int height;
    private boolean isSymmetric;
    private Tileset tileset;
    private Random gameRandom;
    private int depth;
    private boolean putCoral;

    public BSPGenerator() {
    }

    public void init(int width, int height, Tileset tileset, Random gameRandom, int depth, boolean putCoral) {
        this.width = width;
        this.height = height;
        this.gameRandom = gameRandom;
        this.depth = depth;
        this.tileset = tileset;
        this.putCoral = putCoral;
    }

    @Override
    public void init(int width, int height, Tileset tileset, Random gameRandom, boolean putCoral) {
        this.init(
                width,
                height,
                tileset,
                gameRandom,
                (int) (Math.log(Math.min(width, height) / Math.log(2)) + DEFAULT_DEPTH_CONST),
                putCoral
        );
    }

    @Override
    public int[][] generate() {
        int[][] map = new int[this.height][this.width];

        for (int i = 0; i < map.length; i++) {
            Arrays.fill(map[i], 1);
        }

        Leaf initLeaf = new Leaf(new Coordinates(0, 0), width, height, null);

        Leaf[] initSplit = initLeaf.splitX(width / 2);

        ArrayList<Leaf> bCurrentLeaves = new ArrayList<>(Collections.singletonList(initSplit[0]));
        ArrayList<Leaf> rCurrentLeaves = new ArrayList<>(Collections.singletonList(initSplit[1]));

        for (int i = 0; i < depth; i++) {
            ArrayList<Leaf> bNextLeaves = new ArrayList<>();
            ArrayList<Leaf> rNextLeaves = new ArrayList<>();
            for (int j = 0; j < bCurrentLeaves.size(); j++) {
                Leaf bCurrentLeaf = bCurrentLeaves.get(j);
                Leaf rCureentLeaf = rCurrentLeaves.get(j);

                Leaf[] bSisters;
                Leaf[] rSisters;

                if (bCurrentLeaf.getLongestAxis() == Coordinates.Axis.X) {
                    int cut = gameRandom.nextInt(bCurrentLeaf.width - 1);
                    bSisters = bCurrentLeaf.splitX(cut);
                    rSisters = rCureentLeaf.splitX(rCureentLeaf.width - cut);
                } else {
                    int cut = gameRandom.nextInt(bCurrentLeaf.height - 1);

                    bSisters = bCurrentLeaf.splitY(cut);
                    rSisters = rCureentLeaf.splitY(cut);
                }

                if (Math.min(bSisters[0].width, bSisters[0].height) > MIN_SIZE) { //bSisters[0].getArea() > 1) {
                    bNextLeaves.add(bSisters[0]);
                    rNextLeaves.add(rSisters[bCurrentLeaf.getLongestAxis() == Coordinates.Axis.X ? 1 : 0]);
                }

                if (Math.min(bSisters[1].width, bSisters[1].height) > MIN_SIZE) {  //(bSisters[1].getArea() > 1) {
                    bNextLeaves.add(bSisters[1]);
                    rNextLeaves.add(rSisters[bCurrentLeaf.getLongestAxis() == Coordinates.Axis.X ? 0 : 1]);
                }
            }
            bCurrentLeaves = bNextLeaves;
            rCurrentLeaves = rNextLeaves;
        }

        List<Leaf> allLeaves = Stream.of(bCurrentLeaves, rCurrentLeaves)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        for (Leaf leaf : allLeaves) {
            int corridorLength = leaf.position.getY() <= 2 ? 0 : 1;

            for (int dy = corridorLength; dy < leaf.height - 1; dy++) {
                for (int dx = 1; dx < leaf.width - 1; dx++) {
                    map[leaf.position.getY() + dy][leaf.position.getX() + dx] = 0;
                }
            }
        }

        ArrayList<Leaf> currentLeaves = bCurrentLeaves;

        for (int i = 0; i <= depth; i++) {
            ArrayList<Leaf> nextLeaves = new ArrayList<>();
            for (Leaf leaf : currentLeaves) {

                Coordinates firstSisterPos = new Coordinates(
                        positionDependantRound(leaf.position.getX() + (double) leaf.width / 2),
                        leaf.position.getY() + leaf.height / 2
                );

                Leaf secondSister = leaf.getOtherLeaf(leaf);
                Coordinates secondSisterPos = new Coordinates(
                        positionDependantRound(secondSister.position.getX() + (double) secondSister.width / 2),
                        secondSister.position.getY() + secondSister.height / 2
                );

                int xDiff = secondSisterPos.getX() - firstSisterPos.getX();
                int yDiff = secondSisterPos.getY() - firstSisterPos.getY();

                int distance = Math.max(
                        Math.abs(xDiff),
                        Math.abs(yDiff)
                );

                double xStep = (double) xDiff / distance;
                double yStep = (double) yDiff / distance;

                for (int t = 0; t < distance; t++) {
                    int x = (int) xStep * t + firstSisterPos.getX();
                    int y = (int) yStep * t + firstSisterPos.getY();

                    map[y][x] = 0;
                    map[y][width - x - 1] = 0;

                    if (Math.abs(xStep) > Math.abs(yStep)) {
                        map[y + 1][x] = 0;
//                        map[y - 1][x] = 0;

                        map[y + 1][width - x - 1] = 0;
//                        map[y - 1][width - x - 1] = 0;
                    } else {
                        map[y][x + 1] = 0;
//                        map[y][x - 1] = 0;

                        map[y][width - (x + 1) - 1] = 0;
//                        map[y][width - (x - 1) - 1] = 0;
                    }
                }

                nextLeaves.add(leaf.parent);
            }
            currentLeaves = nextLeaves;
        }

        MapFinaliser.putSpawns(map);

        if (putCoral) {
            MapFinaliser.putCoral(map, gameRandom, CORAL_PROBA);
        }

        return map;
    }

    private int positionDependantRound(double x) {
        if (x - (double) width / 2 > 0) {
            return (int) Math.ceil(x) - 1;
        }
        return (int) Math.floor(x);
    }

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
            if (width > height) {
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

        public Leaf[] splitX() {
            return splitX(gameRandom.nextInt(width - 1));
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

        public Leaf[] splitY() {
            return this.splitY(gameRandom.nextInt(height - 1));
        }

        public int getArea() {
            return width * height;
        }

        public Leaf[] getChildren() {
            return children;
        }

        public Leaf getOtherLeaf(Leaf sister) {
            Leaf otherSister = null;
            for (Leaf leaf : parent.children) {
                if (!leaf.equals(sister)) {
                    otherSister = leaf;
                }
            }
            return otherSister;
        }

        @Override
        public String toString() {
            return "Leaf{" +
                    "position=" + position +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

//    private int[][] putCoral(int number, int )
}
