package UNO;

/** JUnit 4-compatible tests for Move. */
public class MoveTest extends student.TestCase
{
    public void testDrawMove()
    {
        Move move = Move.draw();
        assertTrue(move.isDraw());
        assertNull(move.getCard());
        assertNull(move.getChosenColor());
    }

    public void testPlayNumberedCard()
    {
        Card card = new NumberedCards("Blue", 4);
        Move move = Move.play(card, null);
        assertFalse(move.isDraw());
        assertSame(card, move.getCard());
        assertNull(move.getChosenColor());
    }

    public void testPlayWildWithChosenColor()
    {
        Card card = new SpecialCards(null, "wild");
        Move move = Move.play(card, "Green");
        assertSame(card, move.getCard());
        assertEquals("Green", move.getChosenColor());
    }

    public void testPlayPlusFourWithChosenColor()
    {
        Card card = new SpecialCards(null, "+4");
        Move move = Move.play(card, "Yellow");
        assertSame(card, move.getCard());
        assertEquals("Yellow", move.getChosenColor());
    }

    public void testRejectNullCard()
    {
        boolean caught = false;
        try
        {
            Move.play(null, null);
        }
        catch (IllegalArgumentException exception)
        {
            caught = true;
        }
        assertTrue(caught);
    }

    public void testRejectInvalidWildColor()
    {
        boolean caught = false;
        try
        {
            Move.play(new SpecialCards(null, "wild"), "Wild");
        }
        catch (IllegalArgumentException exception)
        {
            caught = true;
        }
        assertTrue(caught);
    }

    public void testRejectColorOnRegularCard()
    {
        boolean caught = false;
        try
        {
            Move.play(new NumberedCards("Red", 3), "Blue");
        }
        catch (IllegalArgumentException exception)
        {
            caught = true;
        }
        assertTrue(caught);
    }
}
