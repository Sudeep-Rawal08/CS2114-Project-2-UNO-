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

    public void testLegalMovesHandleEmptyHandAndRejectNullPlayer()
    {
        GameClass game = new GameClass();
        game.players[1].getCardList().clear();
        LinkedChain<Move> moves = game.getLegalMoves();
        assertEquals(1, moves.size());
        assertTrue(moves.get(0).isDraw());
        try
        {
            game.getLegalMoves(null);
            fail("Expected invalid player");
        }
        catch (IllegalArgumentException expected)
        {
            assertTrue(game.players[1].getCardList().isEmpty());
        }
    }

    public void testCurrentPlayerApplyMoveAndNullRejectionPreserveState()
    {
        GameClass game = new GameClass();
        game.currentTurn = 0;
        PlayerClass player = game.players[0];
        player.getCardList().clear();
        Card card = new NumberedCards(game.currentColor, 8);
        player.getCardList().addToEnd(card);
        assertTrue(game.applyMove(Move.play(card, null)));
        assertSame(card, game.currentCard);
        assertTrue(player.getCardList().isEmpty());
        try
        {
            game.applyMove((Move)null);
            fail("Expected invalid move");
        }
        catch (IllegalArgumentException expected)
        {
            assertSame(card, game.currentCard);
        }
        try
        {
            game.applyMove(null, Move.draw());
            fail("Expected invalid player");
        }
        catch (IllegalArgumentException expected)
        {
            assertTrue(player.getCardList().isEmpty());
        }
    }

    public void testInsufficientPenaltyRejectsBeforeAnyStateChange()
    {
        for (String type : new String[] {"+2", "+4"})
        {
            GameClass game = new GameClass();
            PlayerClass player = game.players[0];
            player.getCardList().clear();
            Card penalty = new SpecialCards(type.equals("+4") ? null : game.currentColor, type);
            player.getCardList().addToEnd(penalty);
            game.deck.clear();
            Card available = new NumberedCards("Blue", 1);
            game.deck.addToEnd(available);
            Card top = game.currentCard;
            String color = game.currentColor;
            int opponentSize = game.players[1].getCardList().size();
            try
            {
                game.applyMove(player, Move.play(penalty, type.equals("+4") ? "Green" : null));
                fail("Expected insufficient penalty deck");
            }
            catch (IllegalStateException expected)
            {
                assertSame(top, game.currentCard);
                assertEquals(color, game.currentColor);
                assertTrue(player.getCardList().contains(penalty));
                assertEquals(opponentSize, game.players[1].getCardList().size());
                assertEquals(1, game.deck.size());
                assertSame(available, game.deck.get(0));
                assertFalse(game.skipNext);
            }
        }
    }

    public void testBaseCardCannotBecomeALegalPlay()
    {
        GameClass game = new GameClass();
        Card bare = new Card(game.currentColor);
        assertFalse(game.isValid(bare));
        game.players[0].getCardList().clear();
        game.players[0].getCardList().addToEnd(bare);
        assertEquals(1, game.getLegalMoves(game.players[0]).size());
        assertTrue(game.getLegalMoves(game.players[0]).get(0).isDraw());
    }

    public void testHumanDrawMoveAndIllegalOwnedPlayPreserveTop()
    {
        GameClass game = new GameClass();
        game.currentTurn = 0;
        game.currentCard = new NumberedCards("Red", 5);
        game.currentColor = "Red";
        PlayerClass player = game.players[0];
        player.getCardList().clear();
        game.deck.clear();
        Card drawn = new NumberedCards("Blue", 7);
        game.deck.addToEnd(drawn);
        Card top = game.currentCard;
        assertFalse(game.applyMove(Move.draw()));
        assertSame(top, game.currentCard);
        assertSame(drawn, player.getCardList().get(0));
        org.junit.Assert.assertThrows(IllegalArgumentException.class,
            () -> game.applyMove(Move.play(drawn, null)));
        org.junit.Assert.assertThrows(IllegalStateException.class,
            () -> game.applyMove(Move.draw()));
        assertSame(top, game.currentCard);
        assertEquals(1, player.getCardList().size());
    }

    public void testHandUsesAsciiCardsAndWrapsAfterFour()
    {
        GameClass game = new GameClass();
        PlayerClass player = game.players[0];
        player.getCardList().clear();
        player.getCardList().addToEnd(new NumberedCards("Red", 7));
        player.getCardList().addToEnd(new SpecialCards("Blue", "+2"));
        player.getCardList().addToEnd(new SpecialCards(null, "wild"));
        player.getCardList().addToEnd(new SpecialCards("Yellow", "reverse"));
        player.getCardList().addToEnd(new SpecialCards(null, "+4"));
        java.io.PrintStream original = System.out;
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
        try
        {
            System.setOut(new java.io.PrintStream(output));
            game.printPlayerDeck(player);
        }
        finally
        {
            System.setOut(original);
        }
        String text = output.toString();
        assertTrue(text.contains("+---------------+"));
        assertTrue(text.contains("[1]"));
        assertTrue(text.contains("[5]"));
        assertTrue(text.contains("RED"));
        assertTrue(text.contains("BLUE"));
        assertTrue(text.contains("WILD"));
        assertTrue(text.contains("REVERSE"));
        assertTrue(text.contains("+4"));
        int rows = 0;
        for (String line : text.split("\r?\n"))
        {
            if (line.contains("[1]"))
            {
                assertTrue(line.contains("[4]"));
                assertFalse(line.contains("[5]"));
            }
            if (line.contains("+---------------+")) rows++;
            assertTrue(line.length() <= 74);
        }
        assertEquals(4, rows);
        assertEquals(5, player.getCardList().size());
        assertEquals(7, player.getCardList().get(0).getCardNumber());
    }

}
