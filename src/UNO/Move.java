package UNO;

/** Describes one play or draw without changing the game. */
public final class Move
{
    private final Card card;
    private final String chosenColor;

    private Move(Card selectedCard, String color)
    {
        card = selectedCard;
        chosenColor = color;
    }


    /** Creates a play. Wild cards require a regular chosen color. */
    public static Move play(Card card, String chosenColor)
    {
        if (card == null)
        {
            throw new IllegalArgumentException("A play needs a card");
        }

        boolean wild = card.isWildCard();
        boolean validColor = Card.isRegularColor(chosenColor);

        if ((wild && !validColor) || (!wild && chosenColor != null))
        {
            throw new IllegalArgumentException(
                "Invalid chosen color for this move");
        }

        return new Move(card, chosenColor);
    }


    /** Creates a draw move. */
    public static Move draw()
    {
        return new Move(null, null);
    }


    /** Returns true when this move draws rather than plays. */
    public boolean isDraw()
    {
        return card == null;
    }


    /** Returns the played card, or null for a draw. */
    public Card getCard()
    {
        return card;
    }


    /** Returns the chosen color for a wild play, otherwise null. */
    public String getChosenColor()
    {
        return chosenColor;
    }
}
