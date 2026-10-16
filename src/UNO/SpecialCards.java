package UNO;

/** An action card, including wild and draw-four cards. */
public class SpecialCards
    extends Card
{
    private final String cardType;

    /**
     * @param color
     *            a card color @param type +2, +4, reverse, skip, or wild
     */
    public SpecialCards(String color, String type)
    {
        super(color);
        if (!"+2".equals(type) && !"+4".equals(type) && !"reverse".equals(type)
            && !"skip".equals(type) && !"wild".equals(type))
        {
            throw new IllegalArgumentException("Unsupported special card type");
        }
        cardType = type;
        setCardColor(color);
    }


    /** @return the special action type, unchanged by selecting a color */
    public String getCardType()
    {
        return cardType;
    }


    @Override
    public void setCardColor(String color)
    {
        if (!isRegularColor(color) && !(isWild() && "Wild".equals(color)))
        {
            throw new IllegalArgumentException(
                "Unsupported color for this card type");
        }
        super.setCardColor(color);
    }


    /**
     * @param game
     *            the game whose direction changes
     */
    public void reverseOrder(GameClass game)
    {
        requireGame(game);
        game.reverseDirection();
    }


    /**
     * @param game
     *            the game whose next player is skipped
     */
    public void skipPlayer(GameClass game)
    {
        requireGame(game);
        game.skipNextPlayer();
    }


    /**
     * @param game
     *            the game in which the opponent draws two and loses a turn
     */
    public void addTwo(GameClass game)
    {
        requireGame(game);
        game.drawForNextPlayer(2);
        game.skipNextPlayer();
    }


    /**
     * @param game
     *            the game to update @param color a chosen regular color
     */
    public void playedWildcard(GameClass game, String color)
    {
        requireGame(game);
        if (!isRegularColor(color))
        {
            throw new IllegalArgumentException(
                "Choose Red, Blue, Green, or Yellow");
        }
        setCardColor(color);
        game.setCurrentColor(color);
    }


    /**
     * @param game
     *            the game to update @param color a chosen regular color
     */
    public void addFour(GameClass game, String color)
    {
        requireGame(game);
        if (!isRegularColor(color))
        {
            throw new IllegalArgumentException(
                "Choose Red, Blue, Green, or Yellow");
        }
        game.requireCards(4);
        playedWildcard(game, color);
        game.drawForNextPlayer(4);
        game.skipNextPlayer();
    }


    @Override
    public String toString()
    {
        return getCardColor() + " " + cardType;
    }


    boolean isWild()
    {
        return "wild".equals(cardType) || "+4".equals(cardType);
    }


    private static void requireGame(GameClass game)
    {
        if (game == null)
        {
            throw new IllegalArgumentException("A game is required");
        }
    }
}
