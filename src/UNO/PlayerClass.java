package UNO;

import java.io.PrintStream;
import java.util.Scanner;
import java.util.Locale;

/** A human player with a separately owned hand. */
public class PlayerClass
{
    private final LinkedChain<Card> cardHand = new LinkedChain<Card>();
    private final Scanner input;
    private final PrintStream output;
    private boolean quit;

    /** Creates a human player using the console. */
    public PlayerClass()
    {
        this(new Scanner(System.in), System.out);
    }

    PlayerClass(Scanner input, PrintStream output)
    {
        if (input == null || output == null)
        {
            throw new IllegalArgumentException("Input and output are required");
        }
        this.input = input;
        this.output = output;
    }

    /** @return this player's existing, separately owned hand */
    public LinkedChain<Card> getCardList()
    {
        return cardHand;
    }
    /** Reads one turn, retrying rejected input without changing the game.
     * A playable drawn card is immediately played; wild draws request a color.
     * @param game this player's active game
     * @return whether a card was played, rather than passing or quitting
     */
    public boolean playCard(GameClass game)
    {
        if (game == null || game.currentPlayer() != this)
        {
            throw new IllegalArgumentException("This must be the player's turn");
        }
        if (quit || cardHand.isEmpty())
        {
            return false;
        }
        while (true)
        {
            output.println("Enter a card code, D to draw, or Q to quit:");
            String code = readLine();
            if (quit)
            {
                return false;
            }
            code = code.toUpperCase(Locale.ROOT);
            if ("D".equals(code))
            {
                Card drawn = game.applyMove(Move.draw());
                output.println("You drew: " + cardCode(drawn) + " = " + drawn);
                if (!game.isValid(drawn))
                {
                    output.println("The drawn card cannot be played. Turn ends.");
                    return false;
                }
                return playSelected(game, drawn);
            }
            if (!code.matches("[RBGY][0-9+RS]|W[W4]"))
            {
                output.println("Invalid input");
                continue;
            }
            Card selected = null;
            for (Card card : cardHand.toArray(new Card[cardHand.size()]))
            {
                if (code.equals(cardCode(card)))
                {
                    selected = card;
                    break;
                }
            }
            if (selected == null)
            {
                output.println("You do not have that card");
            }
            else if (!game.isValid(selected))
            {
                output.println("You cannot play that card");
            }
            else
            {
                return playSelected(game, selected);
            }
        }
    }

    private boolean playSelected(GameClass game, Card card)
    {
        String color = null;
        if (card instanceof SpecialCards && ((SpecialCards)card).isWild())
        {
            while (!Card.isRegularColor(color))
            {
                output.println("Choose Red, Blue, Green, Yellow (or Q to quit):");
                String answer = readLine();
                if (quit)
                {
                    return false;
                }
                for (String option : Card.regularColors())
                {
                    if (option.equalsIgnoreCase(answer)
                        || option.substring(0, 1).equalsIgnoreCase(answer))
                    {
                        color = option;
                        break;
                    }
                }
                if (color == null)
                {
                    output.println("Invalid input");
                }
            }
        }
        game.applyMove(Move.play(card, color));
        output.println("You played: " + card);
        return true;
    }

    private String readLine()
    {
        if (!input.hasNextLine())
        {
            quit = true;
            return "";
        }
        String answer = input.nextLine().trim();
        if ("Q".equalsIgnoreCase(answer))
        {
            quit = true;
        }
        return answer;
    }
    boolean hasQuit()
    {
        return quit;
    }

    static String cardCode(Card card)
    {
        if (card instanceof NumberedCards)
        {
            return card.getCardColor().substring(0, 1)
                + ((NumberedCards)card).getCardNumber();
        }
        SpecialCards special = (SpecialCards)card;
        if (special.isWild())
        {
            return "+4".equals(special.getCardType()) ? "W4" : "WW";
        }
        String type = special.getCardType();
        String symbol = "+2".equals(type) ? "+"
            : "skip".equals(type) ? "S" : "R";
        return card.getCardColor().substring(0, 1) + symbol;
    }
}
