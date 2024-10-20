import java.util.Scanner;

public class Agent1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            for (int i = 0; i < 6; i++) {
                String input = scanner.nextLine();
            }

            System.out.println("RIGHT"/*Move.values()[new Random().nextInt(5)].code*/);
        }
    }
}
