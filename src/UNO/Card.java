package UNO;

// -------------------------------------------------------------------------
/**
 * Card class
 * 
 * @author E
 * @version Oct 15, 2026
 */
public class Card
{
    private String color;
    public static String[] regularColors = {"Red", "Blue", "Green", "Yellow"};

    public Card(String color)
    {
        if (!isRegularColor(color) && !"Wild".equals(color))
        {
            throw new IllegalArgumentException("Unsupported card color");
        }
        this.color = color;
    }


    /** @return the stored color, including Wild before selection */
    public String getCardColor()
    {
        return color;
    }


    /**
     * @param color
     *            a supported color; subclasses restrict Wild
     */
    public void setCardColor(String color)
    {
        if (!isRegularColor(color) && !"Wild".equals(color))
        {
            throw new IllegalArgumentException("Unsupported card color");
        }
        this.color = color;
    }


    /**
     * @param color
     *            a supported color; subclasses restrict Wild
     * @returns true if the card is red, blue, yellow, or green.
     */
    static boolean isRegularColor(String color)
    {
        return "Red".equals(color) || "Blue".equals(color)
            || "Green".equals(color) || "Yellow".equals(color);
    }

    
}
