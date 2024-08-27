package com.codingame.game;

import com.codingame.game.map_utils.MapGenerator;
import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;

import java.util.List;

import static com.codingame.game.Constants.*;

public class Referee extends AbstractReferee {

    //TODO:
    //TODO:
    //TODO:


    private Integer maxOxygenCapacity;
    private MapGenerator generator;
    private int width;
    private int height;

    @Inject
    private MultiplayerGameManager<Player> gameManager;
    @Inject
    private GraphicEntityModule graphicEntityModule;


    @Override
    public void init() {
        // Initialize your game here.
        maxOxygenCapacity = gameManager.getRandom().nextInt(Constants.MAX_OXYGEN_CAPACITY - Constants.MIN_OXYGEN_CAPACITY)
                + Constants.MIN_OXYGEN_CAPACITY;

        int exponent = gameManager.getRandom().nextInt(MAX_MAP_SIZE_EXPONENT - MIN_MAP_SIZE_EXPONENT)
                + MIN_MAP_SIZE_EXPONENT;

        width = (int) Math.pow(MAP_IS_POWER_OF, exponent);
        height = (int) Math.pow(MAP_IS_POWER_OF, exponent - 1);

        generator = BASE_MAP_GENERATOR;

        generator.init(width, height, gameManager.getRandom(), PUT_CORAL);

        int tileSize = (int) Math.min((double) VIEWER_WIDTH / width, (double) VIEWER_HEIGHT / height);

        int[][] map = generator.generate();

        String[] underwaterSheet = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage(MAIN_TS_IMG_SOURCE)
                .setHeight(MAIN_TS_TILE_SIZE)
                .setWidth(MAIN_TS_TILE_SIZE)
                .setName(MAIN_TS_NAME)
                .setImageCount(MAIN_TS_TILE_COUNT)
                .setImagesPerRow(MAIN_TS_IMG_PER_ROW)
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
                .setImage(BG_IMG_SOURCE)
                .setX(0)
                .setY(0)
                .setScale(Math.max((double) VIEWER_WIDTH / BG_WIDTH, (double) VIEWER_HEIGHT / BG_HEIGHT));

        for (int y = 0; y < (VIEWER_HEIGHT / tileSize - height) + 1; y++) {
            for (int x = 0; x < width; x++) {
                graphicEntityModule.createSprite()
                        .setImage(underwaterSheet[SKY_INDICES[0]])
                        .setX(x * tileSize)
                        .setY((int) (y * tileSize - tileSize * SKY_OFFSET))
                        .setScale((double) tileSize / MAIN_TS_TILE_SIZE);
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int imgId = -1;
                if (map[y][x] == WALL_VALUE) {
                    if (y > 0) {
                        if (map[y - 1][x] == HOLLOW_VALUE) {
                            imgId = SAND_TOP_INDICES[gameManager.getRandom().nextInt(SAND_TOP_INDICES.length)];
                        } else {
                            imgId = SAND_FILL_INDICES[gameManager.getRandom().nextInt(SAND_FILL_INDICES.length)];
                        }
                    } else {
                        imgId = SAND_TOP_INDICES[gameManager.getRandom().nextInt(SAND_TOP_INDICES.length)];
                    }
                } else if (map[y][x] == CORAL_VALUE) {
                    imgId = CORAL_INDICES[gameManager.getRandom().nextInt(CORAL_INDICES.length)];
                }

                if (imgId != -1)
                    graphicEntityModule.createSprite()
                            .setImage(underwaterSheet[imgId])
                            .setX(x * tileSize)
                            .setY(y * tileSize + (VIEWER_HEIGHT - height * tileSize))
                            .setScale((double) tileSize / MAIN_TS_TILE_SIZE);
            }
        }

        double submarineFactor = Math.min((double) tileSize / 96, (double) tileSize / 64);

        String[] submarine = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage("Atlantis/Atlantis2_SpriteAnimation.png")
                .setOrigRow(0)
                .setOrigCol(0)
                .setWidth(SUBMARINE_TILE_WIDTH)
                .setHeight(SUBMARINE_TILE_HEIGHT)
                .setImageCount(SUBMARINE_TILE_COUNT)
                .setImagesPerRow(SUBMARINE_IMG_PER_ROW)
                .setName(SUBMARINE_NAME)
                .split();

        graphicEntityModule.createSprite()
                .setImage(submarine[0])
                .setX(0 * tileSize)
                .setY(0 * tileSize + (VIEWER_HEIGHT - height * tileSize))
                .setTint(RED_COLOR)
                .setScaleX(submarineFactor)
                .setScaleY(submarineFactor);

        graphicEntityModule.createSprite()
                .setImage(submarine[0])
                .setAnchorX(1)
                .setX((width - 1) * tileSize)
                .setY(0 * tileSize + (VIEWER_HEIGHT - height * tileSize))
                .setTint(BLUE_COLOR)
                .setScaleX(-submarineFactor)
                .setScaleY(submarineFactor);
    }

    @Override
    public void gameTurn(int turn) {
        for (Player player : gameManager.getActivePlayers()) {
            player.sendInputLine("Actual Oxygen: \n" + player.getOxygenLeft());
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
            if (player.getPosition().getY() > 0) {
                player.changeOxygenLeft(-1);
            } else {
                player.setOxygenLeft(Constants.MAX_OXYGEN_CAPACITY);
            }

            if (player.getOxygenLeft() < 0) {
                player.deactivate("The player has drowned");
            }
        }
    }
}
