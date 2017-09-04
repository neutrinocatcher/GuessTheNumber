package guru.bug.tztest;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static final Path leaderboardFilePath = Paths.get("leaderboard.txt");


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        List<GameResult> resultList = new ArrayList<>();
        do {
            GameResult result = new GameResult();
            System.out.println("Enter your name");
            result.name = scanner.next();
            int myNum = random.nextInt(100) + 1;
            System.out.println("I'm thinking a number from 1 to 100. Try to guess it!");
            boolean userWin = false;
            for (int attempt = 1; attempt <= 10; attempt++) {
                System.out.printf("Attempt #%d. Enter your guess: ", attempt);
                int userNum = scanner.nextInt();
                if (myNum > userNum) {
                    System.out.println("Your number is too low");
                } else if (myNum < userNum) {
                    System.out.println("Your number is too high");
                } else if (myNum == userNum) {
                    System.out.printf("You won! %d attempts were used.\n", attempt);
                    userWin = true;
                    result.attempts = attempt;
                    resultList.add(result);
                    break;
                }
            }
            if (!userWin) {
                System.out.printf("You lost! My number was %d\n", myNum);
            }
            resultList.sort(Comparator.<GameResult>comparingInt(r -> r.attempts).thenComparingLong(r -> r.time));
            for (GameResult r : resultList) {
                System.out.println(r.name + "  " + r.attempts);
            }
            System.out.print("Do you want to play again? (Y/n) ");
        } while (!scanner.next().equals("n"));
        System.out.println("Good bye!");
        try (Writer out = Files.newBufferedWriter(leaderboardFilePath)) {
            for (GameResult r : resultList) {
                String line = String.format("%s %d %d", r.name, r.attempts, r.time);
                out.write(line);
                out.write("\n");
            }
        } catch (IOException e) {
            System.out.println("Sorry, cannot write to file");
        }
    }
}
