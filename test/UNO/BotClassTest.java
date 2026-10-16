package UNO;

import static org.junit.Assert.*;
import org.junit.Test;

/** Each difficulty chooses a deterministic legal move from real candidates. */
public class BotClassTest
{
    @Test
    public void constructorsAndDifficultyGetterValidateConfiguration()
    {
        assertEquals(BotDifficulty.SIMPLE, new BotClass().getDifficulty());
        for (BotDifficulty difficulty : BotDifficulty.values())
        {
            assertEquals(difficulty, new BotClass(difficulty).getDifficulty());
        }
        assertThrows(IllegalArgumentException.class, () -> new BotClass(null));
    }

    @Test
    public void simpleChoosesFirstLegalCardWithoutChangingState()
    {
        GameFixture f = new GameFixture();
        f.game.currentTurn = 1;
        Card illegal = new NumberedCards("Blue", 1);
        Card first = new NumberedCards("Red", 2);
        Card second = new NumberedCards("Green", 5);
        GameFixture.hand(f.bot, illegal, first, second);
        Move selected = f.bot.chooseMove(f.game);
        assertSame(first, selected.getCard());
        assertEquals(3, f.bot.getCardList().size());
        assertEquals("Red", f.game.currentColor);
        assertEquals(20, f.deck.size());
    }

    @Test
    public void normalFavorsRemainingColorInsteadOfFirstLegalCard()
    {
        GameFixture f = new GameFixture("", BotDifficulty.NORMAL);
        f.game.currentTurn = 1;
        Card first = new NumberedCards("Red", 2);
        Card blueFive = new NumberedCards("Blue", 5);
        GameFixture.hand(f.bot, first, blueFive, new NumberedCards("Blue", 1),
            new NumberedCards("Blue", 3));
        assertSame(blueFive, f.bot.chooseMove(f.game).getCard());
    }

    @Test
    public void normalConservesWildWhenAnOrdinaryCardIsLegal()
    {
        GameFixture f = new GameFixture("", BotDifficulty.NORMAL);
        f.game.currentTurn = 1;
        Card ordinary = new NumberedCards("Red", 2);
        GameFixture.hand(f.bot, new SpecialCards("Wild", "wild"), ordinary,
            new NumberedCards("Blue", 1), new NumberedCards("Blue", 3));
        assertSame(ordinary, f.bot.chooseMove(f.game).getCard());
    }

    @Test
    public void simpleUsesRedWhileNormalAndMasterChooseMajorityWildColor()
    {
        for (BotDifficulty level : BotDifficulty.values())
        {
            GameFixture f = new GameFixture("", level);
            f.game.currentTurn = 1;
            Card wild = new SpecialCards("Wild", "wild");
            GameFixture.hand(f.bot, wild, new NumberedCards("Blue", 1),
                new NumberedCards("Blue", 3));
            Move selected = f.bot.chooseMove(f.game);
            assertSame(wild, selected.getCard());
            assertEquals(level == BotDifficulty.SIMPLE ? "Red" : "Blue",
                selected.getChosenColor());
        }
    }

    @Test
    public void normalAndMasterDifferWhenOpponentHasOneCard()
    {
        for (BotDifficulty level : new BotDifficulty[] {BotDifficulty.NORMAL, BotDifficulty.MASTER})
        {
            GameFixture f = new GameFixture("", level);
            f.game.currentTurn = 1;
            f.game.currentColor = "Blue";
            f.game.currentCard = new NumberedCards("Blue", 5);
            Card redFive = new NumberedCards("Red", 5);
            Card skip = new SpecialCards("Blue", "skip");
            GameFixture.hand(f.bot, redFive, new NumberedCards("Red", 2),
                new NumberedCards("Red", 7), skip);
            assertSame(level == BotDifficulty.NORMAL ? redFive : skip,
                f.bot.chooseMove(f.game).getCard());
        }
    }

    @Test
    public void masterUsesColorStrategyWhenOpponentIsNotCloseToWinning()
    {
        GameFixture f = new GameFixture("", BotDifficulty.MASTER);
        f.game.currentTurn = 1;
        GameFixture.hand(f.human, new NumberedCards("Green", 1),
            new NumberedCards("Yellow", 2), new NumberedCards("Yellow", 3));
        Card blueFive = new NumberedCards("Blue", 5);
        GameFixture.hand(f.bot, new SpecialCards("Red", "skip"), blueFive,
            new NumberedCards("Blue", 1), new NumberedCards("Blue", 3));
        assertSame(blueFive, f.bot.chooseMove(f.game).getCard());
    }

    @Test
    public void masterFinishesWithItsOnlyLegalCard()
    {
        GameFixture f = new GameFixture("", BotDifficulty.MASTER);
        f.game.currentTurn = 1;
        Card last = new NumberedCards("Red", 2);
        GameFixture.hand(f.bot, last);
        assertTrue(f.bot.playCard(f.game));
        assertSame(last, f.game.currentCard);
        assertTrue(f.game.isWinner(f.bot));
    }

    @Test
    public void allLevelsRejectNullOrWrongTurnAndReturnNoMoveForEmptyHand()
    {
        for (BotDifficulty level : BotDifficulty.values())
        {
            GameFixture f = new GameFixture("", level);
            assertThrows(IllegalArgumentException.class, () -> f.bot.chooseMove(null));
            assertThrows(IllegalArgumentException.class, () -> f.bot.playCard(null));
            assertThrows(IllegalArgumentException.class, () -> f.bot.chooseMove(f.game));
            assertThrows(IllegalArgumentException.class, () -> f.bot.playCard(f.game));
            f.game.currentTurn = 1;
            f.bot.getCardList().clear();
            assertNull(f.bot.chooseMove(f.game));
            assertFalse(f.bot.playCard(f.game));
            assertEquals(20, f.deck.size());
        }
    }

    @Test
    public void everyLevelDrawsAndPassesWhenNewCardIsAlsoIllegal()
    {
        for (BotDifficulty level : BotDifficulty.values())
        {
            GameFixture f = new GameFixture("", level);
            f.game.currentTurn = 1;
            f.deck.clear();
            Card drawn = new NumberedCards("Blue", 2);
            f.deck.add(drawn);
            assertTrue(f.bot.chooseMove(f.game).isDraw());
            assertFalse(f.bot.playCard(f.game));
            assertTrue(f.bot.getCardList().contains(drawn));
            assertEquals(2, f.bot.getCardList().size());
            assertEquals("Red", f.game.currentColor);
            assertTrue(f.deck.isEmpty());
        }
    }

    @Test
    public void everyLevelImmediatelyPlaysALegalDrawnCard()
    {
        for (BotDifficulty level : BotDifficulty.values())
        {
            GameFixture f = new GameFixture("", level);
            f.game.currentTurn = 1;
            f.deck.clear();
            Card drawn = new NumberedCards("Red", 8);
            f.deck.add(drawn);
            assertTrue(f.bot.playCard(f.game));
            assertSame(drawn, f.game.currentCard);
            assertEquals(1, f.bot.getCardList().size());
            assertFalse(f.bot.getCardList().contains(drawn));
        }
    }

    @Test
    public void everyLevelRespectsDrawFourRestrictionEvenUnderPressure()
    {
        for (BotDifficulty level : BotDifficulty.values())
        {
            GameFixture f = new GameFixture("", level);
            f.game.currentTurn = 1;
            Card four = new SpecialCards("Wild", "+4");
            Card regular = new NumberedCards("Red", 1);
            GameFixture.hand(f.bot, four, regular);
            assertSame(regular, f.bot.chooseMove(f.game).getCard());
            assertTrue(f.bot.playCard(f.game));
            assertTrue(f.bot.getCardList().contains(four));
            assertEquals(1, f.human.getCardList().size());
            assertEquals(20, f.deck.size());
        }
    }

    @Test
    public void everyLevelPropagatesDocumentedEmptyDeckFailure()
    {
        for (BotDifficulty level : BotDifficulty.values())
        {
            GameFixture f = new GameFixture("", level);
            f.game.currentTurn = 1;
            f.deck.clear();
            assertThrows(IllegalStateException.class, () -> f.bot.playCard(f.game));
            assertEquals(1, f.bot.getCardList().size());
        }
    }

    @Test
    public void tiedScoresRemainDeterministic()
    {
        GameFixture f = new GameFixture("", BotDifficulty.NORMAL);
        f.game.currentTurn = 1;
        Card first = new NumberedCards("Red", 1);
        Card second = new NumberedCards("Red", 2);
        GameFixture.hand(f.bot, first, second);
        assertSame(first, f.bot.chooseMove(f.game).getCard());
        assertSame(first, f.bot.chooseMove(f.game).getCard());
    }

    @Test
    public void drawnWildcardUsesDifficultyColorSelection()
    {
        for (BotDifficulty level : BotDifficulty.values())
        {
            GameFixture f = new GameFixture("", level);
            f.game.currentTurn = 1;
            GameFixture.hand(f.bot, new NumberedCards("Blue", 1),
                new NumberedCards("Blue", 2));
            f.deck.clear();
            Card wild = new SpecialCards("Wild", "wild");
            f.deck.add(wild);
            assertTrue(f.bot.playCard(f.game));
            assertSame(wild, f.game.currentCard);
            assertEquals(level == BotDifficulty.SIMPLE ? "Red" : "Blue", f.game.currentColor);
            assertEquals(2, f.bot.getCardList().size());
            assertTrue(f.deck.isEmpty());
        }
    }

    @Test
    public void masterPrioritizesDrawPenaltyAtTwoCardPressureBoundary()
    {
        GameFixture f = new GameFixture("", BotDifficulty.MASTER);
        f.game.currentTurn = 1;
        GameFixture.hand(f.human, new NumberedCards("Blue", 1),
            new NumberedCards("Green", 2));
        Card penalty = new SpecialCards("Red", "+2");
        GameFixture.hand(f.bot, new NumberedCards("Blue", 5),
            new NumberedCards("Blue", 1), new NumberedCards("Blue", 2), penalty);
        assertSame(penalty, f.bot.chooseMove(f.game).getCard());
        assertTrue(f.bot.playCard(f.game));
        assertEquals(4, f.human.getCardList().size());
        assertTrue(f.game.skipNext);
    }

    @Test
    public void masterCanSelectAndApplyLegalDrawFourUnderPressure()
    {
        GameFixture f = new GameFixture("", BotDifficulty.MASTER);
        f.game.currentTurn = 1;
        Card four = new SpecialCards("Wild", "+4");
        GameFixture.hand(f.bot, new NumberedCards("Blue", 5),
            new NumberedCards("Blue", 1), four);
        Move selected = f.bot.chooseMove(f.game);
        assertSame(four, selected.getCard());
        assertEquals("Blue", selected.getChosenColor());
        assertTrue(f.bot.playCard(f.game));
        assertEquals(5, f.human.getCardList().size());
        assertEquals("Blue", f.game.currentColor);
        assertTrue(f.game.skipNext);
    }

    @Test
    public void changingOpponentHiddenCardsDoesNotChangeMasterChoice()
    {
        GameFixture f = new GameFixture("", BotDifficulty.MASTER);
        f.game.currentTurn = 1;
        Card skip = new SpecialCards("Red", "skip");
        GameFixture.hand(f.bot, new NumberedCards("Blue", 5), skip);
        Move before = f.bot.chooseMove(f.game);
        GameFixture.hand(f.human, new SpecialCards("Wild", "+4"));
        Move after = f.bot.chooseMove(f.game);
        assertSame(skip, before.getCard());
        assertSame(before.getCard(), after.getCard());
        assertEquals(before.getChosenColor(), after.getChosenColor());
    }
}
