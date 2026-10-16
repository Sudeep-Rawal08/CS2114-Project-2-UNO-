package UNO;

/** An immutable play or draw request. */
public final class Move
{
    private final Card card;
    private final String chosenColor;

    private Move(Card card, String chosenColor)
    {
        this.card = card;
        this.chosenColor = chosenColor;
    }


    /**
     * Creates a play request without changing the card or the game.
     * 
     * @param card
     *            the exact card to play
     * @param color
     *            a regular color for a wild; null for ordinary cards
     * @return the validated action request
     */
    public static Move play(Card card, String color)
    {
        if (card == null)
        {
            throw new IllegalArgumentException("A play needs a card");
        }
        boolean wild =
            card instanceof SpecialCards && ((SpecialCards)card).isWild();
        if ((wild && !Card.isRegularColor(color)) || (!wild && color != null))
        {
            throw new IllegalArgumentException(
                "Invalid chosen color for this move");
        }
        return new Move(card, color);
    }


    /** @return a request to draw one card */
    public static Move draw()
    {
        return new Move(null, null);
    }


    /** @return whether this action is a draw */
    public boolean isDraw()
    {
        return card == null;
    }


    /** @return the selected card, or null for a draw */
    public Card getCard()
    {
        return card;
    }


    /** @return the selected wild color, or null when not applicable */
    public String getChosenColor()
    {
        return chosenColor;
    }
}
