package com.codingame.game;

import java.util.List;
import java.util.Random;
import java.util.RandomAccess;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;

public class Referee extends AbstractReferee {
    // Uncomment the line below and comment the line under it to create a Solo Game
    // @Inject private SoloGameManager<Player> gameManager;
    @Inject
    private MultiplayerGameManager<Player> gameManager;
    @Inject
    private GraphicEntityModule graphicEntityModule;

    Integer maxOxygenCapacity;

    @Override
    public void init() {
        maxOxygenCapacity = new Random().nextInt(Constants.MAX_OXYGEN_CAPACITY - Constants.MIN_OXYGEN_CAPACITY)
                + Constants.MIN_OXYGEN_CAPACITY;
        // Initialize your game here.
    }

    @Override
    public void gameTurn(int turn) {
        for (Player player : gameManager.getActivePlayers()) {
            player.sendInputLine("input");
            player.execute();
        }

        for (Player player : gameManager.getActivePlayers()) {
            try {
                List<String> outputs = player.getOutputs();
                // Check validity of the player output and compute the new game state
            } catch (TimeoutException e) {
                player.deactivate(String.format("$%d timeout!", player.getIndex()));
            }
        }
    }

    private void updateOxygen() {
        for (Player player : gameManager.getActivePlayers()) {
            if (player.getPosition().getY() > 0)
                player.changeOxygenLeft(-1);
            else
                player.setOxygenLeft(100); //TODO: set to max oxygen

            if (player.getOxygenLeft() < 0) {
                player.deactivate("The player has drowned");
            }
        }
    }
}
