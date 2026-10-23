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
        System.out.println();
        System.out.println("UU     UU  NNN    NN   OOOOOOO");
        System.out.println("UU     UU  NNNN   NN  OO     OO");
        System.out.println("UU     UU  NN NN  NN  OO     OO");
        System.out.println("UU     UU  NN  NN NN  OO     OO");
        System.out.println("UU     UU  NN   NNNN  OO     OO");
        System.out.println(" UU   UU   NN    NNN  OO     OO");
        System.out.println(" UUUUUUU   NN    NNN   OOOOOOO");
        System.out.println();
        System.out.println("Welcome to UNO!");
        System.out.println("HOW TO PLAY");
        System.out.println("  [1], [2], ...  Enter the number above a card to play it.");
        System.out.println("  D              Draw a card.");
        System.out.println("  Y / N          Play a legal draw, or keep it and end your turn.");
        System.out.println("  Wild / +4      Choose Red, Blue, Green, or Yellow.");
        System.out.println();

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
