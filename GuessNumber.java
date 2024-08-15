import java.util.Random;
import java.util.Scanner;

class Game {
    private int number;
    private int userInput;

    public Game() {
        Random ra = new Random();
        number = ra.nextInt(100) + 1;
    }

    public int takeUserInput() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your guess (between 1 and 100): ");
        userInput = sc.nextInt();
        return userInput;
    }

    public boolean isCorrectNumber() {
        return userInput == number;
    }

    public int getNumber() {
        return number;
    }
}

public class GuessNumber {
    public static void main(String[] args) {
        Game g = new Game();
        int attempts = 0;

        while (true) {
            int userGuess = g.takeUserInput();
            attempts++;

            if (g.isCorrectNumber()) {
                System.out.println("Congratulations! You guessed the correct number in " + attempts + " attempts.");
                break;
            } else if (userGuess > g.getNumber()) {
                System.out.println("Your guess is too high.");
            } else {
                System.out.println("Your guess is too low.");
            }
        }
    }
}
