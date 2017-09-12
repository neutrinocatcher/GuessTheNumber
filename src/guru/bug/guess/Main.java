package guru.bug.guess;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Path boardPath = Paths.get("leaderboard.txt");
    private static final List<GameResult> leaderBoard = loadLeaderBoard();

    public static void main(String[] args) {
        Random random = new Random();
        do {
            GameResult gameResult = new GameResult();
            gameResult.name = askString("Enter your name: ", 3, 100);
            long t1 = System.currentTimeMillis();
            int myNum = random.nextInt(100) + 1;
            System.out.println("I'm thinking a number from 1 to 100. Try to guess it!");
            boolean userWin = false;
            for (int attempt = 1; attempt <= 10; attempt++) {
                String msg = String.format("Attempt #%d. Enter your guess: ", attempt);
                int userNum = askNumber(msg, 1, 100);
                if (myNum > userNum) {
                    System.out.println("Your number is too low");
                } else if (myNum < userNum) {
                    System.out.println("Your number is too high");
                } else if (myNum == userNum) {
                    long t2 = System.currentTimeMillis();
                    gameResult.time = t2 - t1;
                    gameResult.attempts = attempt;
                    leaderBoard.add(gameResult);
                    leaderBoard.sort(Comparator.<GameResult>comparingInt(r -> r.attempts)
                            .thenComparingLong(r -> r.time));
                    System.out.printf("You won! %d attempts were used.\n", attempt);
                    userWin = true;
                    break;
                }
            }
            if (!userWin) {
                System.out.printf("You lost! My number was %d\n", myNum);
            }

            printLeaderBoard();

            System.out.print("Do you want to play again? (Y/n) ");
        } while (!scanner.nextLine().equals("n"));
        System.out.println("Good bye!");
        storeLeaderBoard();
    }

    private static void printLeaderBoard() {
        System.out.println("Leader Board:");
        System.out.printf("\t %-10s \t\t %8s \t\t %5s\n", "Name", "Attempts", "Time");
        int maxDisplay = Math.min(4, leaderBoard.size());
        List<GameResult> top = leaderBoard.subList(0, maxDisplay);
        for (GameResult r : top) {
            System.out.printf("\t %-10s \t\t %8d \t\t %5.1f sec\n", r.name, r.attempts, r.time / 1000.0);
        }
    }

    private static String askString(String message, int min, int max) {
        while (true) {
            System.out.print(message);
            String result = scanner.nextLine();
            if (result.length() < min) {
                System.out.printf("String should not be shorter than %d characters\n", min);
            } else if (result.length() > max) {
                System.out.printf("String should not be longer than %d character\n", max);
            } else {
                return result;
            }
        }
    }

    private static int askNumber(String message, int min, int max) {
        while (true) {
            System.out.print(message);
            try {
                int result = scanner.nextInt();
                scanner.nextLine(); // clear everything what user entered after the number
                if (result < min) {
                    System.out.printf("Number should not be less than %d\n", min);
                } else if (result > max) {
                    System.out.printf("Number should not be greater than %d\n", max);

                } else {
                    return result;
                }
            } catch (InputMismatchException e) {
                String str = scanner.nextLine();
                System.out.printf("%s is not a number\n", str);
            }
        }
    }

    private static List<GameResult> loadLeaderBoard() {
        List<GameResult> list = new ArrayList<>();
        try (Scanner in = new Scanner(boardPath)) {
            while (in.hasNext()) {
                GameResult result = new GameResult();
                result.name = in.nextLine();
                result.attempts = in.nextInt();
                result.time = in.nextLong();
                in.nextLine();
                list.add(result);
            }
        } catch (IOException e) {
            System.out.println("Sorry, cannot read leader board");
        }
        return list;
    }

    private static void storeLeaderBoard() {
        try (Writer out = Files.newBufferedWriter(boardPath)) {
            for (GameResult r : leaderBoard) {
                String line = String.format("%s\n%d %d\n", r.name, r.attempts, r.time);
                out.write(line);
            }
        } catch (IOException e) {
            System.out.println("Sorry, cannot store leader board");
        }
    }

}
