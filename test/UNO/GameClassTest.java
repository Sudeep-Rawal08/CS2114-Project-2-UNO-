package UNO;

import static org.junit.Assert.*;
import java.util.Random;
import java.util.Scanner;
import org.junit.Test;

/** Rule validation, storage transfers, and atomic action application. */
public class GameClassTest
{
    @Test
    public void defaultGameDealsSeparateHandsAndConserves108Cards()
    {
        GameClass game = new GameClass();
        assertEquals(7, game.players[0].getCardList().size());
        assertEquals(7, game.players[1].getCardList().size());
        assertNotSame(game.players[0].getCardList(), game.players[1].getCardList());
        assertEquals(93, game.deck.size());
        assertNotNull(game.currentCard);
        assertEquals("Red", game.currentColor);
        assertEquals(0, game.currentTurn);
        assertEquals(1, game.currentDirection);
        assertEquals(108, game.deck.size() + 14 + 1);
    }

    @Test
    public void initializationRejectsInvalidDependencies()
    {
        GameFixture f = new GameFixture();
        assertThrows(IllegalArgumentException.class, () -> new GameClass(
            null, f.bot, f.deck, f.game.currentCard, new Random(0), f.output));
        assertThrows(IllegalArgumentException.class, () -> new GameClass(
            f.human, f.bot, null, f.game.currentCard, new Random(0), f.output));
        assertThrows(IllegalArgumentException.class, () -> new GameClass(
            null, new Scanner(""), f.output, new Random(0)));
    }

    @Test
    public void drawTransfersExactCardWithoutChangingOtherHandOrTop()
    {
        GameFixture f = new GameFixture();
        f.deck.clear();
        Card drawn = new NumberedCards("Blue", 8);
        f.deck.add(drawn);
        Card top = f.game.currentCard;
        assertSame(drawn, f.game.drawCard(f.human));
        assertEquals(0, f.deck.size());
        assertTrue(f.human.getCardList().contains(drawn));
        assertEquals(2, f.human.getCardList().size());
        assertEquals(1, f.bot.getCardList().size());
        assertSame(top, f.game.currentCard);
    }

    @Test
    public void drawRejectsNullForeignPlayerAndEmptyDeck()
    {
        GameFixture f = new GameFixture();
        assertThrows(IllegalArgumentException.class, () -> f.game.drawCard(null));
        assertThrows(IllegalArgumentException.class, () -> f.game.drawCard(new PlayerClass()));
        assertEquals(20, f.deck.size());
        f.deck.clear();
        assertThrows(IllegalStateException.class, () -> f.game.drawCard(f.human));
        assertEquals(1, f.human.getCardList().size());
    }

    @Test
    public void numberedRulesMatchColorOrNumberAndRejectNullOrMismatch()
    {
        GameFixture f = new GameFixture();
        assertTrue(f.game.isValid(new NumberedCards("Red", 2)));
        assertTrue(f.game.isValid(new NumberedCards("Blue", 5)));
        assertFalse(f.game.isValid(new NumberedCards("Blue", 2)));
        assertFalse(f.game.isValid(null));
    }

    @Test
    public void specialRulesMatchTypeOrColorAndUseActiveWildColor()
    {
        GameFixture f = new GameFixture();
        f.game.currentCard = new SpecialCards("Red", "skip");
        assertTrue(f.game.isValid(new SpecialCards("Blue", "skip")));
        assertTrue(f.game.isValid(new SpecialCards("Red", "reverse")));
        assertFalse(f.game.isValid(new SpecialCards("Blue", "+2")));
        f.game.currentCard = new SpecialCards("Green", "wild");
        f.game.currentColor = "Green";
        assertTrue(f.game.isValid(new NumberedCards("Green", 1)));
        assertFalse(f.game.isValid(new NumberedCards("Red", 5)));
    }

    @Test
    public void drawFourIsBlockedByMatchingRegularColorOnly()
    {
        GameFixture f = new GameFixture();
        SpecialCards four = new SpecialCards("Wild", "+4");
        GameFixture.hand(f.human, four, new NumberedCards("Red", 1));
        assertFalse(f.game.isValid(four));
        GameFixture.hand(f.human, four, new NumberedCards("Blue", 5));
        assertTrue(f.game.isValid(four));
        GameFixture.hand(f.human, four, new SpecialCards("Red", "wild"));
        assertTrue(f.game.isValid(four));
        assertTrue(f.game.isValid(new SpecialCards("Wild", "wild")));
    }

    @Test
    public void legalCandidatesContainOwnedCardWildColorsAndDraw()
    {
        GameFixture f = new GameFixture();
        Card number = new NumberedCards("Red", 2);
        Card illegal = new NumberedCards("Blue", 1);
        Card wild = new SpecialCards("Wild", "wild");
        GameFixture.hand(f.human, number, illegal, wild);
        LinkedChain<Move> moves = f.game.getLegalMoves();
        assertEquals(6, moves.size());
        Move[] candidates = moves.toArray(new Move[moves.size()]);
        assertSame(number, candidates[0].getCard());
        assertEquals("Red", candidates[1].getChosenColor());
        assertEquals("Blue", candidates[2].getChosenColor());
        assertEquals("Green", candidates[3].getChosenColor());
        assertEquals("Yellow", candidates[4].getChosenColor());
        assertTrue(candidates[5].isDraw());
        for (Move move : candidates)
        {
            assertNotSame(illegal, move.getCard());
        }
        assertEquals("Wild", wild.getCardColor());
        assertEquals(3, f.human.getCardList().size());
    }

    @Test
    public void legalCandidatesHandleNoLegalCardAndEmptyHand()
    {
        GameFixture f = new GameFixture();
        assertEquals(1, f.game.getLegalMoves().size());
        assertTrue(f.game.getLegalMoves().get(0).isDraw());
        f.human.getCardList().clear();
        assertTrue(f.game.getLegalMoves().isEmpty());
    }

    @Test
    public void applyPlayRemovesExactlyOneDuplicateAndUpdatesTop()
    {
        GameFixture f = new GameFixture();
        Card first = new NumberedCards("Red", 2);
        Card second = new NumberedCards("Red", 2);
        GameFixture.hand(f.human, first, second);
        assertSame(first, f.game.applyMove(Move.play(first, null)));
        assertSame(first, f.game.currentCard);
        assertEquals("Red", f.game.currentColor);
        assertEquals(1, f.human.getCardList().size());
        assertSame(second, f.human.getCardList().get(0));
        assertEquals(0, f.game.currentTurn);
    }

    @Test
    public void applyDrawTransfersOneCardWithoutChangingTop()
    {
        GameFixture f = new GameFixture();
        Card top = f.game.currentCard;
        Card drawn = f.game.applyMove(Move.draw());
        assertTrue(f.human.getCardList().contains(drawn));
        assertEquals(19, f.deck.size());
        assertSame(top, f.game.currentCard);
    }

    @Test
    public void applyRejectsNullUnownedIllegalAndStaleMovesWithoutMutation()
    {
        GameFixture f = new GameFixture();
        Card top = f.game.currentCard;
        assertThrows(IllegalArgumentException.class, () -> f.game.applyMove(null));
        assertThrows(IllegalArgumentException.class, () -> f.game.applyMove(
            Move.play(new NumberedCards("Red", 5), null)));
        Card illegal = f.human.getCardList().get(0);
        assertThrows(IllegalArgumentException.class, () -> f.game.applyMove(Move.play(illegal, null)));
        Card onceLegal = new NumberedCards("Red", 2);
        GameFixture.hand(f.human, onceLegal);
        Move stale = Move.play(onceLegal, null);
        f.game.currentColor = "Green";
        f.game.currentCard = new NumberedCards("Green", 7);
        assertThrows(IllegalArgumentException.class, () -> f.game.applyMove(stale));
        assertEquals(1, f.human.getCardList().size());
        assertEquals(20, f.deck.size());
        assertEquals(0, f.game.currentTurn);
        assertFalse(f.game.skipNext);
        assertNotSame(top, f.game.currentCard);
        assertEquals("Green", f.game.currentColor);
    }

    @Test
    public void insufficientPenaltyDeckRejectsEntirePlayBeforeMutation()
    {
        for (String type : new String[] {"+2", "+4"})
        {
            GameFixture f = new GameFixture();
            boolean four = "+4".equals(type);
            String originalColor = four ? "Wild" : "Red";
            Card penalty = new SpecialCards(originalColor, type);
            GameFixture.hand(f.human, penalty);
            f.deck.clear();
            int remaining = four ? 3 : 1;
            for (int i = 0; i < remaining; i++)
            {
                f.deck.add(new NumberedCards("Blue", i));
            }
            Card top = f.game.currentCard;
            Move move = Move.play(penalty, four ? "Blue" : null);
            assertThrows(IllegalStateException.class, () -> f.game.applyMove(move));
            assertSame(top, f.game.currentCard);
            assertEquals("Red", f.game.currentColor);
            assertEquals(originalColor, penalty.getCardColor());
            assertTrue(f.human.getCardList().contains(penalty));
            assertEquals(1, f.human.getCardList().size());
            assertEquals(remaining, f.deck.size());
            assertEquals(1, f.bot.getCardList().size());
            assertEquals(0, f.game.currentTurn);
            assertEquals(1, f.game.currentDirection);
            assertFalse(f.game.skipNext);
        }
    }

    @Test
    public void advanceTurnWrapsInBothDirectionsAndConsumesSkipOnce()
    {
        GameFixture f = new GameFixture();
        f.game.advanceTurn();
        assertEquals(1, f.game.currentTurn);
        f.game.currentDirection = -1;
        f.game.advanceTurn();
        assertEquals(0, f.game.currentTurn);
        f.game.skipNext = true;
        f.game.advanceTurn();
        assertEquals(0, f.game.currentTurn);
        assertFalse(f.game.skipNext);
        f.game.advanceTurn();
        assertEquals(1, f.game.currentTurn);
    }

    @Test
    public void winnerDistinguishesEmptyNonemptyAndNull()
    {
        GameFixture f = new GameFixture();
        assertFalse(f.game.isWinner(f.human));
        assertFalse(f.game.isWinner(null));
        f.human.getCardList().clear();
        assertTrue(f.game.isWinner(f.human));
    }

    @Test
    public void printHandIncludesCardsHandlesEmptyAndRejectsNull()
    {
        GameFixture f = new GameFixture();
        f.game.printPlayerDeck(f.human);
        assertTrue(f.text().contains("Blue 1"));
        assertTrue(f.text().contains("B1"));
        f.bytes.reset();
        f.human.getCardList().clear();
        f.game.printPlayerDeck(f.human);
        assertFalse(f.text().contains("Blue 1"));
        assertThrows(IllegalArgumentException.class, () -> f.game.printPlayerDeck(null));
    }

    @Test
    public void runRecognizesAnAlreadyEmptyHandAndRejectsNullOutput()
    {
        GameFixture f = new GameFixture();
        f.human.getCardList().clear();
        f.game.run(f.output);
        assertTrue(f.text().contains("Human wins"));
        assertThrows(IllegalArgumentException.class, () -> f.game.run(null));
    }

    @Test
    public void bareCardIsDisplayedButRejectedAsAnUnplayableMove()
    {
        GameFixture f = new GameFixture();
        Card bare = new Card("Red");
        GameFixture.hand(f.human, bare);
        Card top = f.game.currentCard;
        f.game.printPlayerDeck(f.human);
        assertTrue(f.text().contains("Red card (not playable)"));
        assertFalse(f.game.isValid(bare));
        LinkedChain<Move> candidates = f.game.getLegalMoves();
        assertEquals(1, candidates.size());
        assertTrue(candidates.get(0).isDraw());
        assertThrows(IllegalArgumentException.class,
            () -> f.game.applyMove(Move.play(bare, null)));
        assertSame(top, f.game.currentCard);
        assertEquals("Red", f.game.currentColor);
        assertEquals(1, f.human.getCardList().size());
        assertSame(bare, f.human.getCardList().get(0));
        assertEquals(20, f.deck.size());
        assertFalse(f.game.skipNext);
        Card drawFour = new SpecialCards("Wild", "+4");
        GameFixture.hand(f.human, bare, drawFour);
        assertTrue(f.game.isValid(drawFour));
    }
}
