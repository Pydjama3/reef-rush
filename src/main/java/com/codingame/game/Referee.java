package com.codingame.game;

import com.codingame.game.map_utils.MapGenerator;
import com.codingame.game.map_utils.Tileset;
import com.codingame.game.map_utils.map_generators.BSPGenerator;
import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;

import java.util.List;

import static com.codingame.game.Constants.VIEWER_HEIGHT;
import static com.codingame.game.Constants.VIEWER_WIDTH;

public class Referee extends AbstractReferee {
    Integer maxOxygenCapacity;
    MapGenerator generator;
    int width;
    int height;
    @Inject
    private MultiplayerGameManager<Player> gameManager;
    @Inject
    private GraphicEntityModule graphicEntityModule;

    @Override
    public void init() {
        // Initialize your game here.
        maxOxygenCapacity = gameManager.getRandom().nextInt(Constants.MAX_OXYGEN_CAPACITY - Constants.MIN_OXYGEN_CAPACITY)
                + Constants.MIN_OXYGEN_CAPACITY;

        int power = gameManager.getRandom().nextInt(1) + 6;
        width = (int) Math.pow(2, power);
        height = (int) Math.pow(2, power - 1);

        generator = new BSPGenerator();
//        generator = new CellAutomataGeneration();

        generator.init((int) width, height, new Tileset(), gameManager);

        int tileSize = (int) Math.min((double) VIEWER_WIDTH / width, (double) VIEWER_HEIGHT / height);

        int[][] map = generator.generate();

        String[] underwaterSheet = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("SpriteSheet.png")
                .setHeight(128)
                .setWidth(128)
                .setName("underwater_sheet")
                .setImageCount(126)
                .setImagesPerRow(18)
                .setOrigCol(0)
                .setOrigRow(0)
                .split();
        /*
         * Sand fill: 0, 1, 18, 19
         * Sand top: 2-9, 20-27
         * Water: 88
         * Sky: 89
         * Coral: 10-17, 28-35
         * */

        graphicEntityModule.createSprite()
                .setImage("sea.jpg")
                .setX(0)
                .setY(0)
                .setScale(Math.max(VIEWER_WIDTH / 2048d, VIEWER_HEIGHT / 1536d));

        for (int y = 0; y < (VIEWER_HEIGHT / tileSize - height) + 1; y++) {
            for (int x = 0; x < width; x++) {
                graphicEntityModule.createSprite()
                        .setImage(underwaterSheet[89])
                        .setX(x * tileSize)
                        .setY((int) (y * tileSize - tileSize * 0.7))
                        .setScale(tileSize / 128d);
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int imgId = -1;
                if (map[y][x] == 1) {
                    if (y > 0) {
                        if (map[y - 1][x] == 0) {
                            imgId = gameManager.getRandom().nextInt(8) + 2;
                        } else {
                            imgId = gameManager.getRandom().nextInt(2) * 18
                                    + gameManager.getRandom().nextInt(2);
                        }
                    } else {
                        imgId = gameManager.getRandom().nextInt(8) + 2;
                    }
                }

                if (imgId != -1)
                    graphicEntityModule.createSprite()
                            .setImage(underwaterSheet[imgId])
                            .setX(x * tileSize)
                            .setY(y * tileSize + (VIEWER_HEIGHT - height * tileSize))
                            .setScale(tileSize / 128d);
            }
        }
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
