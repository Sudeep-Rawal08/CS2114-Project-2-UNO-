package UNO;

public class CardTest extends student.TestCase
{
    public void testConstructorAndGetterStoreColor()
    {
        assertEquals("Red", new Card("Red").getCardColor());
        assertEquals("Blue", new Card("Blue").getCardColor());
        assertEquals("Green", new Card("Green").getCardColor());
        assertEquals("Yellow", new Card("Yellow").getCardColor());
    }

    public void testConstructorRejectsUnsupportedColors()
    {
        boolean caughtExpected = false;
        try
        {
            new Card("Purple");
        }
        catch (IllegalArgumentException e)
        {
            caughtExpected = true;
        }
        assertTrue("Expected IllegalArgumentException for unsupported color",
            caughtExpected);

        caughtExpected = false;
        try
        {
            new Card(null);
        }
        catch (IllegalArgumentException e)
        {
            caughtExpected = true;
        }
        assertTrue("Expected IllegalArgumentException for null color",
            caughtExpected);

        caughtExpected = false;
        try
        {
            new Card("Wild");
        }
        catch (IllegalArgumentException e)
        {
            caughtExpected = true;
        }
        assertTrue("Expected IllegalArgumentException for Wild color",
            caughtExpected);
    }

    public void testSetterUpdatesColor()
    {
        Card card = new Card("Red");
        card.setCardColor("Green");
        assertEquals("Green", card.getCardColor());
        card.setCardColor("Yellow");
        assertEquals("Yellow", card.getCardColor());
    }

    public void testSetterRejectsUnsupportedColors()
    {
        Card card = new Card("Red");
        boolean caughtExpected = false;
        try
        {
            card.setCardColor("Purple");
        }
        catch (IllegalArgumentException e)
        {
            caughtExpected = true;
        }
        assertTrue("Expected IllegalArgumentException for unsupported color",
            caughtExpected);

        caughtExpected = false;
        try
        {
            card.setCardColor(null);
        }
        catch (IllegalArgumentException e)
        {
            caughtExpected = true;
        }
        assertTrue("Expected IllegalArgumentException for null color",
            caughtExpected);

        caughtExpected = false;
        try
        {
            card.setCardColor("Wild");
        }
        catch (IllegalArgumentException e)
        {
            caughtExpected = true;
        }
        assertTrue("Expected IllegalArgumentException for Wild color",
            caughtExpected);
        assertEquals("Red", card.getCardColor());
    }
}
