package com.codingame.game;

import com.codingame.game.map_utils.Coordinates;
import com.codingame.game.map_utils.MapGenerator;
import com.codingame.game.map_utils.TileMap;
import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import com.codingame.gameengine.core.AbstractPlayer;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.endscreen.EndScreenModule;
import com.codingame.gameengine.module.entities.BufferedGroup;
import com.codingame.gameengine.module.entities.Curve;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Text;
import com.google.inject.Inject;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<Coordinates, Text> coralValues;


    @Inject
    private MultiplayerGameManager<Player> gameManager;
    @Inject
    private GraphicEntityModule graphicEntityModule;
    @Inject
    private EndScreenModule endScreenModule;


    @Override
    public void init() {
        // Initialize your game here.
        maxOxygenCapacity = MAX_OXYGEN_CAPACITY;

        int exponent = (gameManager.getLeagueLevel() - 1) /*gameManager.getRandom().nextInt(MAX_MAP_SIZE_EXPONENT - MIN_MAP_SIZE_EXPONENT)*/
                + MIN_MAP_SIZE_EXPONENT;

        width = (int) Math.pow(MAP_IS_POWER_OF, exponent);
        height = (int) Math.pow(MAP_IS_POWER_OF, exponent - 1);

        generator = BASE_MAP_GENERATOR;

        tileMap = TileMap.create(width, height, gameManager.getRandom(), generator);

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
                    .setZIndex(3);

            player.infos = graphicEntityModule.createText()
                    .setZIndex(4)
                    .setAnchor(-1);

            //TODO: change depending on teams/individual
            i += 1;
            y = i / 2;
            x = i % 2;
        }

        int totalPlastic = 0;
        for (Coordinates pos : tileMap.getCoralPos()) {
            totalPlastic += tileMap.getPlasticCount(pos);
        }

        gameManager.addToGameSummary("--- PLAYERS ---");
        for (Player player : gameManager.getPlayers()) {
            gameManager.addToGameSummary("Player " + player.getIndex() + ": " + player.getNicknameToken());
        }
        gameManager.addToGameSummary("--- INFOS ---");
        gameManager.addToGameSummary(totalPlastic + " plastic wastes on " + tileMap.getCoralPos().length + " corals.");

        renderBackground();
        renderMap();
        renderCorals();
        renderPlayers();
    }

    @Override
    public void gameTurn(int turn) {
        Player player = gameManager.getPlayer(turn % gameManager.getPlayerCount());
        if (!player.isActive())
            return;

        gameManager.addToGameSummary("The player " + player.getIndex() + " with a score of " + player.getScore() + " :");

        /* SEND OXYGEN + SONAR */
        String[] prefix = new String[]{"y+", "x+", "y-", "x-"};

        //OXYGEN LEVEL
        player.sendInputLine(player.getOxygenLeft().toString());
        gameManager.addToGameSummary("- starts his turn with " + player.getOxygenLeft() + " oxygen left");

        //PLASTIC COUNT
        player.sendInputLine(Integer.toString(tileMap.getPlasticCount(player.getPosition())));
        gameManager.addToGameSummary("- has " + tileMap.getPlasticCount(player.getPosition()) + " plastic waste at his position");

        // SONAR
        int[] dxs = new int[]{0, 1, 0, -1};
        int[] dys = new int[]{-1, 0, 1, 0};

        int[] maxMove = new int[prefix.length];

        for (int i = 0; i < prefix.length; i++) {
            int[] furthestInDirection = tileMap.getFurthestInDirection(player.getPosition(), dxs[i], dys[i]);

            for (Player otherPlayer : gameManager.getPlayers()) {
                if (otherPlayer == player)
                    continue;

                Coordinates otherPlayerPosition = otherPlayer.getPosition();
                Coordinates playerPosition = player.getPosition();

                if (playerPosition.getX() == otherPlayerPosition.getX() || playerPosition.getY() == otherPlayerPosition.getY()) {
                    double distance = playerPosition.distanceTo(otherPlayerPosition);
                    if (distance < furthestInDirection[1]) {
                        furthestInDirection[0] = SUBMARINE_VALUE;
                        furthestInDirection[1] = (int) distance;
                    }
                }
            }

            if (furthestInDirection[0] == WALL_VALUE || furthestInDirection[0] == SUBMARINE_VALUE || furthestInDirection[0] == SURFACE_VALUE) {
                if (furthestInDirection[1] == 0) {
                    maxMove[i] = 0;
                } else {
                    maxMove[i] = 1;
                }
            }

            player.setMaxMove(maxMove);


            String furthestObject = furthestInDirection[0] == WALL_VALUE ? "WALL" :
                    furthestInDirection[0] == SUBMARINE_VALUE ? "SUBMARINE" :
                            furthestInDirection[0] == CORAL_VALUE ? "CORAL" : "SURFACE";

            player.sendInputLine(
                    prefix[i] + "=" + furthestObject + "(" + furthestInDirection[1] + "m)"
            );

            /* Sonar:
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

        player.execute();


        /* RECEVOIR OUTPUTS + DEPLACER JOUEURS */

        try {
            List<String> outputs = player.getOutputs();

            Player.Move playerMove = Player.Move.getMovementFromCode(outputs.get(0));
            if (playerMove != Player.Move.INVALID) {
                int dx = Math.max(-player.maxMove[3], Math.min(playerMove.x, player.maxMove[1]));
                int dy = Math.max(-player.maxMove[0], Math.min(playerMove.y, player.maxMove[2]));

                player.changePosition(new Coordinates(dx, dy));
            } /*else {
                player.deactivate("Player " + player.getIndex() + " has given a wrong command ! (found '" + outputs.get(0) + "')");
            }*/
            gameManager.addToGameSummary("-> executes the command: " + playerMove);

            int collectedPlastic = 0;
            int plasticAtPlayer = tileMap.getPlasticCount(player.getPosition());
            if (plasticAtPlayer > 0) {
                collectedPlastic = 1;
                tileMap.setPlasticCount(player.getPosition(), plasticAtPlayer - 1);
                player.setScore(player.getScore() + 1);
            }
            gameManager.addToGameSummary("- collects " + collectedPlastic + " plastic waste");

            player.updateOxygen();
            gameManager.addToGameSummary("- ends his turn with " + player.getOxygenLeft() + " oxygen left");

            if (player.getOxygenLeft() < 1) {
                player.setScore(-1);
                player.deactivate("The player " + player.getIndex() + " has drowned !");
            }

            renderPlayer(player);
            updateCorals();

            // Check validity of the player output and compute the new game state
        } catch (AbstractPlayer.TimeoutException e) {
            player.deactivate("The player " + player.getIndex() + " has timed out !");
        }

        if (gameManager.getActivePlayers().size() <= 1) {
            gameManager.endGame();
        }
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
                            .setZIndex(1);
                }
            }
        }
    }

    private void renderPlayer(Player player) {
        Coordinates playerPos = player.getPosition();

        player.sprite
                .setScaleX(submarineFactor * player.direction)
                .setX(playerPos.getX() * tileSize, Curve.EASE_IN)
                .setY(playerPos.getY() * tileSize + (VIEWER_HEIGHT - height * tileSize), Curve.EASE_IN);

        if (player.direction < 0) {
            player.sprite.setAnchorX(1);
        } else {
            player.sprite.setAnchorX(0);
        }

        player.infos.setText(Integer.toString(player.getOxygenLeft()))
                .setFillColor(player.getColorToken())
                .setX(playerPos.getX() * tileSize, Curve.EASE_IN)
                .setY(playerPos.getY() * tileSize + (VIEWER_HEIGHT - height * tileSize), Curve.EASE_IN);
    }

    private void renderPlayers() {
        for (Player player : gameManager.getActivePlayers()) {
            renderPlayer(player);
        }
    }

    private void updateCorals() {
        for (Coordinates coralPos : tileMap.getCoralPos()) {
            int plasticCount = tileMap.getPlasticCount(coralPos);

            Color color = new Color((int) (255 * ((double) plasticCount / MAX_CORAL)), (int) (255 * (1 - (double) plasticCount / MAX_CORAL)), 0);
            int rgb = (color.getRGB() - (0xFF << 24));

            coralValues.put(
                    coralPos,
                    coralValues.get(coralPos)
                            .setText(String.valueOf(plasticCount))
                            .setFillColor(rgb)
            );
        }
    }


    private void renderCorals() {
        coralValues = new HashMap<Coordinates, Text>();

        for (Coordinates coralPos : tileMap.getCoralPos()) {
            int x = coralPos.getX();
            int y = coralPos.getY();

            int plasticCount = tileMap.getPlasticCount(coralPos);

            Color color = new Color((int) (255 * ((double) plasticCount / MAX_CORAL)), (int) (255 * (1 - (double) plasticCount / MAX_CORAL)), 0);
            int rgb = (color.getRGB() - (0xFF << 24));


            coralValues.put(
                    coralPos,
                    graphicEntityModule.createText(Integer.toString(plasticCount))
                            .setX(x * tileSize)
                            .setY(y * tileSize + (VIEWER_HEIGHT - height * tileSize))
                            .setFillColor(rgb)
                            .setZIndex(2)
            );
        }
    }

    private void renderBackground() {
        graphicEntityModule.createSprite()
                .setImage(BG_IMG_SOURCE)
                .setX(0)
                .setY(0)
                .setScale(Math.max((double) VIEWER_WIDTH / BG_WIDTH, (double) VIEWER_HEIGHT / BG_HEIGHT))
                .setZIndex(-1);

        BufferedGroup skyGroup = graphicEntityModule.createBufferedGroup();

        for (int y = 0; y < (VIEWER_HEIGHT / tileSize - height) + 1; y++) {
            for (int x = 0; x < width; x++) {
                skyGroup.add(
                        graphicEntityModule.createSprite()
                                .setImage(underwaterSheet[SKY_INDICES[0]])
                                .setX(x * tileSize)
                                .setY((int) (y * tileSize - tileSize * SKY_OFFSET))
                                .setScale((double) tileSize / MAIN_TS_TILE_SIZE)
                                .setZIndex(0)
                );
            }
        }
    }

    @Override
    public void onEnd() {
        endScreenModule.setScores(gameManager.getPlayers().stream().mapToInt(AbstractMultiplayerPlayer::getScore).toArray());
    }
}
