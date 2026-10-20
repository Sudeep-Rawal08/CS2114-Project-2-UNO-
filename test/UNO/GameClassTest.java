package UNO;


/** Tests the public rules and setup methods in GameClass. */
public class GameClassTest extends student.TestCase
{
    public void testConstructorCreatesPlayersAndNumberedStartingCard()
    {
        GameClass game = new GameClass();
        assertEquals(2, game.players.length);
        assertEquals(7, game.players[0].getCardList().size());
        assertEquals(7, game.players[1].getCardList().size());
        assertTrue(game.currentCard instanceof NumberedCards);
        assertEquals(game.currentCard.getCardColor(), game.currentColor);
        assertTrue(game.deck.size() > 0);
    }
    public void testDrawCardMovesOneCardIntoPlayersHand()
    {
        GameClass game = new GameClass();
        PlayerClass player = new PlayerClass();
        int deckBefore = game.deck.size();
        Card drawn = game.drawCard(player);
        assertNotNull(drawn);
        assertEquals(1, player.getCardList().size());
        assertEquals(deckBefore - 1, game.deck.size());
        assertTrue(player.getCardList().contains(drawn));
    }
    public void testDrawCardRejectsNullAndEmptyDeck()
    {
        GameClass game = new GameClass();
                {
            boolean caughtExpected = false;
            try
            {
                game.drawCard(null);
            }
            catch (IllegalArgumentException e)
            {
                caughtExpected = true;
            }
            assertTrue("Expected IllegalArgumentException", caughtExpected);
        };
        game.deck.clear();
                {
            boolean caughtExpected = false;
            try
            {
                game.drawCard(new PlayerClass());
            }
            catch (IllegalStateException e)
            {
                caughtExpected = true;
            }
            assertTrue("Expected IllegalStateException", caughtExpected);
        };
    }
    public void testIsValidAcceptsWildAndMatchingColorNumberOrType()
    {
        GameClass game = new GameClass();
        game.currentCard = new NumberedCards("Red", 5);
        game.currentColor = "Red";
        assertTrue(game.isValid(new NumberedCards("Red", 2)));
        assertTrue(game.isValid(new NumberedCards("Blue", 5)));
        assertTrue(game.isValid(new SpecialCards(null, "wild")));
        assertTrue(game.isValid(new SpecialCards(null, "+4")));
        game.currentCard = new SpecialCards("Green", "skip");
        game.currentColor = "Green";
        assertTrue(game.isValid(new SpecialCards("Red", "skip")));
        assertFalse(game.isValid(new NumberedCards("Blue", 4)));
        assertFalse(game.isValid(null));
        game.currentCard = null;
        assertFalse(game.isValid(new NumberedCards("Red", 1)));
    }
    public void testPrintPlayerDeckAcceptsEmptyAndNonemptyHands()
    {
        GameClass game = new GameClass();
        PlayerClass player = new PlayerClass();
        game.printPlayerDeck(player);
        player.getCardList().addToEnd(new NumberedCards("Blue", 3));
        game.printPlayerDeck(player);
                {
            boolean caughtExpected = false;
            try
            {
                game.printPlayerDeck(null);
            }
            catch (IllegalArgumentException e)
            {
                caughtExpected = true;
            }
            assertTrue("Expected IllegalArgumentException", caughtExpected);
        };
    }
    public void testIsWinnerChecksForEmptyHand()
    {
        GameClass game = new GameClass();
        PlayerClass player = new PlayerClass();
        assertTrue(game.isWinner(player));
        player.getCardList().addToEnd(new NumberedCards("Red", 1));
        assertFalse(game.isWinner(player));
        assertFalse(game.isWinner(null));
    }
    public void testDirectionSkipDrawAndColorStateMethodsWork()
    {
        GameClass game = new GameClass();
        assertEquals(1, game.currentDirection);
        game.reverseDirection();
        assertEquals(-1, game.currentDirection);
        game.skipNextPlayer();
        assertTrue(game.skipNext);
        int before = game.players[1].getCardList().size();
        game.drawForNextPlayer(2);
        assertEquals(before + 2, game.players[1].getCardList().size());
        assertTrue(game.skipNext);
        game.setCurrentColor("Blue");
        assertEquals("Blue", game.currentColor);
                {
            boolean caughtExpected = false;
            try
            {
                game.setCurrentColor("Wild");
            }
            catch (IllegalArgumentException e)
            {
                caughtExpected = true;
            }
            assertTrue("Expected IllegalArgumentException", caughtExpected);
        };
                {
            boolean caughtExpected = false;
            try
            {
                game.setCurrentColor(null);
            }
            catch (IllegalArgumentException e)
            {
                caughtExpected = true;
            }
            assertTrue("Expected IllegalArgumentException", caughtExpected);
        };
    }

    
    public void testDifficultyConstructorAndLegalMoves()
    {
        GameClass game = new GameClass(BotDifficulty.MASTER);
        assertEquals(BotDifficulty.MASTER,
            ((BotClass)game.players[1]).getDifficulty());
        BotClass bot = (BotClass)game.players[1];
        bot.getCardList().clear();
        game.currentCard = new NumberedCards("Red", 5);
        game.currentColor = "Red";
        NumberedCards valid = new NumberedCards("Blue", 5);
        SpecialCards wild = new SpecialCards(null, "wild");
        bot.getCardList().addToEnd(valid);
        bot.getCardList().addToEnd(wild);

        LinkedChain<Move> moves = game.getLegalMoves(bot);
        assertEquals(6, moves.size());
        assertSame(valid, moves.get(0).getCard());
        assertEquals("Red", moves.get(1).getChosenColor());
        assertEquals("Blue", moves.get(2).getChosenColor());
        assertEquals("Green", moves.get(3).getChosenColor());
        assertEquals("Yellow", moves.get(4).getChosenColor());
        assertTrue(moves.get(5).isDraw());
    }

    public void testApplyWildMoveSetsChosenColor()
    {
        GameClass game = new GameClass();
        PlayerClass player = game.players[0];
        SpecialCards wild = new SpecialCards(null, "wild");
        player.getCardList().clear();
        player.getCardList().addToEnd(wild);
        game.currentCard = new NumberedCards("Red", 5);
        game.currentColor = "Red";

        assertTrue(game.applyMove(player, Move.play(wild, "Green")));
        assertSame(wild, game.currentCard);
        assertEquals("Green", game.currentColor);
        assertEquals("Green", wild.getCardColor());
        assertFalse(player.getCardList().contains(wild));
    }

    public void testApplyMoveRejectsCardNotInHand()
    {
        GameClass game = new GameClass();
        PlayerClass player = new PlayerClass();
        game.currentCard = new NumberedCards("Red", 1);
        game.currentColor = "Red";
        boolean caughtExpected = false;
        try
        {
            game.applyMove(player,
                Move.play(new NumberedCards("Red", 2), null));
        }
        catch (IllegalArgumentException exception)
        {
            caughtExpected = true;
        }
        assertTrue(caughtExpected);
    }

}
