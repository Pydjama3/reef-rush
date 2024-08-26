package com.codingame.game.map_utils;

import java.util.*;

public class MapFinaliser {
    private static final int BG_VALUE = 1;
    private static final Coordinates RELATIVE_SPAWN_POS = new Coordinates(0, 0);

    public static void putCoral(int[][] map, Random gameRandom, float percent) {
        for (int x = 0; x < map[0].length / 2; x++) {
            for (int y = 0; y < map.length - 1; y++) {
                if (map[y][x] == 0 && gameRandom.nextFloat() < percent) {
                    if (map[y + 1][x] == 1) {
                        map[y][x] = 2;
                        map[y][map[0].length - x - 1] = 2;
                    }
                }
            }
        }
    }

    public static void putSpawns(int[][] map) {
        int height = map.length;
        int width = map[0].length;

        HashMap<Integer, Set<Integer>> linked = new HashMap<>();
        int[][] labels = new int[height][width];
        int nextLabel = 1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (map[y][x] == BG_VALUE)
                    continue;

                Set<Integer> neighbours = new HashSet<>();

                int[] dxs = new int[]{-1, 0, 1, 0}; // {...}
                int[] dys = new int[]{0, -1, 0, 1}; // {...}

                for (int i = 0; i < 2 /*dxs.length*/; i++) {
                    int dx = dxs[i];
                    int dy = dys[i];

                    int neighbourX = Math.max(0, Math.min(x + dx, width - 1));
                    int neighbourY = Math.max(0, Math.min(y + dy, height - 1));

                    if (neighbourX == x && neighbourY == y)
                        continue;

                    if (map[neighbourY][neighbourX] == BG_VALUE)
                        continue;

                    neighbours.add(labels[neighbourY][neighbourX]);
                }

                if (neighbours.isEmpty()) {
                    Set<Integer> initSet = new HashSet<>();
                    initSet.add(nextLabel);
                    linked.put(nextLabel, initSet);

                    labels[y][x] = nextLabel;

                    nextLabel += 1;
                } else {
                    labels[y][x] = min(setToArray(neighbours));

                    for (Integer neighbourLabel : neighbours) {
                        Set<Integer> union = new HashSet<>(neighbours);
                        union.addAll(linked.get(neighbourLabel));

                        linked.put(neighbourLabel, union);
                    }
                }
            }
        }

        Map<Integer, List<Coordinates>> labelPoints = new Hashtable<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (map[y][x] == BG_VALUE)
                    continue;

                int currentLabel = labels[y][x];

                while (min(setToArray(linked.get(currentLabel))) != currentLabel) {
                    currentLabel = min(setToArray(linked.get(currentLabel)));
                }

                List<Coordinates> pointList = labelPoints.get(currentLabel);
                List<Coordinates> nextPoints = new ArrayList<>();

                if (pointList == null) {
                    nextPoints.add(new Coordinates(x, y));
                } else {
                    nextPoints = pointList;
                    nextPoints.add(new Coordinates(x, y));
                }

                labelPoints.put(currentLabel, nextPoints);

                labels[y][x] = currentLabel;
            }
        }

        Coordinates[][] orderedConnectedComponents = new Coordinates[labelPoints.keySet().size()][];

        int i = 0;
        for (int label : labelPoints.keySet()) {
            orderedConnectedComponents[i] = labelPoints.get(label).toArray(new Coordinates[0]);
            i++;
        }

        Arrays.sort(orderedConnectedComponents, Comparator.comparingInt(o -> -o.length));

        Coordinates[] biggestArea = orderedConnectedComponents[0];

        Coordinates nearestPoint = biggestArea[new Random().nextInt(biggestArea.length)];
        double minDistance = Double.MAX_VALUE;

        for (Coordinates point : biggestArea) {
            double distance = RELATIVE_SPAWN_POS.distanceTo(point);

            if (distance < minDistance) {
                minDistance = distance;
                nearestPoint = point;
            }
        }

        int xDistance = nearestPoint.getX() - RELATIVE_SPAWN_POS.getX();
        int yDistance = nearestPoint.getY() - RELATIVE_SPAWN_POS.getY();

        int steps = Math.max(
                Math.abs(xDistance),
                Math.abs(yDistance)
        );

        double xStep = (double) xDistance / steps;
        double yStep = (double) yDistance / steps;

        for (int t = 0; t < steps + 1; t++) {
            int dx = (int) Math.round(xStep * t);
            int dy = (int) Math.round(yStep * t);

            int[] ddx = new int[]{-1, 1, 0, 0, 0};
            int[] ddy = new int[]{0, 0, 0, -1, 1};

            for (int j = 0; j < ddx.length; j++) {
                int pathX = Math.max(0, Math.min(RELATIVE_SPAWN_POS.getX() + dx + ddx[j], width - 1));
                int pathY = Math.max(0, Math.min(RELATIVE_SPAWN_POS.getY() + dy + ddy[j], height - 1));

                map[pathY][pathX] = 0;
                map[pathY][width - pathX - 1] = 0;
            }
        }
    }

    private static int min(int[] array) {
        int min = Integer.MAX_VALUE;
        for (Integer value : array) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    private static int[] setToArray(Set<Integer> set) {
        return set.stream().mapToInt(Number::intValue).toArray();
    }
}
