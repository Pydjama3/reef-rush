package com.codingame.game.map_utils;

import java.util.HashMap;
import java.util.Map;

public class Tileset {

    private Map<Integer, String> set = new HashMap<>();

    public Tileset() {

    }

    public void set(int id, String name) {
        set.put(id, name);
    }

    public String get(int id) {
        return set.get(id);
    }

    public void delete(int id) {
        set.remove(id);
    }
}
