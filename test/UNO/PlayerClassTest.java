package UNO;

import static org.junit.Assert.*;
import org.junit.Test;

/** Scripted human input exercises real game state and recovery. */
public class PlayerClassTest
{
    @Test
    public void defaultPlayersHaveDistinctNonNullEmptyHands()
    {
        PlayerClass first = new PlayerClass();
        PlayerClass second = new PlayerClass();
        assertNotNull(first.getCardList());
        assertTrue(first.getCardList().isEmpty());
        assertSame(first.getCardList(), first.getCardList());
        assertNotSame(first.getCardList(), second.getCardList());
        first.getCardList().add(new NumberedCards("Red", 1));
        assertEquals(1, first.getCardList().size());
        assertTrue(second.getCardList().isEmpty());
    }

    @Test
    public void validOwnedCardIsPlayedAfterInvalidInputsRecover()
    {
        GameFixture f = new GameFixture("\nR500\nZZ\nR1\nB1\nR5\n", BotDifficulty.SIMPLE);
        Card legal = new NumberedCards("Red", 5);
        Card illegal = new NumberedCards("Blue", 1);
        GameFixture.hand(f.human, legal, illegal);
        assertTrue(f.human.playCard(f.game));
        assertSame(legal, f.game.currentCard);
        assertSame(illegal, f.human.getCardList().get(0));
        assertEquals(1, f.human.getCardList().size());
        assertTrue(f.text().contains("Invalid input"));
        assertTrue(f.text().contains("You do not have that card"));
        assertTrue(f.text().contains("You cannot play that card"));
        assertFalse(f.human.hasQuit());
    }

    @Test(timeout = 1000)
    public void everyRejectedInputPreservesStateBeforeEof()
    {
        for (String line : new String[] {"\n", "R500\n", "ZZ\n", "R1\n", "B1\n"})
        {
            GameFixture f = new GameFixture(line, BotDifficulty.SIMPLE);
            Card owned = f.human.getCardList().get(0);
            Card top = f.game.currentCard;
            assertFalse(f.human.playCard(f.game));
            assertTrue(f.human.hasQuit());
            assertSame(owned, f.human.getCardList().get(0));
            assertEquals(1, f.human.getCardList().size());
            assertSame(top, f.game.currentCard);
            assertEquals("Red", f.game.currentColor);
            assertEquals(20, f.deck.size());
            assertEquals(0, f.game.currentTurn);
            assertFalse(f.game.skipNext);
        }
    }

    @Test
    public void wildcardColorRepromptsAndAppliesOnlyAfterValidSelection()
    {
        GameFixture f = new GameFixture("ww\nPurple\nWild\ngreen\n", BotDifficulty.SIMPLE);
        SpecialCards wild = new SpecialCards("Wild", "wild");
        GameFixture.hand(f.human, wild, new NumberedCards("Blue", 1));
        assertTrue(f.human.playCard(f.game));
        assertEquals("Green", f.game.currentColor);
        assertEquals("Green", wild.getCardColor());
        assertEquals("wild", wild.getCardType());
        assertEquals(1, f.human.getCardList().size());
        assertTrue(f.text().contains("Red, Blue, Green, Yellow"));
        assertTrue(f.text().contains("Invalid input"));
    }

    @Test(timeout = 1000)
    public void eofDuringWildColorDoesNotPartiallyPlay()
    {
        GameFixture f = new GameFixture("WW\nPurple\n", BotDifficulty.SIMPLE);
        Card wild = new SpecialCards("Wild", "wild");
        GameFixture.hand(f.human, wild);
        Card top = f.game.currentCard;
        assertFalse(f.human.playCard(f.game));
        assertTrue(f.human.hasQuit());
        assertSame(top, f.game.currentCard);
        assertEquals("Red", f.game.currentColor);
        assertEquals("Wild", wild.getCardColor());
        assertSame(wild, f.human.getCardList().get(0));
    }

    @Test(timeout = 1000)
    public void eofAfterDrawingWildRetainsTheDrawWithoutPartiallyPlaying()
    {
        GameFixture f = new GameFixture("D\n", BotDifficulty.SIMPLE);
        f.deck.clear();
        Card wild = new SpecialCards("Wild", "wild");
        f.deck.add(wild);
        Card top = f.game.currentCard;
        assertFalse(f.human.playCard(f.game));
        assertTrue(f.human.hasQuit());
        assertTrue(f.deck.isEmpty());
        assertEquals(2, f.human.getCardList().size());
        assertTrue(f.human.getCardList().contains(wild));
        assertSame(top, f.game.currentCard);
        assertEquals("Red", f.game.currentColor);
        assertEquals("Wild", wild.getCardColor());
        assertEquals(0, f.game.currentTurn);
        assertFalse(f.game.skipNext);
    }

    @Test
    public void drawAnUnplayableCardEndsTurnAndRetainsCard()
    {
        GameFixture f = new GameFixture("D\n", BotDifficulty.SIMPLE);
        f.deck.clear();
        Card drawn = new NumberedCards("Blue", 2);
        f.deck.add(drawn);
        Card top = f.game.currentCard;
        assertFalse(f.human.playCard(f.game));
        assertTrue(f.human.getCardList().contains(drawn));
        assertEquals(2, f.human.getCardList().size());
        assertSame(top, f.game.currentCard);
        assertTrue(f.deck.isEmpty());
    }

    @Test
    public void drawnPlayableCardIsImmediatelyPlayed()
    {
        GameFixture f = new GameFixture("D\n", BotDifficulty.SIMPLE);
        f.deck.clear();
        Card drawn = new NumberedCards("Red", 8);
        f.deck.add(drawn);
        assertTrue(f.human.playCard(f.game));
        assertSame(drawn, f.game.currentCard);
        assertFalse(f.human.getCardList().contains(drawn));
        assertEquals(1, f.human.getCardList().size());
    }

    @Test
    public void drawnWildcardPromptsForColorWithoutDrawingAgain()
    {
        GameFixture f = new GameFixture("D\nPurple\nB\n", BotDifficulty.SIMPLE);
        f.deck.clear();
        Card wild = new SpecialCards("Wild", "wild");
        f.deck.add(wild);
        assertTrue(f.human.playCard(f.game));
        assertSame(wild, f.game.currentCard);
        assertEquals("Blue", f.game.currentColor);
        assertTrue(f.deck.isEmpty());
        assertEquals(1, f.human.getCardList().size());
    }

    @Test
    public void voluntarilyDrawnFourCannotBypassMatchingColorRestriction()
    {
        GameFixture f = new GameFixture("D\n", BotDifficulty.SIMPLE);
        Card regular = new NumberedCards("Red", 1);
        GameFixture.hand(f.human, regular);
        f.deck.clear();
        Card four = new SpecialCards("Wild", "+4");
        f.deck.add(four);
        Card top = f.game.currentCard;
        assertFalse(f.human.playCard(f.game));
        assertSame(top, f.game.currentCard);
        assertEquals(2, f.human.getCardList().size());
        assertTrue(f.human.getCardList().contains(four));
        assertEquals(1, f.bot.getCardList().size());
        assertEquals("Wild", four.getCardColor());
    }

    @Test
    public void playRejectsNullOrWrongTurnAndHandlesAlreadyWonHand()
    {
        GameFixture f = new GameFixture("R5\n", BotDifficulty.SIMPLE);
        assertThrows(IllegalArgumentException.class, () -> f.human.playCard(null));
        f.game.currentTurn = 1;
        assertThrows(IllegalArgumentException.class, () -> f.human.playCard(f.game));
        f.game.currentTurn = 0;
        f.human.getCardList().clear();
        assertFalse(f.human.playCard(f.game));
        assertFalse(f.human.hasQuit());
    }

    @Test
    public void quitDoesNotConsumeCardsOrTurns()
    {
        GameFixture f = new GameFixture("Q\n", BotDifficulty.SIMPLE);
        Card top = f.game.currentCard;
        assertFalse(f.human.playCard(f.game));
        assertTrue(f.human.hasQuit());
        assertSame(top, f.game.currentCard);
        assertEquals(1, f.human.getCardList().size());
        assertEquals(0, f.game.currentTurn);
    }

    @Test
    public void handCodesSupportAllSpecialCardTypes()
    {
        GameFixture f = new GameFixture();
        GameFixture.hand(f.human, new SpecialCards("Red", "+2"),
            new SpecialCards("Blue", "reverse"), new SpecialCards("Green", "skip"),
            new SpecialCards("Wild", "wild"), new SpecialCards("Wild", "+4"));
        f.game.printPlayerDeck(f.human);
        for (String code : new String[] {"R+", "BR", "GS", "WW", "W4"})
        {
            assertTrue(f.text().contains(code));
        }
    }
}
