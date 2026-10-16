package UNO;

import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

/** Owns game rules and turn state. */
public class GameClass
{
    private int currentDirection;
    private int currentTurn;
    private boolean skipNext;
    private PlayerClass[] players;
    private LinkedChain<Card> deck;
    private Card currentCard;
    private String currentColor;
    private final Random random;
    final PrintStream output;

    /** Initializes a 108-card game with separate seven-card hands. */
    public GameClass()
    {
        this(
            BotDifficulty.SIMPLE,
            new Scanner(System.in),
            System.out,
            new Random());
    }


    /**
     * Create a new default GameClass object.
     * 
     * @param difficulty
     *            holds the difficulty of the bot
     * @param input
     * @param output
     * @param random
     */
    GameClass(
        BotDifficulty difficulty,
        Scanner input,
        PrintStream output,
        Random random)
    {
        this(
            new PlayerClass(input, output),
            new BotClass(difficulty),
            createDeck(),
            new NumberedCards("Red", 0),
            random,
            output);
        for (int i = 0; i < 7; i++)
        {
            drawCard(players[0]);
            drawCard(players[1]);
        }
    }


    GameClass(
        PlayerClass human,
        BotClass bot,
        LinkedChain<Card> deck,
        Card opening,
        Random random,
        PrintStream output)
    {
        if (human == null || bot == null || human == bot || deck == null
            || opening == null || random == null || output == null
            || !Card.isRegularColor(opening.getCardColor()))
        {
            throw new IllegalArgumentException(
                "A complete game state is required");
        }
        players = new PlayerClass[] { human, bot };
        this.deck = deck;
        currentCard = opening;
        currentColor = opening.getCardColor();
        this.random = random;
        this.output = output;
        currentDirection = 1;
        currentTurn = 0;
    }


    /**
     * Transfers one randomly selected deck card to a registered player.
     * 
     * @param player
     *            the recipient
     * @return the exact transferred card
     * @throws IllegalStateException
     *             if the deck is empty
     */
    public Card drawCard(PlayerClass player)
    {
        if (player == null || (player != players[0] && player != players[1]))
        {
            throw new IllegalArgumentException(
                "The player must belong to this game");
        }
        requireCards(1);
        Card card = deck.get(random.nextInt(deck.size()));
        deck.remove(card);
        player.getCardList().add(card);
        return card;
    }


    /**
     * @param card
     *            the proposed card @return whether the current rules allow it
     * @return true if the card can be placed, false if not
     */
    public boolean isValid(Card card)
    {
        if (!(card instanceof NumberedCards) && !(card instanceof SpecialCards))
        {
            return false;
        }
        if (card instanceof SpecialCards && ((SpecialCards)card).isWild())
        {
            if ("+4".equals(((SpecialCards)card).getCardType()))
            {
                Card[] hand = currentPlayer().getCardList()
                    .toArray(new Card[currentPlayer().getCardList().size()]);
                for (Card owned : hand)
                {
                    boolean wild = owned instanceof SpecialCards
                        && ((SpecialCards)owned).isWild();
                    boolean playable = owned instanceof NumberedCards
                        || owned instanceof SpecialCards;
                    if (playable && !wild
                        && currentColor.equals(owned.getCardColor()))
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        if (currentColor.equals(card.getCardColor()))
        {
            return true;
        }
        if (card instanceof NumberedCards
            && currentCard instanceof NumberedCards)
        {
            return ((NumberedCards)card)
                .getCardNumber() == ((NumberedCards)currentCard)
                    .getCardNumber();
        }
        return card instanceof SpecialCards
            && currentCard instanceof SpecialCards
            && ((SpecialCards)card).getCardType()
                .equals(((SpecialCards)currentCard).getCardType());
    }


    /**
     * @param player
     *            the player whose hand should be displayed
     */
    public void printPlayerDeck(PlayerClass player)
    {
        if (player == null)
        {
            throw new IllegalArgumentException("A player is required");
        }
        Card[] hand =
            player.getCardList().toArray(new Card[player.getCardList().size()]);
        output.println("Your hand (" + hand.length + " cards):");
        for (Card card : hand)
        {
            String description =
                card instanceof NumberedCards || card instanceof SpecialCards
                    ? card.toString()
                    : card.getCardColor() + " card (not playable)";
            output.println(
                "  " + PlayerClass.cardCode(card) + " = " + description);
        }
    }


    /**
     * @param player
     *            the player to check @return true only for a non-null empty
     *            hand
     * @return true if the player is the wins, false otherwise.
     */
    public boolean isWinner(PlayerClass player)
    {
        return player != null && player.getCardList().isEmpty();
    }


    /**
     * @return legal plays in hand order, followed by a draw; empty for a winner
     */
    public LinkedChain<Move> getLegalMoves()
    {
        LinkedChain<Move> moves = new LinkedChain<Move>();
        LinkedChain<Card> hand = currentPlayer().getCardList();
        if (hand.isEmpty())
        {
            return moves;
        }
        for (Card card : hand.toArray(new Card[hand.size()]))
        {
            if (isValid(card))
            {
                if (card instanceof SpecialCards
                    && ((SpecialCards)card).isWild())
                {
                    for (String color : Card.regularColors)
                    {
                        moves.addToEnd(Move.play(card, color));
                    }
                }
                else
                {
                    moves.addToEnd(Move.play(card, null));
                }
            }
        }
        moves.addToEnd(Move.draw());
        return moves;
    }


    /**
     * Applies one action after rechecking ownership and legality. Turn
     * advancement remains the game loop's responsibility.
     * 
     * @param move
     *            the action to perform
     * @return the played card, or the newly drawn card for a draw action
     */
    public Card applyMove(Move move)
    {
        if (move == null)
        {
            throw new IllegalArgumentException("A move is required");
        }
        if (move.isDraw())
        {
            return drawCard(currentPlayer());
        }
        Card card = move.getCard();
        if (!currentPlayer().getCardList().contains(card) || !isValid(card))
        {
            throw new IllegalArgumentException(
                "The card must be owned and legal");
        }
        SpecialCards special =
            card instanceof SpecialCards ? (SpecialCards)card : null;
        if (special != null)
        {
            if ("+2".equals(special.getCardType()))
            {
                requireCards(2);
            }
            else if ("+4".equals(special.getCardType()))
            {
                requireCards(4);
            }
        }
        currentPlayer().getCardList().remove(card);
        currentCard = card;
        currentColor = move.getChosenColor() == null
            ? card.getCardColor()
            : move.getChosenColor();
        if (special != null)
        {
            switch (special.getCardType())
            {
                case "reverse":
                    special.reverseOrder(this);
                    break;
                case "skip":
                    special.skipPlayer(this);
                    break;
                case "+2":
                    special.addTwo(this);
                    break;
                case "+4":
                    special.addFour(this, move.getChosenColor());
                    break;
                case "wild":
                    special.playedWildcard(this, move.getChosenColor());
                    break;
                default:
                    break;
            }
        }
        return card;
    }


    /** Advances in the selected direction, consuming one pending skip. */
    public void advanceTurn()
    {
        int steps = skipNext ? 2 : 1;
        currentTurn = Math
            .floorMod(currentTurn + steps * currentDirection, players.length);
        skipNext = false;
    }


    /**
     * Runs until a winner, quit/EOF, or documented deck depletion.
     * 
     * @param out
     *            destination for round-level status messages
     */
    public void run(PrintStream out)
    {
        if (out == null)
        {
            throw new IllegalArgumentException("Output is required");
        }
        while (true)
        {
            for (int i = 0; i < players.length; i++)
            {
                if (isWinner(players[i]))
                {
                    out.println((i == 0 ? "Human" : "Bot") + " wins!");
                    return;
                }
            }
            PlayerClass player = currentPlayer();
            out.println(
                "\nTop: " + currentCard + "; active color: " + currentColor);
            out.println(
                "Turn: " + (currentTurn == 0 ? "Human" : "Bot")
                    + "; direction: " + currentDirection + "; current cards: "
                    + player.getCardList().size());
            if (!(player instanceof BotClass))
            {
                printPlayerDeck(player);
            }
            try
            {
                player.playCard(this);
            }
            catch (IllegalStateException exception)
            {
                out.println("Round stopped: no cards are available.");
                return;
            }
            if (player.hasQuit())
            {
                out.println("Round ended by quit or end of input.");
                return;
            }
            if (!isWinner(player))
            {
                advanceTurn();
            }
        }
    }


    /**
     * @return the player whose turn it is currently
     */
    PlayerClass currentPlayer()
    {
        return players[currentTurn];
    }


    /**
     * @return the next player whose turn it is
     */
    PlayerClass nextPlayer()
    {
        return players[(currentTurn + currentDirection + players.length)
            % players.length];
    }


    /**
     * Ensures that the deck has enough cards to be drawn from.
     * 
     * @param count
     *            the number of cards that will be drawn from the deck.
     */
    void requireCards(int count)
    {
        if (deck.size() < count)
        {
            throw new IllegalStateException("No cards are available");
        }
    }


    /**
     * Changes the direction in which the turns are rotating
     */
    void reverseDirection()
    {
        currentDirection = -currentDirection;
    }


    /**
     * Sets skip next to true which is called in advance turn to rotate turns
     */
    void skipNextPlayer()
    {
        skipNext = true;
    }


    /**
     * Draws a certain amount of cards for the next player.
     * 
     * @param count
     *            the amount of cards to be drawn
     */
    void drawForNextPlayer(int count)
    {
        requireCards(count);
        PlayerClass recipient = nextPlayer();
        for (int i = 0; i < count; i++)
        {
            drawCard(recipient);
        }
    }


    /**
     * Changes the current color accepted.
     * 
     * @param color
     *            the color to be changed to
     */
    void setCurrentColor(String color)
    {
        if (!Card.isRegularColor(color))
        {
            throw new IllegalArgumentException("A regular color is required");
        }
        currentColor = color;
    }


    private static LinkedChain<Card> createDeck()
    {
        LinkedChain<Card> cards = new LinkedChain<Card>();
        for (String color : Card.regularColors)
        {
            // Red zero is already the opening card on the table.
            if (!"Red".equals(color))
            {
                cards.add(new NumberedCards(color, 0));
            }
            for (int copy = 0; copy < 2; copy++)
            {
                for (int number = 1; number <= 9; number++)
                {
                    cards.add(new NumberedCards(color, number));
                }
                for (String type : new String[] { "+2", "reverse", "skip" })
                {
                    cards.add(new SpecialCards(color, type));
                }
            }
        }
        for (int copy = 0; copy < 4; copy++)
        {
            cards.add(new SpecialCards("Wild", "wild"));
            cards.add(new SpecialCards("Wild", "+4"));
        }
        return cards;
    }
}
