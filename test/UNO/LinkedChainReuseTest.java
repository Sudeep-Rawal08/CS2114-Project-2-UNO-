package UNO;

import static org.junit.Assert.*;
import org.junit.Test;

/** Three concrete application element types use the unmodified chain. */
public class LinkedChainReuseTest
{
    @Test
    public void storesAndRetrievesBothCardTypesAndMoves()
    {
        NumberedCards number = new NumberedCards("Red", 3);
        SpecialCards special = new SpecialCards("Blue", "skip");
        LinkedChain<Card> cards = new LinkedChain<Card>();
        cards.addToEnd(number);
        cards.addToEnd(special);
        Card[] retrieved = cards.toArray(new Card[cards.size()]);
        assertSame(number, retrieved[0]);
        assertSame(special, retrieved[1]);
        LinkedChain<Move> moves = new LinkedChain<Move>();
        Move play = Move.play(number, null);
        moves.add(play);
        assertSame(play, moves.get(0));
        assertSame(play, moves.remove());
        assertTrue(moves.isEmpty());
    }

    @Test
    public void emptyAndMissingRemovalPreserveApplicationCollections()
    {
        LinkedChain<Card> hand = new LinkedChain<Card>();
        assertEquals(0, hand.size());
        assertEquals(0, hand.toArray(new Card[0]).length);
        assertNull(hand.remove());
        assertFalse(hand.remove(new NumberedCards("Red", 5)));
        Card owned = new NumberedCards("Blue", 2);
        hand.add(owned);
        assertFalse(hand.remove(new SpecialCards("Wild", "wild")));
        assertFalse(hand.remove(null));
        assertEquals(1, hand.size());
        assertSame(owned, hand.get(0));
        assertThrows(IllegalArgumentException.class, () -> hand.add(null));
        assertEquals(1, hand.size());
    }
}
