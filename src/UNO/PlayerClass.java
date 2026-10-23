package UNO;

import java.util.Scanner;

/** Represents the human player's hand and decisions. */
public class PlayerClass
{
    protected LinkedChain<Card> cardHand;
    protected static final Scanner INPUT = new Scanner(System.in);
    private final Scanner input;

    public PlayerClass()
    {
        this(INPUT);
    }


    PlayerClass(Scanner input)
    {
        if (input == null)
        {
            throw new IllegalArgumentException("Input is required.");
        }
        this.input = input;
        cardHand = new LinkedChain<Card>();
    }


    public LinkedChain<Card> getCardList()
    {
        return cardHand;
    }


    /** Returns whether this player is controlled by the computer. */
    public boolean isBot()
    {
        return false;
    }


    /** Returns the display title for this player. */
    public String getTitle()
    {
        return "You";
    }


    /** Chooses a color for a wild card; bots override this method. */
    public String chooseColor()
    {
        return askForColor();
    }


    public boolean playCard(GameClass game)
    {
        if (game == null)
        {
            throw new IllegalArgumentException("Game must not be null.");
        }
        while (true)
        {
            System.out.println("Your hand:");
            game.printPlayerDeck(this);
            System.out.println(
                "Current card: " + game.currentCard + " | Required color: "
                    + game.currentColor);
            System.out.println(
                game.players[1].getTitle() + " has "
                    + game.players[1].getCardList().size() + " cards.");
            System.out.print("Enter a card number to play, or D to draw: ");
            String input = this.input.nextLine().trim();
            if (input.isEmpty())
            {
                System.out.println("Invalid input.");
                continue;
            }
            if (input.equalsIgnoreCase("D"))
            {
                try
                {
                    Card drawn = game.drawCard(this);
                    System.out.println("You drew: " + drawn);
                    if (game.isValid(drawn))
                    {
                        String answer = "";
                        while (!answer.equalsIgnoreCase("y")
                            && !answer.equalsIgnoreCase("n")
                            && !answer.equalsIgnoreCase("yes")
                            && !answer.equalsIgnoreCase("no"))
                        {
                            System.out.print("Play this card? (Y/N): ");
                            answer = this.input.nextLine().trim();
                            if (!answer.equalsIgnoreCase("y")
                                && !answer.equalsIgnoreCase("n")
                                && !answer.equalsIgnoreCase("yes")
                                && !answer.equalsIgnoreCase("no"))
                            {
                                System.out.println(
                                    "Invalid input. Please enter Y or N.");
                            }
                        }
                        if (answer.equalsIgnoreCase("y")
                            || answer.equalsIgnoreCase("yes"))
                        {
                            return playSpecificCard(game, drawn);
                        }
                    }
                    return false;
                }
                catch (IllegalStateException exception)
                {
                    System.out
                        .println("No cards are available. The round ends.");
                    return false;
                }
            }
            try
            {
                int index = Integer.parseInt(input) - 1;
                if (index < 0 || index >= cardHand.size())
                {
                    System.out.println("Invalid input.");
                    continue;
                }
                Card selected = cardHand.get(index);
                if (!game.isValid(selected))
                {
                    System.out.println("You cannot play that card.");
                    continue;
                }
                return playSpecificCard(game, selected);
            }
            catch (NumberFormatException exception)
            {
                System.out.println("Invalid input.");
            }
        }
    }


    protected boolean playSpecificCard(GameClass game, Card card)
    {
        if (!game.isValid(card))
        {
            System.out.println("You cannot play that card.");
            return false;
        }
        String chosenColor = null;
        if (card.isWildCard())
        {
            chosenColor = askForColor();
        }
        game.applyMove(this, Move.play(card, chosenColor));
        System.out.println("You played: " + card);
        if (chosenColor != null)
        {
            System.out.println("You chose: " + chosenColor);
        }
        return true;
    }


    protected String askForColor()
    {
        while (true)
        {
            System.out.print("Choose a color (Red, Blue, Green, Yellow): ");
            String color = this.input.nextLine().trim();
            if (color.equalsIgnoreCase("red"))
                return "Red";
            if (color.equalsIgnoreCase("blue"))
                return "Blue";
            if (color.equalsIgnoreCase("green"))
                return "Green";
            if (color.equalsIgnoreCase("yellow"))
                return "Yellow";
            System.out
                .println("Invalid color. Choose Red, Blue, Green, or Yellow.");
        }
    }
}
