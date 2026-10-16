package UNO;

import static org.junit.Assert.*;
import org.junit.Test;

/** Action requests validate their fields before entering the game. */
public class MoveTest
{
    @Test
    public void playRetainsTheExactOwnedCard()
    {
        Card card = new NumberedCards("Blue", 4);
        Move move = Move.play(card, null);
        assertFalse(move.isDraw());
        assertSame(card, move.getCard());
        assertNull(move.getChosenColor());
    }

    @Test
    public void drawHasNoCardOrChosenColor()
    {
        Move move = Move.draw();
        assertTrue(move.isDraw());
        assertNull(move.getCard());
        assertNull(move.getChosenColor());
    }

    @Test
    public void wildcardMoveDoesNotMutateTheCard()
    {
        Card card = new SpecialCards("Wild", "wild");
        Move move = Move.play(card, "Green");
        assertEquals("Green", move.getChosenColor());
        assertEquals("Wild", card.getCardColor());
    }

    @Test
    public void playRejectsNullCardAndInvalidColorCombinations()
    {
        Card number = new NumberedCards("Red", 5);
        Card wild = new SpecialCards("Wild", "+4");
        assertThrows(IllegalArgumentException.class, () -> Move.play(null, null));
        assertThrows(IllegalArgumentException.class, () -> Move.play(number, "Blue"));
        for (String invalid : new String[] {null, "Wild", "Purple", ""})
        {
            assertThrows(IllegalArgumentException.class, () -> Move.play(wild, invalid));
        }
    }
}
