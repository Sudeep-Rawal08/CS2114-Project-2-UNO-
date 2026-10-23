package UNO;

/** Base class for cards. A card's color is one of the four regular colors. */
public class Card
{
    protected String color;

    public Card(String color)
    {
        if (!isRegularColor(color))
        {
            throw new IllegalArgumentException("Unsupported card color");
        }
        this.color = color;
    }


    public String getCardColor()
    {
        return color;
    }


    /** Returns true when this is a numbered card. */
    public boolean isNumberedCard()
    {
        return false;
    }


    /** Returns true when this is a special card. */
    public boolean isSpecialCard()
    {
        return false;
    }


    /** Returns this card's number, or -1 when it has no number. */
    public int getCardNumber()
    {
        return -1;
    }


    /** Returns this card's type, or null when it has no type. */
    public String getCardType()
    {
        return null;
    }


    /** Returns whether this card lets its player choose a color. */
    public boolean isWildCard()
    {
        return false;
    }


    /** Applies a special card's effect, if it has one. */
    public void playSpecialEffect(GameClass game, String chosenColor)
    {
        if (chosenColor != null)
        {
            throw new IllegalArgumentException(
                "This card does not choose a color.");
        }
    }


    public void setCardColor(String color)
    {
        if (!isRegularColor(color))
        {
            throw new IllegalArgumentException("Unsupported card color");
        }
        this.color = color;
    }


    static boolean isRegularColor(String color)
    {
        return "Red".equals(color) || "Blue".equals(color)
            || "Green".equals(color) || "Yellow".equals(color);
    }


    static String[] regularColors()
    {
        return new String[] { "Red", "Blue", "Green", "Yellow" };
    }
}
