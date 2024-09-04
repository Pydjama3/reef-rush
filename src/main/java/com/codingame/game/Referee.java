package com.codingame.game;

import com.codingame.game.map_utils.Coordinates;
import com.codingame.game.map_utils.MapGenerator;
import com.codingame.game.map_utils.TileMap;
import com.codingame.gameengine.core.AbstractPlayer;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.Curve;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;

import java.util.List;

import static com.codingame.game.Constants.*;

public class Referee extends AbstractReferee {
    private Integer maxOxygenCapacity;
    private MapGenerator generator;
    private int width;
    private int height;
    private TileMap tileMap;

    private int tileSize;
    private double submarineFactor;

    private String[] underwaterSheet;
    private String[] submarineSheet;


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

        tileMap = TileMap.create(width, height, gameManager.getRandom(), generator /*BASE_MAP_GENERATOR*/);

        tileSize = (int) Math.min((double) VIEWER_WIDTH / width, (double) VIEWER_HEIGHT / height);

        underwaterSheet = graphicEntityModule.createSpriteSheetSplitter()
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

        submarineFactor = Math.min((double) tileSize / SUBMARINE_TILE_WIDTH, (double) tileSize / SUBMARINE_TILE_HEIGHT);

        submarineSheet = graphicEntityModule.createSpriteSheetSplitter()
                .setSourceImage(SUBMARINE_IMG_SOURCE)
                .setOrigRow(0)
                .setOrigCol(0)
                .setWidth(SUBMARINE_TILE_WIDTH)
                .setHeight(SUBMARINE_TILE_HEIGHT)
                .setImageCount(SUBMARINE_TILE_COUNT)
                .setImagesPerRow(SUBMARINE_IMG_PER_ROW)
                .setName(SUBMARINE_NAME)
                .split();

        int playersByTeam = (gameManager.getPlayerCount() + 1) / 2;

        int i = 0;
        int x = 0;
        int y = 0;
        for (Player player : gameManager.getActivePlayers()) {
            player.setOxygenLeft(maxOxygenCapacity);

            if (x == 0) {
                player.setPosition(new Coordinates(0, (height / playersByTeam) * y)
                        .subtract(RELATIVE_SPAWN_POS));
            } else {
                player.setPosition(new Coordinates(width - 1, (height / playersByTeam) * y)
                        .subtract(RELATIVE_SPAWN_POS));
            }

            player.sprite = graphicEntityModule.createSprite()
                    .setImage(submarineSheet[x])
                    .setTint(player.getColorToken())
                    .setScaleX(submarineFactor)
                    .setScaleY(submarineFactor)
                    .setX(player.getPosition().getX() * tileSize)
                    .setY(player.getPosition().getY() * tileSize + (VIEWER_HEIGHT - height * tileSize))
                    .setZIndex(1);

            //TODO: change depending on teams/individual
            i += 1;
            y = i / 2;
            x = i % 2;
        }

        renderBackground();
        renderMap();
        renderCorals();
        renderPlayers();
    }

    @Override
    public void gameTurn(int turn) {

        // send oxygen (input)
        // send sonnar (input)
        // recevoir deplacement (output)
        // compute coraux (auto)
        // update oxygen
        // RENDER
        // Win condition


        /* SEND OXYGEN + SONAR */
//        System.out.println("Sending oxygen left and sonar infos to players...");

        String[] prefix = new String[]{"y+", "x+", "y-", "x-"};

        for (Player player : gameManager.getActivePlayers()) {
            //OXYGEN LEVEL
            player.sendInputLine(player.getOxygenLeft().toString());

            //PLASTIC COUNT
            player.sendInputLine(Integer.toString(tileMap.getPlasticCount(player.getPosition())));

            // SONAR
            int[] dxs = new int[]{0, 1, 0, -1};
            int[] dys = new int[]{-1, 0, 1, 0};

            int[] maxMove = new int[prefix.length];

            for (int i = 0; i < prefix.length; i++) {
                int[] furthestInDirection = tileMap.getFurthestInDirection(player.getPosition(), dxs[i], dys[i]);

                if (furthestInDirection[0] == WALL_VALUE) {
                    if (furthestInDirection[1] <= 1) {
                        maxMove[i] = 0;
                    } else {
                        maxMove[i] = 1;
                    }
                }

                player.setMaxMove(maxMove);

                String furthestObject = furthestInDirection[0] == WALL_VALUE ? "WALL" :
                        (furthestInDirection[0] == CORAL_VALUE ? "CORAL" : "SURFACE");

                player.sendInputLine(
                        prefix[i] + "=" + furthestObject + "(" + furthestInDirection[1] + "m)"
                );
            }

            /* Sonnar:
             *    1
             *  4   2
             *    3
             * */

            // Typical input:
            // <oxygen>
            // <plastic count beneath>
            // y+=<block type [coral, wall, none]> <distance>
            // x+=<block type [coral, wall, none]> <distance>
            // y-=<block type [coral, wall, none]> <distance>
            // x-=<block type [coral, wall, none]> <distance>

        }
//        System.out.println("=> Oxygen left and sonar infos sent !");


//        System.out.println("Executing players...");
//        for (Player player : gameManager.getActivePlayers()) {
//            player.execute();
//        }
//        System.out.println("=> Players executed !");


        /* RECEVOIR OUTPUTS + DEPLACER JOUEURS */

//        System.out.println("Receiving outputs and moving players...");
        for (Player player : gameManager.getActivePlayers()) {
            player.execute();
            try {
                List<String> outputs = player.getOutputs();

//                System.out.println(player.getIndex() + ": " + Arrays.toString(player.maxMove));

                Player.Move playerMove = Player.Move.getMovementFromCode(outputs.get(0));
                if (playerMove != Player.Move.INVALID) {
                    int dx = Math.max(-player.maxMove[3], Math.min(playerMove.x, player.maxMove[1]));
                    int dy = Math.max(-player.maxMove[0], Math.min(playerMove.y, player.maxMove[2]));

                    player.changePosition(new Coordinates(dx, dy));
                }

                player.updateOxygen();

                if (player.getOxygenLeft() < 0) {
                    player.deactivate("The player " + player.getIndex() + " has drowned !");
                }

                renderPlayer(player);

                // Check validity of the player output and compute the new game state
            } catch (AbstractPlayer.TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
//        System.out.println("=> Outputs received and players moved !");

//
//        /* COMPUTE CORAUX + UPDATE OXYGEN + (WIN CONDITION) */
//
////        System.out.println("Updating corals and players oxygen levels...");
//        for (Player player : gameManager.getActivePlayers()) {
//            player.updateOxygen();
//
//            if (player.getOxygenLeft() < 0) {
//                player.deactivate("The player " + player.getIndex() + " has drowned !");
//            }
//        }
////        System.out.println("=> Corals and players oxygen levels updated !");

//        renderPlayers();

//        graphicEntityModule.commitWorldState(1);
    }

    private void renderMap() {
        int[][] map = tileMap.getRenderedMap();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (map[y][x] != -1) {
                    graphicEntityModule.createSprite()
                            .setImage(underwaterSheet[map[y][x]])
                            .setX(x * tileSize)
                            .setY(y * tileSize + (VIEWER_HEIGHT - height * tileSize))
                            .setScale((double) tileSize / MAIN_TS_TILE_SIZE)
                            .setZIndex(0);
                }
            }
        }
    }

    private void renderPlayer(Player player) {
        Coordinates playerPos = player.getPosition();

//        System.out.println(playerPos);
//        System.out.println(new Coordinates(player.sprite.getX(), player.sprite.getY()));

        player.sprite
                .setScaleX(submarineFactor * player.direction)
                .setX(playerPos.getX() * tileSize, Curve.EASE_IN)
                .setY(playerPos.getY() * tileSize + (VIEWER_HEIGHT - height * tileSize), Curve.LINEAR);

        if (player.direction < 0) {
            player.sprite.setAnchorX(1);
        } else {
            player.sprite.setAnchorX(0);
        }
    }

    private void renderPlayers() {
        for (Player player : gameManager.getActivePlayers()) {
            renderPlayer(player);
        }
    }

    private void renderCorals() {
        for (Coordinates pos : tileMap.getCoralPos()) {
            int x = pos.getX();
            int y = pos.getY();

            graphicEntityModule.createText(Integer.toString(tileMap.getPlasticCount(pos)))
                    .setX(x * tileSize)
                    .setY(y * tileSize + (VIEWER_HEIGHT - height * tileSize))
                    .setStrokeColor(RED_COLOR)
                    .setZIndex(0);
        }
    }

    private void renderBackground() {
        graphicEntityModule.createSprite()
                .setImage(BG_IMG_SOURCE)
                .setX(0)
                .setY(0)
                .setScale(Math.max((double) VIEWER_WIDTH / BG_WIDTH, (double) VIEWER_HEIGHT / BG_HEIGHT))
                .setZIndex(-1);

        for (int y = 0; y < (VIEWER_HEIGHT / tileSize - height) + 1; y++) {
            for (int x = 0; x < width; x++) {
                graphicEntityModule.createSprite()
                        .setImage(underwaterSheet[SKY_INDICES[0]])
                        .setX(x * tileSize)
                        .setY((int) (y * tileSize - tileSize * SKY_OFFSET))
                        .setScale((double) tileSize / MAIN_TS_TILE_SIZE)
                        .setZIndex(0);
            }
        }
    }
}
