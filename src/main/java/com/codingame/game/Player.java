package com.codingame.game;

import com.codingame.game.map_utils.Coordinates;
import com.codingame.gameengine.core.AbstractMultiplayerPlayer;

// Uncomment the line below and comment the line under it to create a Solo Game
// public class Player extends AbstractSoloPlayer {
public class Player extends AbstractMultiplayerPlayer {

    Integer oxygenLeft;

    Coordinates position;

    public enum Movements {
        UP(1, 0, -1),
        RIGHT(2, 1, 0),
        DOWN(3, 0, 1),
        LEFT(4, -1, 0),

        NONE(0, 0, 0),
        INVALID(-1, -1, -1);

        final int code;
        final int x;
        final int y;

        Movements(int code, int x, int y) {
            this.code = code;

            this.x = x;
            this.y = y;
        }

        public Movements getMovementFromCode(int code) {
            Movements nameOfCode = INVALID;

            switch (code) {
                case 0:
                    nameOfCode = NONE;
                case 1:
                    nameOfCode = UP;
                case 2:
                    nameOfCode = RIGHT;
                case 3:
                    nameOfCode = DOWN;
                case 4:
                    nameOfCode = LEFT;
            }

            return nameOfCode;
        }

    }

    @Override
    public int getExpectedOutputLines() {
        // Returns the number of expected lines of outputs for a player

        // TODO: Replace the returned value with a valid number. Most of the time the value is 1. 
        return 1;
    }

    public Integer getOxygenLeft() {
        return oxygenLeft;
    }

    public void setOxygenLeft(Integer quantity) {
        oxygenLeft = quantity;
    }

    public void changeOxygenLeft(Integer quantity) {
        oxygenLeft += quantity;
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(int x, int y) {
        position.set(x, y);
    }

    public void changePosition(Movements movement) {
        position.add(movement.x, movement.y);
    }
}
