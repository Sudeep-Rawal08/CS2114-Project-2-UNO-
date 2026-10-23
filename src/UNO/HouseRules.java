package UNO;

/**
 * Stores the optional house rules for a game of UNO.
 */
public class HouseRules
{
    // Fields to track whether each specific house rule is enabled or disabled
    private boolean stackingPlus;
    private boolean sevenSwap;
    private boolean drawTillMatch;

    /**
     * Creates a set of house rules with every rule disabled by default.
     */
    public HouseRules()
    {
        stackingPlus = false;
        sevenSwap = false;
        drawTillMatch = false;
    }

    /**
     * Returns whether the stacking rule is enabled.
     * 
     * @return true if stacking is enabled, false otherwise
     */
    public boolean isStackingPlus()
    {
        return stackingPlus;
    }

    /**
     * Returns whether the 7-swap rule is enabled.
     * 
     * @return true if 7-swap is enabled, false otherwise
     */
    public boolean isSevenSwap()
    {
        return sevenSwap;
    }

    /**
     * Returns whether the draw-till-match rule is enabled.
     * 
     * @return true if draw-till-match is enabled, false otherwise
     */
    public boolean isDrawTillMatch()
    {
        return drawTillMatch;
    }

    /**
     * Toggles the stacking rule state between enabled and disabled.
     */
    public void toggleStacking()
    {
        stackingPlus = !stackingPlus;
    }

    /**
     * Toggles the 7-swap rule state between enabled and disabled.
     */
    public void toggleSevenSwap()
    {
        sevenSwap = !sevenSwap;
    }

    /**
     * Toggles the draw-till-match rule state between enabled and disabled.
     */
    public void toggleDrawTillMatch()
    {
        drawTillMatch = !drawTillMatch;
    }
}