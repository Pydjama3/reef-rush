package com.codingame.game;

import com.codingame.game.map_utils.Coordinates;
import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import com.codingame.gameengine.module.entities.Sprite;

// Uncomment the line below and comment the line under it to create a Solo Game
// public class Player extends AbstractSoloPlayer {
public class Player extends AbstractMultiplayerPlayer {

    public Sprite sprite;
    int[] maxMove;
    int direction = 1;
    private Integer oxygenLeft;
    private Coordinates position;

    public Player() {
        super();

        oxygenLeft = 0;
        position = new Coordinates(0, 0);
    }

    @Override
    public int getExpectedOutputLines() {
        // Returns the number of expected lines of outputs for a player
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

    public void updateOxygen() {
        if (position.getY() > 1) {
            changeOxygenLeft(-1);
        } else {
            setOxygenLeft(Constants.MAX_OXYGEN_CAPACITY);
        }
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates coordinates) {
        position = coordinates;
    }

    public void setPosition(int x, int y) {
        position.set(x, y);
    }

    public void changePosition(Move move) {
        changePosition(new Coordinates(move.x, move.y));
    }

    public void changePosition(Coordinates displacement) {
        if (displacement.getX() < 0) {
            direction = -1;
        } else if (displacement.getX() > 0) {
            direction = 1;
        }
        position = position.add(displacement);
    }

    public void setMaxMove(int[] maxMove) {
        this.maxMove = maxMove;
    }

    public enum Move {
        UP("up", 0, -1),
        RIGHT("right", 1, 0),
        DOWN("down", 0, 1),
        LEFT("left", -1, 0),

        NONE("none", 0, 0),
        INVALID("-1", -1, -1);

        public final int x;
        public final int y;
        public final String code;

        Move(String code, int x, int y) {
            this.code = code;

            this.x = x;
            this.y = y;
        }

        public static Move getMovementFromCode(String code) {
            Move nameOfCode = INVALID;

            for (Move move : Move.values()) {
                if (code.equals(move.code)) {
                    nameOfCode = move;
                    break;
                }
            }

            return nameOfCode;
        }
    }
}
