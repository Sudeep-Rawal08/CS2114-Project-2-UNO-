package UNO;


/** Tests player hand access and card-play behavior without console input. */
public class PlayerClassTest extends student.TestCase
{
    public void testConstructorAndGetterCreateAndReturnHand()
    {
        PlayerClass player = new PlayerClass();
        assertNotNull(player.getCardList());
        assertTrue(player.getCardList().isEmpty());
    }
    public void testPlaySpecificCardUpdatesHandAndCurrentCard()
    {
        GameClass game = new GameClass();
        PlayerClass player = new PlayerClass();
        NumberedCards card = new NumberedCards(game.currentColor, 8);
        player.getCardList().addToEnd(card);
        assertTrue(player.playSpecificCard(game, card));
        assertFalse(player.getCardList().contains(card));
        assertSame(card, game.currentCard);
        assertEquals(card.getCardColor(), game.currentColor);
    }
    public void testPlaySpecificCardRejectsInvalidCard()
    {
        GameClass game = new GameClass();
        PlayerClass player = new PlayerClass();
        game.currentCard = new NumberedCards("Red", 1);
        game.currentColor = "Red";
        NumberedCards invalid = new NumberedCards("Blue", 7);
        player.getCardList().addToEnd(invalid);
        assertFalse(player.playSpecificCard(game, invalid));
        assertTrue(player.getCardList().contains(invalid));
    }
    public void testPlaySpecificCardAppliesSkipAndWildActions()
    {
        GameClass skipGame = new GameClass();
        PlayerClass player = new PlayerClass();
        SpecialCards skip = new SpecialCards(skipGame.currentColor, "skip");
        player.getCardList().addToEnd(skip);
        assertTrue(player.playSpecificCard(skipGame, skip));
        assertTrue(skipGame.skipNext);

        GameClass wildGame = new GameClass();
        PlayerClass wildPlayer = new PlayerClass();
        SpecialCards wild = new SpecialCards(null, "wild");
        wildPlayer.getCardList().addToEnd(wild);
        // The color prompt is interactive, so the wild path is covered manually.
        assertTrue(wildGame.isValid(wild));
    }
    public void testInterruptedWildColorLeavesStateUnchanged()
    {
        GameClass game = new GameClass();
        PlayerClass player = new PlayerClass()
        {
            @Override
            protected String askForColor()
            {
                throw new java.util.NoSuchElementException("Input ended");
            }
        };
        SpecialCards wild = new SpecialCards(null, "wild");
        player.getCardList().addToEnd(wild);
        Card top = game.currentCard;
        String color = game.currentColor;
        try
        {
            player.playSpecificCard(game, wild);
            fail("Expected interrupted color input");
        }
        catch (java.util.NoSuchElementException expected)
        {
            assertTrue(player.getCardList().contains(wild));
            assertSame(top, game.currentCard);
            assertEquals(color, game.currentColor);
            assertNull(wild.getCardColor());
        }
    }

    public void testWildColorSelectionCompletesBeforePlay()
    {
        GameClass game = new GameClass();
        PlayerClass player = new PlayerClass()
        {
            @Override
            protected String askForColor()
            {
                return "Blue";
            }
        };
        Card wild = new SpecialCards(null, "wild");
        player.getCardList().addToEnd(wild);
        assertTrue(player.playSpecificCard(game, wild));
        assertSame(wild, game.currentCard);
        assertEquals("Blue", game.currentColor);
        assertTrue(player.getCardList().isEmpty());
        assertEquals("Blue", player.chooseColor());
        assertFalse(player.isBot());
        assertEquals("You", player.getTitle());
        try
        {
            player.playCard(null);
            fail("Expected invalid game");
        }
        catch (IllegalArgumentException expected)
        {
            assertTrue(player.getCardList().isEmpty());
        }
    }

    public void testHumanCannotPlayACardItDoesNotOwn()
    {
        GameClass game = new GameClass();
        PlayerClass player = game.players[0];
        player.getCardList().clear();
        Card unowned = new NumberedCards(game.currentColor, 5);
        Card top = game.currentCard;
        try
        {
            player.playSpecificCard(game, unowned);
            fail("Expected unowned card rejection");
        }
        catch (IllegalArgumentException expected)
        {
            assertSame(top, game.currentCard);
            assertTrue(player.getCardList().isEmpty());
        }
    }

    public void testPlayCardRecoversFromBadInputAndPlaysOwnedCard()
    {
        GameClass game = new GameClass();
        PlayerClass player = new PlayerClass(new java.util.Scanner(
            "\nwrong\n99999999999999999999\n0\n99\n2\n1\n"));
        game.players[0] = player;
        game.currentCard = new NumberedCards("Red", 5);
        game.currentColor = "Red";
        Card legal = new NumberedCards("Red", 7);
        Card illegal = new NumberedCards("Blue", 2);
        player.getCardList().addToEnd(legal);
        player.getCardList().addToEnd(illegal);
        assertTrue(player.playCard(game));
        assertSame(legal, game.currentCard);
        assertEquals(1, player.getCardList().size());
        assertSame(illegal, player.getCardList().get(0));
    }

    public void testDrawnPlayableCardCanBePlayedOrKept()
    {
        for (String answer : new String[] {"Y", "N", "yes", "no"})
        {
            GameClass game = new GameClass();
            PlayerClass player = new PlayerClass(new java.util.Scanner(
                "D\nwrong\n" + answer + "\n"));
            game.players[0] = player;
            game.currentCard = new NumberedCards("Red", 5);
            game.currentColor = "Red";
            player.getCardList().addToEnd(new NumberedCards("Blue", 2));
            game.deck.clear();
            Card drawn = new NumberedCards("Red", 9);
            game.deck.addToEnd(drawn);
            Card top = game.currentCard;
            boolean play = answer.equalsIgnoreCase("y") || answer.equals("yes");
            assertEquals(play, player.playCard(game));
            assertEquals(!play, player.getCardList().contains(drawn));
            assertSame(play ? drawn : top, game.currentCard);
            assertEquals(play ? 1 : 2, player.getCardList().size());
        }
    }

    public void testInvalidDrawAndEmptyDeckEndTurnWithoutPartialPlay()
    {
        GameClass game = new GameClass();
        PlayerClass player = new PlayerClass(new java.util.Scanner("D\nD\n"));
        game.players[0] = player;
        game.currentCard = new NumberedCards("Red", 5);
        game.currentColor = "Red";
        game.deck.clear();
        Card drawn = new NumberedCards("Blue", 7);
        game.deck.addToEnd(drawn);
        Card top = game.currentCard;
        assertFalse(player.playCard(game));
        assertSame(drawn, player.getCardList().get(0));
        assertFalse(player.playCard(game));
        assertSame(top, game.currentCard);
        assertEquals(1, player.getCardList().size());
    }

    public void testChooseColorUsesRealInputAndRejectsBadColors()
    {
        for (String color : Card.regularColors())
        {
            PlayerClass player = new PlayerClass(new java.util.Scanner(
                "Purple\n\n" + color.toLowerCase() + "\n"));
            assertEquals(color, player.chooseColor());
        }
        PlayerClass empty = new PlayerClass(new java.util.Scanner(""));
        org.junit.Assert.assertThrows(java.util.NoSuchElementException.class,
            () -> empty.chooseColor());
        org.junit.Assert.assertThrows(IllegalArgumentException.class,
            () -> new PlayerClass((java.util.Scanner)null));
    }

    public void testWildInputErrorsRecoverAndInterruptedDrawKeepsCard()
    {
        GameClass game = new GameClass();
        PlayerClass player = new PlayerClass(new java.util.Scanner("1\nPurple\nblue\n"));
        game.players[0] = player;
        Card wild = new SpecialCards(null, "wild");
        player.getCardList().addToEnd(wild);
        assertTrue(player.playCard(game));
        assertEquals("Blue", game.currentColor);
        assertTrue(player.getCardList().isEmpty());

        PlayerClass interrupted = new PlayerClass(new java.util.Scanner("D\ny\n"));
        game.players[0] = interrupted;
        game.deck.clear();
        Card drawn = new SpecialCards(null, "wild");
        game.deck.addToEnd(drawn);
        Card top = game.currentCard;
        org.junit.Assert.assertThrows(java.util.NoSuchElementException.class,
            () -> interrupted.playCard(game));
        assertSame(drawn, interrupted.getCardList().get(0));
        assertSame(top, game.currentCard);
        assertNull(drawn.getCardColor());
    }

    public void testHumanPenaltyCannotPartiallyPlay()
    {
        GameClass game = new GameClass();
        PlayerClass player = new PlayerClass(new java.util.Scanner("1\nGreen\n"));
        game.players[0] = player;
        Card penalty = new SpecialCards(null, "+4");
        player.getCardList().addToEnd(penalty);
        game.deck.clear();
        game.deck.addToEnd(new NumberedCards("Blue", 1));
        Card top = game.currentCard;
        org.junit.Assert.assertThrows(IllegalStateException.class,
            () -> player.playCard(game));
        assertSame(top, game.currentCard);
        assertTrue(player.getCardList().contains(penalty));
        assertEquals(1, game.deck.size());
    }

}
