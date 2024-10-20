import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class SkeletonMain {
    public static void main(String[] args) {

        // Uncomment this section and comment the other one to create a Solo Game
        /* Solo Game */
        // SoloGameRunner gameRunner = new SoloGameRunner();

        // Sets the player
        // gameRunner.setAgent(Player1.class);

        // Sets a test case
        // gameRunner.setTestCase("test1.json");

        /* Multiplayer Game */
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        gameRunner.setSeed(3769614187772240400L);

//        gameRunner.setSeed(0L); // uncomment line for randomness

        // Adds as many player as you need to test your game
//        gameRunner.addAgent(Agent1.class);
//        gameRunner.addAgent(Agent2.class);
        gameRunner.addAgent("python3 " + System.getProperty("user.dir") + "/config/Boss.py");
        gameRunner.addAgent("python3 " + System.getProperty("user.dir") + "/config/Boss.py");

        // Another way to add a player
        // gameRunner.addAgent("python3 /home/user/player.py");


        gameRunner.start();
    }
}
