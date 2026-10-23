package UNO;

import java.util.Scanner;
import java.util.NoSuchElementException;

/** Starts and runs the UNO game. */
public class UnoGame
{
    public static void main(String[] args)
    {
        Scanner input = PlayerClass.INPUT;
        boolean playAgain = true;
        System.out.println("Welcome to UNO!");
        System.out.println("How to enter your moves:");
        System.out.println(
            "- Your hand is shown as a numbered list. Type the number beside the card you want to play.");
        System.out.println(
            "  Example: if the list says '3. Yellow 5', type 3 to play Yellow 5.");
        System.out.println("- Type D to draw a card.");
        System.out.println(
            "- If the drawn card is playable, type Y to play it or N to keep it and end your turn.");
        System.out.println(
            "- For Wild and +4 cards, type a color name: Red, Blue, Green, or Yellow.");
        System.out.println(
            "- Card names look like 'Yellow 5', 'Blue +2', 'Red skip', 'Wild', or '+4'.");

        while (playAgain)
        {
            System.out.println();
            System.out.println("Choose the bot difficulty:");
            System.out.println("1. Simple");
            System.out.println("2. Normal");
            System.out.println("3. Master");

            String choice = "";
            BotDifficulty difficulty = null;
            while (difficulty == null)
            {
                System.out.print("Enter 1, 2, or 3: ");
                if (input.hasNextLine())
                {
                    choice = input.nextLine().trim();
                }
                if (choice.equals("1") || choice.equalsIgnoreCase("simple"))
                {
                    difficulty = BotDifficulty.SIMPLE;
                }
                else if (choice.equals("2")
                    || choice.equalsIgnoreCase("normal"))
                {
                    difficulty = BotDifficulty.NORMAL;
                }
                else if (choice.equals("3")
                    || choice.equalsIgnoreCase("master"))
                {
                    difficulty = BotDifficulty.MASTER;
                }
                else if (!input.hasNextLine())
                {
                    System.out.println("No input available. Ending the game.");
                    return;
                }
                else
                {
                    System.out.println(
                        "Invalid choice. Please choose Simple, Normal, or Master.");
                }
            }

            GameClass game = new GameClass(difficulty);
            System.out.println("Bot difficulty: " + difficulty);
            System.out.println("You are Player 1. The bot is Player 2.");
            System.out.println("Starting card: " + game.currentCard);

            boolean running = true;
            boolean winnerFound = false;
            while (running)
            {
                PlayerClass player = game.players[game.currentTurn];
                System.out.println();
                System.out.println(
                    player.isBot()
                        ? player.getTitle() + "'s turn."
                        : "Your turn.");
                boolean played = false;
                try
                {
                    played = player.playCard(game);
                    game.printPendingPenaltyMessage();
                }
                catch (NoSuchElementException exception)
                {
                    System.out.println("Input ended. Ending the round.");
                    running = false;
                }
                catch (IllegalStateException exception)
                {
                    System.out
                        .println("The round ends: " + exception.getMessage());
                    running = false;
                }
                if (game.isWinner(player))
                {
                    System.out.println(
                        player.isBot()
                            ? player.getTitle() + " wins! You lose."
                            : "You win!");
                    winnerFound = true;
                    running = false;
                }
                if (!played)
                {
                    System.out.println(
                        player.isBot()
                            ? player.getTitle() + " does not play a card."
                            : "No card played.");
                }
                if (game.deck.isEmpty() && running)
                {
                    System.out
                        .println("The draw pile is empty. The round ends.");
                    running = false;
                }
                if (running)
                {
                    if (game.skipNext)
                    {
                        game.skipNext = false;
                        boolean penaltyCard = game.currentCard.isSpecialCard()
                            && (game.currentCard.getCardType().equals("+2")
                                || game.currentCard.getCardType().equals("+4"));
                        game.currentTurn =
                            (game.currentTurn + 2 * game.currentDirection
                                + game.players.length) % game.players.length;
                    }
                    else
                    {
                        game.currentTurn =
                            (game.currentTurn + game.currentDirection
                                + game.players.length) % game.players.length;
                    }
                }
                System.out.println("----------------------------------------");
            }

            if (!winnerFound)
            {
                System.out.println("Game over. No winner this round.");
            }
            String answer = "";
            while (!answer.equalsIgnoreCase("y")
                && !answer.equalsIgnoreCase("n")
                && !answer.equalsIgnoreCase("yes")
                && !answer.equalsIgnoreCase("no"))
            {
                System.out.print("Would you like to play again? (Y/N): ");
                if (input.hasNextLine())
                {
                    answer = input.nextLine().trim();
                }
                else
                {
                    answer = "n";
                }
                if (!answer.equalsIgnoreCase("y")
                    && !answer.equalsIgnoreCase("n")
                    && !answer.equalsIgnoreCase("yes")
                    && !answer.equalsIgnoreCase("no"))
                {
                    System.out.println("Invalid input. Please enter Y or N.");
                }
            }
            playAgain =
                answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes");
        }
        System.out.println("Thanks for playing!");
    }
}
