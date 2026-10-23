package UNO;

import java.util.Random;

/**
 * Coordinates the deck, turns, player state, and game logic of UNO.
 */
public class GameClass
{
    // The optional house rules configured for the current game
    private HouseRules houseRules;
    
    // Direction of play (1 for forward, -1 for reverse)
    int currentDirection;
    
    // Index of the player whose turn is currently active
    int currentTurn;
    
    // Array storing the players in the game (Human and Bot)
    PlayerClass[] players;
    
    // The main deck of cards available to draw from
    LinkedChain<Card> deck;
    
    // The card currently sitting on top of the discard pile
    Card currentCard;
    
    // The active color in play (relevant for wild cards)
    String currentColor;
    
    // Flag indicating whether the next player's turn should be skipped
    boolean skipNext;
    
    // Message to display regarding forced penalty draws
    private String pendingPenaltyMessage;
    
    // Random number generator for shuffling and drawing
    private Random random;
    
    // Tracks the accumulated draw penalty count when stacking is active
    private int stackedDrawCount;

    /**
     * Constructs a default game using Simple bot difficulty and default house rules.
     */
    public GameClass()
    {
        this(BotDifficulty.SIMPLE);
    }

    /**
     * Constructs a game with the specified bot difficulty and default house rules.
     * 
     * @param difficulty the difficulty level for the computer player
     */
    public GameClass(BotDifficulty difficulty)
    {
        this(difficulty, new HouseRules());
    }

    /**
     * Constructs a game with custom bot difficulty and customized house rules.
     * 
     * @param difficulty the difficulty level for the computer player
     * @param rules the configured house rules for this match
     */
    public GameClass(BotDifficulty difficulty, HouseRules rules)
    {
        if (difficulty == null)
        {
            throw new IllegalArgumentException("Difficulty must not be null.");
        }
        if (rules == null)
        {
            throw new IllegalArgumentException("House rules must not be null.");
        }
        houseRules = rules;
        currentDirection = 1;
        currentTurn = 0;
        skipNext = false;
        pendingPenaltyMessage = null;
        stackedDrawCount = 0;
        random = new Random();
        deck = new LinkedChain<Card>();
        players = new PlayerClass[2];
        players[0] = new PlayerClass();
        players[1] = new BotClass(difficulty);

        // Build the deck with standard colors, numbers, and special/wild cards
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

        // Shuffle deck and deal initial 7 cards to each player
        shuffleDeck();
        for (int count = 0; count < 7; count++)
        {
            drawCard(players[0]);
            drawCard(players[1]);
        }
        setStartingCard();
    }

    /**
     * Randomly shuffles the cards currently in the deck.
     */
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

    /**
     * Selects a numbered card from the deck to serve as the initial discard pile card.
     */
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

    /**
     * Draws a random card from the deck and adds it to the specified player's hand.
     * 
     * @param player the player drawing the card
     * @return the card that was drawn
     */
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

    /**
     * Returns legal moves for the computer player.
     * 
     * @return list of legal moves for the bot
     */
    public LinkedChain<Move> getLegalMoves()
    {
        return getLegalMoves(players[1]);
    }

    /**
     * Returns legal play moves and draw options for the selected player.
     * 
     * @param player the player whose legal moves are being checked
     * @return list of legal moves available
     */
    public LinkedChain<Move> getLegalMoves(PlayerClass player)
    {
        if (player == null)
        {
            throw new IllegalArgumentException("Player must not be null.");
        }
        LinkedChain<Move> moves = new LinkedChain<Move>();

        // If stacking house rule is active and penalties are accumulated, restrict moves to stacking response cards
        if (houseRules.isStackingPlus() && stackedDrawCount > 0)
        {
            for (int i = 0; i < player.getCardList().size(); i++)
            {
                Card card = player.getCardList().get(i);
                if (card.isSpecialCard() && (card.getCardType().equals("+2") || card.getCardType().equals("+4")))
                {
                    if (card.isWildCard())
                    {
                        String[] colors = { "Red", "Blue", "Green", "Yellow" };
                        for (String col : colors)
                        {
                            moves.addToEnd(Move.play(card, col));
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

        // Standard legal move evaluation for regular turns
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

    /**
     * Applies a move for whichever player's turn is currently active.
     * 
     * @param move the move to apply
     * @return true if a card was played, false otherwise
     */
    public boolean applyMove(Move move)
    {
        return applyMove(players[currentTurn], move);
    }

    /**
     * Applies a specific move chosen by a player, factoring in house rules.
     * 
     * @param player the player making the move
     * @param move the move to apply
     * @return true if a card was played, false otherwise
     */
    public boolean applyMove(PlayerClass player, Move move)
    {
        if (player == null || move == null)
        {
            throw new IllegalArgumentException("Player and move are required.");
        }
        
        // Handle draw actions
        if (move.isDraw())
        {
            // Resolve stacked penalty if stacking is enabled and penalty exists
            if (stackedDrawCount > 0 && houseRules.isStackingPlus())
            {
                resolveStackedDraw(player);
                return false;
            }

            // Handle draw-until-match house rule behavior
            if (houseRules.isDrawTillMatch())
            {
                Card drawn = null;
                int drawCount = 0;
                while (!deck.isEmpty())
                {
                    drawn = drawCard(player);
                    drawCount++;
                    if (isValid(drawn))
                    {
                        break;
                    }
                }
                
                if (!player.isBot())
                {
                    System.out.println("You drew " + drawCount + " card(s). Last drawn: " + drawn);
                }

                if (drawn != null && isValid(drawn))
                {
                    String color = null;
                    if (drawn.isWildCard())
                    {
                        color = player.chooseColor();
                    }
                    if (player.isBot())
                    {
                        System.out.println("Bot (" + player.getTitle() + ") drew " + drawCount 
                            + " card(s) and plays: " + drawn);
                        if (color != null)
                        {
                            System.out.println("Bot (" + player.getTitle() + ") chooses " + color + ".");
                        }
                    }
                    return applyMove(player, Move.play(drawn, color));
                }
                return false;
            }

            // Standard single card draw
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
                System.out.println("Bot (" + player.getTitle() + ") plays: " + drawn);
                if (color != null)
                {
                    System.out.println("Bot (" + player.getTitle() + ") chooses " + color + ".");
                }
                return applyMove(player, Move.play(drawn, color));
            }
            return false;
        }

        // Validate card and color choices for a play move
        Card card = move.getCard();
        if (!player.getCardList().contains(card) || !isValid(card))
        {
            throw new IllegalArgumentException("The selected card cannot be played.");
        }
        if (card.isWildCard() && !Card.isRegularColor(move.getChosenColor()))
        {
            throw new IllegalArgumentException("A wild card needs a chosen color.");
        }
        if (!card.isWildCard() && move.getChosenColor() != null)
        {
            throw new IllegalArgumentException("Only wild cards choose a color.");
        }

        // Handle special card penalty accumulations (e.g., stacking +2 / +4)
        if (card.isSpecialCard())
        {
            if ("+2".equals(card.getCardType()))
            {
                if (houseRules.isStackingPlus())
                {
                    stackedDrawCount += 2;
                }
                else
                {
                    requireCards(2);
                }
            }
            else if ("+4".equals(card.getCardType()))
            {
                if (houseRules.isStackingPlus())
                {
                    stackedDrawCount += 4;
                }
                else
                {
                    requireCards(4);
                }
            }
        }

        // Remove card from player's hand and update table state
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

        // Check for 7-Swap house rule trigger (playing a number 7 swaps hands)
        if (houseRules.isSevenSwap() && card.isNumberedCard() && card.getCardNumber() == 7)
        {
            swapHands();
        }

        return true;
    }

    /**
     * Swaps hands between player 0 and player 1 when the 7-swap rule is triggered.
     */
    private void swapHands()
    {
        LinkedChain<Card> hand1 = players[0].getCardList();
        LinkedChain<Card> hand2 = players[1].getCardList();
        
        LinkedChain<Card> temp = new LinkedChain<Card>();
        
        while (!hand1.isEmpty())
        {
            Card c = hand1.get(0);
            hand1.remove(c);
            temp.addToEnd(c);
        }
        
        while (!hand2.isEmpty())
        {
            Card c = hand2.get(0);
            hand2.remove(c);
            hand1.addToEnd(c);
        }
        
        while (!temp.isEmpty())
        {
            Card c = temp.get(0);
            temp.remove(c);
            hand2.addToEnd(c);
        }
        
        System.out.println("A 7 was played! Hands have been swapped between players!");
    }

    /**
     * Forces a player to draw the accumulated stacked penalty cards.
     * 
     * @param player the player receiving the stacked penalty
     */
    private void resolveStackedDraw(PlayerClass player)
    {
        requireCards(stackedDrawCount);
        for (int i = 0; i < stackedDrawCount; i++)
        {
            drawCard(player);
        }
        if (player.isBot())
        {
            pendingPenaltyMessage = player.getTitle() + " was forced to draw "
                + stackedDrawCount + " cards due to stacking.";
        }
        else
        {
            pendingPenaltyMessage = "You were forced to draw " + stackedDrawCount
                + " cards due to stacking.";
        }
        stackedDrawCount = 0;
        skipNext = true;
    }

    /**
     * Determines whether a given card can legally be played on the current table card.
     * 
     * @param card the card to check
     * @return true if playable, false otherwise
     */
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

    /**
     * Prints a player's card hand in formatted rows.
     * 
     * @param player the player whose hand is displayed
     */
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

    /**
     * Prints the current table state, including top card, active color, and pile sizes.
     */
    void printTable()
    {
        System.out.println("------------------------------ TABLE ------------------------------");
        printCardRow(new Card[] {currentCard}, 0);
        System.out.println("Active color: " + currentColor
            + "    |    Draw pile: " + deck.size() + " cards");
        if (stackedDrawCount > 0)
        {
            System.out.println("Stacked Penalty: +" + stackedDrawCount + " cards pending");
        }
        System.out.println();
    }

    /**
     * Helper method to visually render a row of card graphics.
     */
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

    /**
     * Checks if a player has won the game (i.e., hand is empty).
     * 
     * @param player the player to check
     * @return true if the player has won, false otherwise
     */
    public boolean isWinner(PlayerClass player)
    {
        return player != null && player.getCardList().isEmpty();
    }

    /**
     * Reverses the direction of play.
     */
    void reverseDirection()
    {
        currentDirection = -currentDirection;
        if (players.length == 2)
        {
            skipNext = true;
        }
    }

    /**
     * Flags the next player's turn to be skipped.
     */
    void skipNextPlayer()
    {
        skipNext = true;
    }

    /**
     * Verifies that enough cards remain in the deck to fulfill draw requirements.
     * 
     * @param count the number of cards required
     */
    void requireCards(int count)
    {
        if (deck.size() < count)
        {
            throw new IllegalStateException("Not enough cards are available to draw.");
        }
    }

    /**
     * Forces the next player to draw a specified number of penalty cards.
     * 
     * @param numberOfCards number of cards to draw
     */
    void drawForNextPlayer(int numberOfCards)
    {
        if (houseRules.isStackingPlus())
        {
            return;
        }
        requireCards(numberOfCards);
        int next = (currentTurn + currentDirection + players.length) % players.length;
        for (int i = 0; i < numberOfCards; i++)
        {
            drawCard(players[next]);
        }
        if (players[next].isBot())
        {
            pendingPenaltyMessage = players[next].getTitle() + " was forced to draw "
                + numberOfCards + " cards and its turn was skipped.";
        }
        else
        {
            pendingPenaltyMessage = "You were forced to draw " + numberOfCards
                + " cards and your turn was skipped.";
        }
        skipNext = true;
    }

    /**
     * Prints and clears any pending penalty message.
     */
    void printPendingPenaltyMessage()
    {
        if (pendingPenaltyMessage != null)
        {
            System.out.println(pendingPenaltyMessage);
            pendingPenaltyMessage = null;
        }
    }

    /**
     * Sets the active color for wild cards.
     * 
     * @param color the color string to set
     */
    void setCurrentColor(String color)
    {
        if (color == null || !(color.equals("Red") || color.equals("Blue")
            || color.equals("Green") || color.equals("Yellow")))
        {
            throw new IllegalArgumentException("Unsupported card color.");
        }
        currentColor = color;
    }

    /**
     * Returns the house rules instance for this game.
     * 
     * @return the house rules
     */
    public HouseRules getHouseRules()
    {
        return houseRules;
    }

    /**
     * Sets the house rules for this game.
     * 
     * @param rules the house rules to apply
     */
    public void setHouseRules(HouseRules rules)
    {
        if (rules == null)
        {
            throw new IllegalArgumentException("House rules must not be null.");
        }
        houseRules = rules;
    }
}