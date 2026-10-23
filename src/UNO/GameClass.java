package UNO;

import java.util.Random;

/** Coordinates the deck, turns, and rules of UNO. */
public class GameClass
{
    int currentDirection;
    int currentTurn;
    PlayerClass[] players;
    LinkedChain<Card> deck;
    Card currentCard;
    String currentColor;
    boolean skipNext;
    private String pendingPenaltyMessage;
    private Random random;

    public GameClass()
    {
        this(BotDifficulty.SIMPLE);
    }


    public GameClass(BotDifficulty difficulty)
    {
        if (difficulty == null)
        {
            throw new IllegalArgumentException("Difficulty must not be null.");
        }
        currentDirection = 1;
        currentTurn = 0;
        skipNext = false;
        pendingPenaltyMessage = null;
        random = new Random();
        deck = new LinkedChain<Card>();
        players = new PlayerClass[2];
        players[0] = new PlayerClass();
        players[1] = new BotClass(difficulty);

        String[] colors = { "Red", "Blue", "Green", "Yellow" };
        for (int i = 0; i < colors.length; i++)
        {
            String color = colors[i];
            deck.addToEnd(new NumberedCards(color, 0));
            for (int number = 1; number <= 9; number++)
            {
                deck.addToEnd(new NumberedCards(color, number));
                deck.addToEnd(new NumberedCards(color, number));
            }
            for (int count = 0; count < 2; count++)
            {
                deck.addToEnd(new SpecialCards(color, "+2"));
                deck.addToEnd(new SpecialCards(color, "reverse"));
                deck.addToEnd(new SpecialCards(color, "skip"));
            }
        }
        for (int count = 0; count < 4; count++)
        {
            deck.addToEnd(new SpecialCards(null, "wild"));
            deck.addToEnd(new SpecialCards(null, "+4"));
        }

        shuffleDeck();
        for (int count = 0; count < 7; count++)
        {
            drawCard(players[0]);
            drawCard(players[1]);
        }
        setStartingCard();
    }


    private void shuffleDeck()
    {
        LinkedChain<Card> shuffled = new LinkedChain<Card>();
        while (!deck.isEmpty())
        {
            int index = random.nextInt(deck.size());
            Card card = deck.get(index);
            deck.remove(card);
            shuffled.addToEnd(card);
        }
        deck = shuffled;
    }


    private void setStartingCard()
    {
        int index = 0;
        while (index < deck.size())
        {
            Card card = deck.get(index);
            if (card.isNumberedCard())
            {
                currentCard = card;
                deck.remove(card);
                currentColor = card.getCardColor();
                return;
            }
            index++;
        }
        throw new IllegalStateException(
            "No numbered starting card is available.");
    }


    public Card drawCard(PlayerClass player)
    {
        if (player == null)
        {
            throw new IllegalArgumentException("Player must not be null.");
        }
        if (deck.isEmpty())
        {
            throw new IllegalStateException("No cards are available to draw.");
        }
        int index = random.nextInt(deck.size());
        Card card = deck.get(index);
        deck.remove(card);
        player.getCardList().addToEnd(card);
        return card;
    }


    /** Returns legal moves for the bot. */
    public LinkedChain<Move> getLegalMoves()
    {
        return getLegalMoves(players[1]);
    }


    /** Returns legal play moves and one draw move for the selected player. */
    public LinkedChain<Move> getLegalMoves(PlayerClass player)
    {
        if (player == null)
        {
            throw new IllegalArgumentException("Player must not be null.");
        }
        LinkedChain<Move> moves = new LinkedChain<Move>();
        for (int i = 0; i < player.getCardList().size(); i++)
        {
            Card card = player.getCardList().get(i);
            if (isValid(card))
            {
                if (card.isWildCard())
                {
                    String[] colors = { "Red", "Blue", "Green", "Yellow" };
                    for (int j = 0; j < colors.length; j++)
                    {
                        moves.addToEnd(Move.play(card, colors[j]));
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


    /** Applies a move for the player whose turn is active. */
    public boolean applyMove(Move move)
    {
        return applyMove(players[currentTurn], move);
    }


    /** Applies a move chosen by a player. */
    public boolean applyMove(PlayerClass player, Move move)
    {
        if (player == null || move == null)
        {
            throw new IllegalArgumentException("Player and move are required.");
        }
        if (move.isDraw())
        {
            Card drawn = drawCard(player);
            if (!player.isBot())
            {
                System.out.println("You drew: " + drawn);
            }
            if (player.isBot() && isValid(drawn))
            {
                String color = null;
                if (drawn.isWildCard())
                {
                    color = player.chooseColor();
                }
                System.out
                    .println("Bot (" + player.getTitle() + ") plays: " + drawn);
                if (color != null)
                {
                    System.out.println(
                        "Bot (" + player.getTitle() + ") chooses " + color
                            + ".");
                }
                return applyMove(player, Move.play(drawn, color));
            }
            return false;
        }

        Card card = move.getCard();
        if (!player.getCardList().contains(card) || !isValid(card))
        {
            throw new IllegalArgumentException(
                "The selected card cannot be played.");
        }
        if (card.isWildCard() && !Card.isRegularColor(move.getChosenColor()))
        {
            throw new IllegalArgumentException(
                "A wild card needs a chosen color.");
        }
        if (!card.isWildCard() && move.getChosenColor() != null)
        {
            throw new IllegalArgumentException(
                "Only wild cards choose a color.");
        }

        if (card.isSpecialCard())
        {
            if ("+2".equals(card.getCardType())) requireCards(2);
            else if ("+4".equals(card.getCardType())) requireCards(4);
        }
        player.getCardList().remove(card);
        currentCard = card;
        if (card.isSpecialCard())
        {
            if (!card.isWildCard())
            {
                currentColor = card.getCardColor();
            }
            card.playSpecialEffect(this, move.getChosenColor());
        }
        else
        {
            currentColor = card.getCardColor();
        }
        return true;
    }


    public boolean isValid(Card card)
    {
        if (card == null || currentCard == null
            || (!card.isNumberedCard() && !card.isSpecialCard()))
        {
            return false;
        }
        if (card.isWildCard())
        {
            return true;
        }
        if (card.getCardColor() != null
            && card.getCardColor().equals(currentColor))
        {
            return true;
        }
        if (card.isNumberedCard() && currentCard.isNumberedCard())
        {
            return card.getCardNumber() == currentCard.getCardNumber();
        }
        if (card.isSpecialCard() && currentCard.isSpecialCard())
        {
            return card.getCardType().equals(currentCard.getCardType());
        }
        return false;
    }


    public void printPlayerDeck(PlayerClass player)
    {
        if (player == null)
        {
            throw new IllegalArgumentException("Player must not be null.");
        }
        LinkedChain<Card> hand = player.getCardList();
        if (hand.isEmpty())
        {
            System.out.println("(no cards)");
            return;
        }
        for (int start = 0; start < hand.size(); start += 4)
        {
            Card[] row = new Card[Math.min(4, hand.size() - start)];
            for (int i = 0; i < row.length; i++)
            {
                row[i] = hand.get(start + i);
            }
            printCardRow(row, start + 1);
            System.out.println();
        }
    }


    void printTable()
    {
        System.out.println("------------------------------ TABLE ------------------------------");
        printCardRow(new Card[] {currentCard}, 0);
        System.out.println("Active color: " + currentColor
            + "    |    Draw pile: " + deck.size() + " cards");
        System.out.println();
    }


    private static void printCardRow(Card[] cards, int firstIndex)
    {
        String[][] pictures = new String[cards.length][];
        for (int i = 0; i < cards.length; i++)
        {
            Card card = cards[i];
            String color = card.isWildCard() ? "WILD"
                : card.getCardColor().toUpperCase(java.util.Locale.ROOT);
            String value = card.isNumberedCard()
                ? String.valueOf(card.getCardNumber())
                : card.isSpecialCard()
                    ? card.getCardType().toUpperCase(java.util.Locale.ROOT) : "CARD";
            int left = (15 - value.length()) / 2;
            String spaces = "               ";
            String middle = "|" + spaces.substring(0, left) + value
                + spaces.substring(0, 15 - value.length() - left) + "|";
            String label = firstIndex == 0 ? "       TOP"
                : "      [" + (firstIndex + i) + "]";
            pictures[i] = new String[] {
                String.format("%-17s", label),
                "+---------------+",
                String.format("|%-15s|", " " + color),
                "|               |",
                middle,
                "|               |",
                String.format("|%15s|", color + " "),
                "+---------------+"
            };
        }
        for (int line = 0; line < pictures[0].length; line++)
        {
            StringBuilder row = new StringBuilder();
            for (int i = 0; i < pictures.length; i++)
            {
                if (i > 0) row.append("  ");
                row.append(pictures[i][line]);
            }
            System.out.println(row);
        }
    }


    public boolean isWinner(PlayerClass player)
    {
        return player != null && player.getCardList().isEmpty();
    }


    void reverseDirection()
    {
        currentDirection = -currentDirection;
        if (players.length == 2)
        {
            skipNext = true;
        }
    }


    void skipNextPlayer()
    {
        skipNext = true;
    }


    void requireCards(int count)
    {
        if (deck.size() < count)
        {
            throw new IllegalStateException("Not enough cards are available to draw.");
        }
    }


    void drawForNextPlayer(int numberOfCards)
    {
        requireCards(numberOfCards);
        int next =
            (currentTurn + currentDirection + players.length) % players.length;
        for (int i = 0; i < numberOfCards; i++)
        {
            drawCard(players[next]);
        }
        if (players[next].isBot())
        {
            pendingPenaltyMessage =
                players[next].getTitle() + " was forced to draw "
                    + numberOfCards + " cards and its turn was skipped.";
        }
        else
        {
            pendingPenaltyMessage = "You were forced to draw " + numberOfCards
                + " cards and your turn was skipped.";
        }
        skipNext = true;
    }


    void printPendingPenaltyMessage()
    {
        if (pendingPenaltyMessage != null)
        {
            System.out.println(pendingPenaltyMessage);
            pendingPenaltyMessage = null;
        }
    }


    void setCurrentColor(String color)
    {
        if (color == null || !(color.equals("Red") || color.equals("Blue")
            || color.equals("Green") || color.equals("Yellow")))
        {
            throw new IllegalArgumentException("Unsupported card color.");
        }
        currentColor = color;
    }
}
