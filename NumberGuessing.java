import java.util.*;
import java.util.concurrent.TimeUnit;

// Define color codes for terminal output
class Colors {
    public static final String RESET = "\033[0m"; // Text Reset
    public static final String RED = "\033[0;31m"; // RED
    public static final String GREEN = "\033[0;32m"; // GREEN
    public static final String YELLOW = "\033[0;33m"; // YELLOW
    public static final String BLUE = "\033[0;34m"; // BLUE
}

// Define the game class
class NumberGuessingGame {
    private int numberToGuess;
    private int attemptsLeft;
    private int score;
    private long startTime;
    private boolean hintUsed;
    private boolean timedMode;
    private static Map<String, List<Integer>> leaderboard = new HashMap<>();
    private static int totalGames = 0;
    private static int totalWins = 0;

    // Constructor to initialize game settings
    public NumberGuessingGame(int maxAttempts, int range, boolean timedMode) {
        Random random = new Random();
        this.numberToGuess = random.nextInt(range) + 1; // Random number within range
        this.attemptsLeft = maxAttempts;
        this.score = maxAttempts * 10; // Starting score is based on max attempts
        this.startTime = System.currentTimeMillis(); // Start the game timer
        this.hintUsed = false; // Track if the player has used a hint
        this.timedMode = timedMode; // Track if the game is in timed mode
    }

    // Method to play the game
    public void play(String playerName) {
        Scanner scanner = new Scanner(System.in);
        long timeLimit = timedMode ? 30 : 9999; // 30 seconds limit if timed mode is enabled
        System.out.println(Colors.GREEN + "\nWelcome, " + playerName
                + "! Let's embark on an exciting guessing adventure!" + Colors.RESET);
        System.out.println(Colors.YELLOW + "---------------------------------------------------" + Colors.RESET);

        List<Integer> previousAttempts = new ArrayList<>(); // List to store previous attempts

        while (attemptsLeft > 0) {
            System.out.print("\nEnter your guess (or type 'hint' for a hint): ");
            String input = scanner.nextLine().trim().toLowerCase();

            // Handle timed mode
            if (timedMode) {
                long elapsedTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime);
                if (elapsedTime >= timeLimit) {
                    System.out.println(Colors.RED + "Time's up! You ran out of time." + Colors.RESET);
                    score = 0;
                    break;
                }
                System.out.println(
                        Colors.YELLOW + "Time remaining: " + (timeLimit - elapsedTime) + " seconds." + Colors.RESET);
            }

            // Handle hint usage
            if (input.equals("hint")) {
                if (!hintUsed) {
                    giveHint();
                    hintUsed = true; // Player can only use one hint per game
                } else {
                    System.out.println(Colors.RED + "You've already used your hint!" + Colors.RESET);
                }
                continue; // Go back to the loop for guessing
            }

            // Validate guess input
            int guess;
            try {
                guess = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(Colors.RED + "Invalid input! Please enter a valid number." + Colors.RESET);
                continue;
            }

            // Store previous attempts
            previousAttempts.add(guess);

            // Check guess against the number to guess
            if (guess == numberToGuess) {
                System.out.println(
                        Colors.GREEN + "\nCongratulations! You've guessed the correct number: " + numberToGuess + "!"
                                + Colors.RESET);
                totalWins++;
                calculateScore(); // Calculate the score based on remaining attempts
                addToLeaderboard(playerName);
                break;
            } else if (guess < numberToGuess) {
                System.out.println(Colors.YELLOW + "Too low!" + Colors.RESET);
            } else {
                System.out.println(Colors.YELLOW + "Too high!" + Colors.RESET);
            }

            attemptsLeft--;
            score -= 10; // Deduct points for each wrong attempt
            System.out.println(Colors.BLUE + "Attempts remaining: " + attemptsLeft + Colors.RESET);
        }

        // Handle game over scenario
        if (attemptsLeft == 0) {
            System.out.println(Colors.RED + "Game Over! The correct number was: " + numberToGuess + Colors.RESET);
            score = 0; // No points if the player doesn't guess the number
        }

        totalGames++;
        showStats();
        System.out.println(Colors.YELLOW + "Your previous attempts: " + previousAttempts + Colors.RESET);
    }

    // Method to provide a hint
    private void giveHint() {
        System.out.println(Colors.BLUE + "Hint: The number is between " + Math.max(1, numberToGuess - 10) + " and "
                + Math.min(100, numberToGuess + 10) + "." + Colors.RESET);
        score -= 20; // Deduct points for using a hint
        System.out.println(Colors.YELLOW + "You used a hint! 20 points deducted from your score." + Colors.RESET);
    }

    // Method to calculate score based on time and attempts
    private void calculateScore() {
        long endTime = System.currentTimeMillis();
        long timeTaken = TimeUnit.MILLISECONDS.toSeconds(endTime - startTime);
        System.out.println("You took " + timeTaken + " seconds to guess the number.");
        if (timeTaken <= 30 && !timedMode) {
            score += 50; // Bonus points for quick guesses
        }
        if (timedMode && timeTaken < 30) {
            score += 30; // Extra bonus in timed mode for fast guesses
        }

        // Bonus for fewer attempts
        score += (attemptsLeft * 5); // Add points based on remaining attempts
        System.out.println(Colors.GREEN + "Your final score: " + score + Colors.RESET);
    }

    // Add player's score to the leaderboard
    private void addToLeaderboard(String playerName) {
        leaderboard.putIfAbsent(playerName, new ArrayList<>());
        leaderboard.get(playerName).add(score);
    }

    // Show game statistics and leaderboard
    public static void showStats() {
        System.out.println("\n" + Colors.YELLOW + "Game Statistics:" + Colors.RESET);
        System.out.println("Total games played: " + totalGames);
        System.out.println("Total games won: " + totalWins);
    }

    // Show the leaderboard
    public static void showLeaderboard() {
        System.out.println("\n" + Colors.GREEN + "Leaderboard:" + Colors.RESET);
        leaderboard.forEach((player, scores) -> {
            System.out.println(player + ": " + scores);
        });
    }

    // Method to restart the game
    public static boolean restartGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nDo you want to play again? (yes/no): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        return answer.equals("yes");
    }
}

// Main class to execute the game
public class NumberGuessing {
    public static void main(String[] args) {
        boolean playAgain;
        Scanner scanner = new Scanner(System.in);

        do {
            // Ask the player for their name
            System.out.println(Colors.YELLOW + "\n======== Number Guessing Game ========" + Colors.RESET);
            System.out.print("Enter your name: ");
            String playerName = scanner.nextLine();

            // Ask the player to choose a difficulty level
            System.out.println("\nChoose difficulty level:");
            System.out.println("1. Easy (15 attempts, 1-50 range)");
            System.out.println("2. Medium (10 attempts, 1-100 range)");
            System.out.println("3. Hard (5 attempts, 1-200 range)");
            System.out.println("4. Custom (You set your own range)");
            System.out.print("Enter No. (1, 2, 3, or 4): ");

            int difficulty = 0;
            int maxAttempts = 10;
            int range = 100;
            boolean timedMode = false;

            // Handle user input for difficulty level
            try {
                difficulty = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(Colors.RED + "Invalid input! Defaulting to Medium." + Colors.RESET);
                scanner.nextLine(); // Consume the invalid input
            }

            // Set game parameters based on difficulty
            if (difficulty == 4) {
                System.out.print("Enter your custom range (e.g., 1-500): ");
                range = scanner.nextInt();
                System.out.print("Enter max attempts: ");
                maxAttempts = scanner.nextInt();
            } else {
                switch (difficulty) {
                    case 1:
                        maxAttempts = 15;
                        range = 50;
                        break;
                    case 2:
                        maxAttempts = 10;
                        range = 100;
                        break;
                    case 3:
                        maxAttempts = 5;
                        range = 200;
                        break;
                    default:
                        System.out.println(Colors.RED + "Invalid choice! Defaulting to Medium." + Colors.RESET);
                        break;
                }
            }

            // Choose game mode (timed or normal)
            System.out.print("Do you want to enable Timed Mode (30 seconds)? (yes/no): ");
            scanner.nextLine(); // consume newline
            String timedInput = scanner.nextLine().trim().toLowerCase();
            if (timedInput.equals("yes")) {
                timedMode = true;
            }

            // Start the game
            NumberGuessingGame game = new NumberGuessingGame(maxAttempts, range, timedMode);
            game.play(playerName);

            // Show the leaderboard
            NumberGuessingGame.showLeaderboard();

            // Ask if the player wants to restart the game
            playAgain = NumberGuessingGame.restartGame();
        } while (playAgain);

        System.out.println("\nThanks for playing! Goodbye.");
    }
}
