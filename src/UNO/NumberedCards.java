package UNO;

public class NumberedCards extends Card
{
    private int cardNumber;

    public NumberedCards(String color, int number)
    {
        super(color);
        setCardNumber(number);
    }

    @Override
    public int getCardNumber()
    {
        return cardNumber;
    }

    @Override
    public boolean isNumberedCard()
    {
        return true;
    }

    public void setCardNumber(int number)
    {
        if (number < 0 || number > 9)
        {
            throw new IllegalArgumentException("Card number must be from 0 through 9.");
        }
        cardNumber = number;
    }

    @Override
    public void setCardColor(String color)
    {
        super.setCardColor(color);
    }

    @Override
    public String toString()
    {
        return getCardColor() + " " + cardNumber;
    }
}
