package UNO;


/** Tests bot turn decisions and card effects. */
public class BotClassTest extends student.TestCase
{
    public void testBotPlaysFirstValidCard()
    {
        GameClass game = new GameClass();
        BotClass bot = new BotClass();
        game.players[1] = bot;
        game.currentCard = new NumberedCards("Red", 3);
        game.currentColor = "Red";
        NumberedCards valid = new NumberedCards("Red", 8);
        bot.getCardList().addToEnd(valid);
        assertTrue(bot.playCard(game));
        assertSame(valid, game.currentCard);
        assertFalse(bot.getCardList().contains(valid));
    }
    public void testBotDrawsWhenNoCardIsValid()
    {
        GameClass game = new GameClass();
        BotClass bot = new BotClass();
        game.players[1] = bot;
        game.currentCard = new NumberedCards("Red", 1);
        game.currentColor = "Red";
        bot.getCardList().addToEnd(new NumberedCards("Blue", 9));

        // Replace the random deck with one known-invalid draw so this test
        // does not depend on the game's shuffled deck.
        game.deck.clear();
        game.deck.addToEnd(new NumberedCards("Green", 7));

        int before = bot.getCardList().size();
        assertFalse(bot.playCard(game));
        assertEquals(before + 1, bot.getCardList().size());
    }
    public void testBotRejectsNullGame()
    {
                {
            boolean caughtExpected = false;
            try
            {
                new BotClass().playCard(null);
            }
            catch (IllegalArgumentException e)
            {
                caughtExpected = true;
            }
            assertTrue("Expected IllegalArgumentException", caughtExpected);
        };
    }

    
    public void testDifficultyIsStoredAndDefaultsToSimple()
    {
        assertEquals(BotDifficulty.SIMPLE, new BotClass().getDifficulty());
        assertEquals(BotDifficulty.NORMAL,
            new BotClass(BotDifficulty.NORMAL).getDifficulty());
        assertEquals(BotDifficulty.MASTER,
            new BotClass(BotDifficulty.MASTER).getDifficulty());
    }

    public void testBotChoosesAndAppliesMove()
    {
        GameClass game = new GameClass(BotDifficulty.SIMPLE);
        BotClass bot = (BotClass)game.players[1];
        bot.getCardList().clear();
        game.currentCard = new NumberedCards("Red", 3);
        game.currentColor = "Red";
        NumberedCards valid = new NumberedCards("Red", 8);
        bot.getCardList().addToEnd(valid);

        Move move = bot.chooseMove(game);
        assertFalse(move.isDraw());
        assertSame(valid, move.getCard());
        assertTrue(bot.playCard(game));
        assertSame(valid, game.currentCard);
        assertFalse(bot.getCardList().contains(valid));
    }

    public void testBotDifficultyRejectsNull()
    {
        boolean caughtExpected = false;
        try
        {
            new BotClass(null);
        }
        catch (IllegalArgumentException exception)
        {
            caughtExpected = true;
        }
        assertTrue(caughtExpected);
    }

    public void testNormalPrefersPenaltyAndMasterAlsoConsidersColors()
    {
        for (BotDifficulty level : new BotDifficulty[] {
            BotDifficulty.NORMAL, BotDifficulty.MASTER})
        {
            GameClass game = new GameClass(level);
            BotClass bot = (BotClass)game.players[1];
            bot.getCardList().clear();
            game.currentCard = new NumberedCards("Red", 5);
            game.currentColor = "Red";
            Card penalty = new SpecialCards("Red", "+2");
            Card blueFive = new NumberedCards("Blue", 5);
            bot.getCardList().addToEnd(penalty);
            bot.getCardList().addToEnd(blueFive);
            for (int number = 6; number <= 8; number++)
            {
                bot.getCardList().addToEnd(new NumberedCards("Blue", number));
            }
            Card selected = bot.chooseMove(game).getCard();
            assertSame(level == BotDifficulty.NORMAL ? penalty : blueFive, selected);
            assertEquals(5, bot.getCardList().size());
        }
    }

    public void testChooseColorUsesMajorityAndRedForEmptyHand()
    {
        BotClass bot = new BotClass(BotDifficulty.MASTER);
        assertEquals("Red", bot.chooseColor());
        bot.getCardList().addToEnd(new NumberedCards("Blue", 1));
        bot.getCardList().addToEnd(new NumberedCards("Green", 2));
        bot.getCardList().addToEnd(new NumberedCards("Blue", 3));
        assertEquals("Blue", bot.chooseColor());
        assertTrue(bot.isBot());
        assertTrue(bot.getTitle().contains("Master"));
        try
        {
            bot.chooseMove(null);
            fail("Expected invalid game");
        }
        catch (IllegalArgumentException expected)
        {
            assertEquals(3, bot.getCardList().size());
        }
    }

}
