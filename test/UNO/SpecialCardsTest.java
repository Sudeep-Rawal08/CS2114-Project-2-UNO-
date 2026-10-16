package UNO;

import static org.junit.Assert.*;
import org.junit.Test;

/** Special effects request state changes through the game coordinator. */
public class SpecialCardsTest
{
    @Test
    public void reverseChangesDirectionAndRejectsNullGame()
    {
        GameFixture f = new GameFixture();
        SpecialCards reverse = new SpecialCards("Red", "reverse");
        reverse.reverseOrder(f.game);
        assertEquals(-1, f.game.currentDirection);
        reverse.reverseOrder(f.game);
        assertEquals(1, f.game.currentDirection);
        assertThrows(IllegalArgumentException.class, () -> reverse.reverseOrder(null));
        assertEquals(1, f.game.currentDirection);
    }

    @Test
    public void skipMakesOpponentLoseOneTurnAndRejectsNull()
    {
        GameFixture f = new GameFixture();
        SpecialCards skip = new SpecialCards("Red", "skip");
        skip.skipPlayer(f.game);
        assertTrue(f.game.skipNext);
        f.game.advanceTurn();
        assertEquals(0, f.game.currentTurn);
        assertFalse(f.game.skipNext);
        assertThrows(IllegalArgumentException.class, () -> skip.skipPlayer(null));
    }

    @Test
    public void drawTwoAddsExactlyTwoAndSkipsOpponent()
    {
        GameFixture f = new GameFixture();
        SpecialCards two = new SpecialCards("Red", "+2");
        two.addTwo(f.game);
        assertEquals(3, f.bot.getCardList().size());
        assertEquals(1, f.human.getCardList().size());
        assertEquals(18, f.deck.size());
        assertTrue(f.game.skipNext);
        assertThrows(IllegalArgumentException.class, () -> two.addTwo(null));
    }

    @Test
    public void drawTwoOnInsufficientDeckPreservesHandsAndSkip()
    {
        GameFixture f = new GameFixture();
        f.deck.clear();
        f.deck.add(new NumberedCards("Blue", 1));
        assertThrows(IllegalStateException.class,
            () -> new SpecialCards("Red", "+2").addTwo(f.game));
        assertEquals(1, f.deck.size());
        assertEquals(1, f.bot.getCardList().size());
        assertFalse(f.game.skipNext);
    }

    @Test
    public void wildcardUpdatesBothColorsButKeepsType()
    {
        GameFixture f = new GameFixture();
        SpecialCards wild = new SpecialCards("Wild", "wild");
        wild.playedWildcard(f.game, "Green");
        assertEquals("Green", wild.getCardColor());
        assertEquals("Green", f.game.currentColor);
        assertEquals("wild", wild.getCardType());
        assertThrows(IllegalArgumentException.class, () -> wild.playedWildcard(null, "Blue"));
    }

    @Test
    public void invalidWildcardColorCannotMutateCardOrGame()
    {
        GameFixture f = new GameFixture();
        SpecialCards wild = new SpecialCards("Wild", "wild");
        for (String invalid : new String[] {null, "Purple", "Wild"})
        {
            assertThrows(IllegalArgumentException.class, () -> wild.playedWildcard(f.game, invalid));
            assertEquals("Wild", wild.getCardColor());
            assertEquals("Red", f.game.currentColor);
        }
    }

    @Test
    public void drawFourSetsColorAddsExactlyFourAndSkips()
    {
        GameFixture f = new GameFixture();
        SpecialCards four = new SpecialCards("Wild", "+4");
        four.addFour(f.game, "Blue");
        assertEquals(5, f.bot.getCardList().size());
        assertEquals(16, f.deck.size());
        assertEquals("Blue", four.getCardColor());
        assertEquals("Blue", f.game.currentColor);
        assertTrue(f.game.skipNext);
        assertThrows(IllegalArgumentException.class, () -> four.addFour(null, "Blue"));
    }

    @Test
    public void drawFourRejectsColorAndDepletionBeforeAnySideEffect()
    {
        GameFixture f = new GameFixture();
        SpecialCards four = new SpecialCards("Wild", "+4");
        assertThrows(IllegalArgumentException.class, () -> four.addFour(f.game, "Purple"));
        assertThrows(IllegalArgumentException.class, () -> four.addFour(f.game, null));
        assertEquals(20, f.deck.size());
        f.deck.clear();
        assertThrows(IllegalStateException.class, () -> four.addFour(f.game, "Blue"));
        assertEquals("Wild", four.getCardColor());
        assertEquals("Red", f.game.currentColor);
        assertEquals(1, f.bot.getCardList().size());
        assertFalse(f.game.skipNext);
    }

    @Test
    public void applyingEachActionDispatchesTheCorrectEffect()
    {
        String[] types = {"reverse", "skip", "+2", "wild", "+4"};
        for (String type : types)
        {
            GameFixture f = new GameFixture();
            boolean wild = "wild".equals(type) || "+4".equals(type);
            Card card = new SpecialCards(wild ? "Wild" : "Red", type);
            GameFixture.hand(f.human, card);
            f.game.applyMove(Move.play(card, wild ? "Green" : null));
            assertTrue(f.human.getCardList().isEmpty());
            assertSame(card, f.game.currentCard);
            assertEquals(wild ? "Green" : "Red", f.game.currentColor);
            int penalty = "+2".equals(type) ? 2 : "+4".equals(type) ? 4 : 0;
            assertEquals(1 + penalty, f.bot.getCardList().size());
            assertEquals("reverse".equals(type) ? -1 : 1, f.game.currentDirection);
            assertEquals("skip".equals(type) || penalty > 0, f.game.skipNext);
        }
    }
}
