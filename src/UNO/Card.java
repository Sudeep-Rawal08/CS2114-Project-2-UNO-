package UNO;
// -------------------------------------------------------------------------
/**
 *  Card class
 * 
 *  @author E
 *  @version Oct 15, 2026
 */
public class Card
{
    private String color;

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

    /** @param color a supported color; subclasses restrict Wild */
    public void setCardColor(String color)
    {
        if (!isRegularColor(color) && !"Wild".equals(color))
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
        return new String[] {"Red", "Blue", "Green", "Yellow"};
    }
}
