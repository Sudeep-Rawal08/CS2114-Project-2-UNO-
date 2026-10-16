package UNO;

/** A numbered card with one regular color. */
public class NumberedCards
    extends Card
{
    private int cardNumber;

    /**
     * @param color
     *            a regular color 
     * @param number
     *            a value from 0 to 9
     */
    public NumberedCards(String color, int number)
    {
        super(color);
        setCardColor(color);
        setCardNumber(number);
    }


    /** @return the number printed on the card */
    public int getCardNumber()
    {
        return cardNumber;
    }


    /**
     * @param number
     *            a value from 0 to 9
     */
    public void setCardNumber(int number)
    {
        if (number < 0 || number > 9)
        {
            throw new IllegalArgumentException(
                "Card number must be 0 through 9");
        }
        cardNumber = number;
    }


    @Override
    public void setCardColor(String color)
    {
        if (!isRegularColor(color))
        {
            throw new IllegalArgumentException(
                "Numbered cards need a regular color");
        }
        super.setCardColor(color);
    }


    @Override
    public String toString()
    {
        return getCardColor() + " " + cardNumber;
    }
}
