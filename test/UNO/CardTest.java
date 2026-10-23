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
    public void testBaseCardMethodsAndRejectedSpecialEffect()
    {
        GameClass game = new GameClass();
        for (String color : Card.regularColors())
        {
            Card card = new Card(color);
            assertFalse(card.isNumberedCard());
            assertFalse(card.isSpecialCard());
            assertFalse(card.isWildCard());
            assertEquals(-1, card.getCardNumber());
            assertNull(card.getCardType());
            Card top = game.currentCard;
            card.playSpecialEffect(game, null);
            assertSame(top, game.currentCard);
            try
            {
                card.playSpecialEffect(game, "Blue");
                fail("A base card cannot choose a color");
            }
            catch (IllegalArgumentException expected)
            {
                assertEquals(color, card.getCardColor());
                assertSame(top, game.currentCard);
            }
        }
    }

    public void testNumberedCardPublicMethodsAndBoundaries()
    {
        NumberedCards card = new NumberedCards("Red", 0);
        assertEquals(0, card.getCardNumber());
        assertTrue(card.isNumberedCard());
        assertFalse(card.isSpecialCard());
        assertFalse(card.isWildCard());
        assertNull(card.getCardType());
        assertEquals("Red 0", card.toString());
        card.setCardNumber(9);
        card.setCardColor("Blue");
        assertEquals(9, card.getCardNumber());
        assertEquals("Blue", card.getCardColor());
        assertEquals("Blue 9", card.toString());
        for (int invalid : new int[] {-1, 10})
        {
            org.junit.Assert.assertThrows(IllegalArgumentException.class,
                () -> new NumberedCards("Red", invalid));
            org.junit.Assert.assertThrows(IllegalArgumentException.class,
                () -> card.setCardNumber(invalid));
            assertEquals(9, card.getCardNumber());
        }
        for (String invalid : new String[] {null, "Purple", "Wild", ""})
        {
            org.junit.Assert.assertThrows(IllegalArgumentException.class,
                () -> new NumberedCards(invalid, 5));
            org.junit.Assert.assertThrows(IllegalArgumentException.class,
                () -> card.setCardColor(invalid));
            assertEquals("Blue", card.getCardColor());
        }
    }

}
