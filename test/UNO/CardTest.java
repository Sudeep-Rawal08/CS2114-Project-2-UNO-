package UNO;

import static org.junit.Assert.*;
import org.junit.Test;

/** Card values, validation, and rejected-state preservation. */
public class CardTest
{
    @Test
    public void sharedColorContractRejectsInvalidMutationAndAllowsWildBoundary()
    {
        Card card = new Card("Red") { };
        card.setCardColor("Blue");
        assertEquals("Blue", card.getCardColor());
        assertThrows(IllegalArgumentException.class, () -> card.setCardColor(null));
        assertThrows(IllegalArgumentException.class, () -> card.setCardColor("Purple"));
        assertEquals("Blue", card.getCardColor());
        card.setCardColor("Wild");
        assertEquals("Wild", card.getCardColor());
    }

    @Test
    public void numberedConstructorAndGettersIncludeZeroBoundary()
    {
        NumberedCards five = new NumberedCards("Red", 5);
        assertEquals("Red", five.getCardColor());
        assertEquals(5, five.getCardNumber());
        assertEquals(0, new NumberedCards("Blue", 0).getCardNumber());
        assertEquals(9, new NumberedCards("Green", 9).getCardNumber());
    }

    @Test
    public void numberedConstructorRejectsInvalidValues()
    {
        assertThrows(IllegalArgumentException.class, () -> new NumberedCards("Red", -1));
        assertThrows(IllegalArgumentException.class, () -> new NumberedCards("Red", 10));
        assertThrows(IllegalArgumentException.class, () -> new NumberedCards(null, 5));
        assertThrows(IllegalArgumentException.class, () -> new NumberedCards("Wild", 5));
        assertThrows(IllegalArgumentException.class, () -> new NumberedCards("Purple", 5));
    }

    @Test
    public void numberSetterValidatesBeforeMutation()
    {
        NumberedCards card = new NumberedCards("Red", 5);
        card.setCardNumber(7);
        assertEquals(7, card.getCardNumber());
        for (int invalid : new int[] {-1, 10})
        {
            assertThrows(IllegalArgumentException.class, () -> card.setCardNumber(invalid));
            assertEquals(7, card.getCardNumber());
        }
    }

    @Test
    public void numberedColorSetterUsesInheritedColorAndRejectsWild()
    {
        Card card = new NumberedCards("Red", 5);
        card.setCardColor("Yellow");
        assertEquals("Yellow", card.getCardColor());
        for (String invalid : new String[] {null, "Purple", "Wild", ""})
        {
            assertThrows(IllegalArgumentException.class, () -> card.setCardColor(invalid));
            assertEquals("Yellow", card.getCardColor());
        }
    }

    @Test
    public void specialConstructorsGettersAndColorBoundaries()
    {
        for (String type : new String[] {"+2", "reverse", "skip"})
        {
            SpecialCards card = new SpecialCards("Blue", type);
            assertEquals(type, card.getCardType());
            assertEquals("Blue", card.getCardColor());
            card.setCardColor("Green");
            assertEquals("Green", card.getCardColor());
            assertThrows(IllegalArgumentException.class, () -> card.setCardColor("Wild"));
        }
        for (String type : new String[] {"wild", "+4"})
        {
            SpecialCards card = new SpecialCards("Wild", type);
            assertEquals("Wild", card.getCardColor());
            card.setCardColor("Red");
            assertEquals(type, card.getCardType());
            assertEquals("Red", card.getCardColor());
            card.setCardColor("Wild");
            assertEquals("Wild", card.getCardColor());
        }
    }

    @Test
    public void specialConstructorAndSetterRejectBadArguments()
    {
        assertThrows(IllegalArgumentException.class, () -> new SpecialCards("Red", null));
        assertThrows(IllegalArgumentException.class, () -> new SpecialCards("Red", "unknown"));
        assertThrows(IllegalArgumentException.class, () -> new SpecialCards("Wild", "skip"));
        assertThrows(IllegalArgumentException.class, () -> new SpecialCards("Purple", "wild"));
        SpecialCards card = new SpecialCards("Wild", "+4");
        for (String invalid : new String[] {null, "Purple", ""})
        {
            assertThrows(IllegalArgumentException.class, () -> card.setCardColor(invalid));
            assertEquals("Wild", card.getCardColor());
        }
    }

    @Test
    public void cardFormattingIncludesOrdinaryAndBoundaryValues()
    {
        assertEquals("Red 5", new NumberedCards("Red", 5).toString());
        assertEquals("Yellow 0", new NumberedCards("Yellow", 0).toString());
        assertEquals("Blue skip", new SpecialCards("Blue", "skip").toString());
        assertEquals("Wild wild", new SpecialCards("Wild", "wild").toString());
    }
}
