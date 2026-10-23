package UNO;

/** A colored action card or a wild card. Wild cards have no card color. */
public class SpecialCards
    extends Card
{
    private final String cardType;

    public SpecialCards(String color, String cardType)
    {
        super(isWildType(cardType) ? "Red" : color);
        if (!isSupportedType(cardType))
        {
            throw new IllegalArgumentException("Unsupported special card type");
        }
        this.cardType = cardType;
        if (isWild())
        {
            this.color = null;
        }
    }


    @Override
    public String getCardType()
    {
        return cardType;
    }


    @Override
    public void setCardColor(String color)
    {
        if (isWild() && color == null)
        {
            this.color = null;
        }
        else if (isRegularColor(color) && !isWild())
        {
            this.color = color;
        }
        else
        {
            throw new IllegalArgumentException(
                "Unsupported color for this card type");
        }
    }


    public void reverseOrder(GameClass game)
    {
        requireGame(game);
        game.reverseDirection();
    }


    public void skipPlayer(GameClass game)
    {
        requireGame(game);
        game.skipNextPlayer();
    }


    public void addTwo(GameClass game)
    {
        requireGame(game);
        game.drawForNextPlayer(2);
        game.skipNextPlayer();
    }


    public void playedWildcard(GameClass game, String color)
    {
        requireGame(game);
        requireChosenColor(color);
        game.setCurrentColor(color);
        this.color = color;
    }


    public void addFour(GameClass game, String color)
    {
        requireGame(game);
        requireChosenColor(color);
        game.drawForNextPlayer(4);
        game.skipNextPlayer();
        game.setCurrentColor(color);
        this.color = color;
    }


    @Override
    public void playSpecialEffect(GameClass game, String chosenColor)
    {
        if (isWild())
        {
            if (cardType.equals("wild"))
            {
                playedWildcard(game, chosenColor);
            }
            else
            {
                addFour(game, chosenColor);
            }
        }
        else
        {
            if (chosenColor != null)
            {
                throw new IllegalArgumentException(
                    "Only wild cards choose a color.");
            }
            if (cardType.equals("+2"))
            {
                addTwo(game);
            }
            if (cardType.equals("reverse"))
            {
                reverseOrder(game);
            }
            if (cardType.equals("skip"))
            {
                skipPlayer(game);
            }
        }
    }


    @Override
    public String toString()
    {
        if (isWild())
        {
            return cardType.equals("wild") ? "Wild" : cardType;
        }
        return color + " " + cardType;
    }


    @Override
    public boolean isSpecialCard()
    {
        return true;
    }


    @Override
    public boolean isWildCard()
    {
        return isWildType(cardType);
    }


    boolean isWild()
    {
        return isWildCard();
    }


    private static boolean isWildType(String type)
    {
        return "wild".equals(type) || "+4".equals(type);
    }


    private static boolean isSupportedType(String type)
    {
        return "+2".equals(type) || "+4".equals(type) || "reverse".equals(type)
            || "skip".equals(type) || "wild".equals(type);
    }


    private void requireChosenColor(String color)
    {
        if (!isRegularColor(color))
        {
            throw new IllegalArgumentException("Unsupported chosen color");
        }
    }


    private static void requireGame(GameClass game)
    {
        if (game == null)
        {
            throw new IllegalArgumentException("Game must not be null");
        }
    }
}
