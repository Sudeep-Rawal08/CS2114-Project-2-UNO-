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

}
