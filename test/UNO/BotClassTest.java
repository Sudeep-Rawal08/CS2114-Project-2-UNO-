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

    public void testDifficultyFormattingAndEmptyColorBoundary()
    {
        String[] names = {"Simple", "Normal", "Master"};
        for (int i = 0; i < names.length; i++)
        {
            BotDifficulty level = BotDifficulty.values()[i];
            assertEquals(names[i], level.toString());
            assertEquals(level, BotDifficulty.valueOf(level.name()));
            BotClass bot = new BotClass(level);
            assertEquals(level, bot.getDifficulty());
            assertTrue(bot.isBot());
            assertTrue(bot.getTitle().contains(names[i]));
            assertEquals("Red", bot.chooseColor());
        }
        org.junit.Assert.assertThrows(IllegalArgumentException.class,
            () -> BotDifficulty.valueOf("Unknown"));
    }

    public void testAllBotsPlayDrawnWildAndRejectAnEmptyDeck()
    {
        for (BotDifficulty level : BotDifficulty.values())
        {
            GameClass game = new GameClass(level);
            game.currentTurn = 1;
            BotClass bot = (BotClass)game.players[1];
            bot.getCardList().clear();
            bot.getCardList().addToEnd(new NumberedCards("Blue", 7));
            game.currentCard = new NumberedCards("Red", 5);
            game.currentColor = "Red";
            game.deck.clear();
            Card wild = new SpecialCards(null, "wild");
            game.deck.addToEnd(wild);
            assertTrue(bot.playCard(game));
            assertSame(wild, game.currentCard);
            assertEquals("Blue", game.currentColor);
            assertEquals(1, bot.getCardList().size());
            game.currentCard = new NumberedCards("Red", 5);
            game.currentColor = "Red";
            org.junit.Assert.assertThrows(IllegalStateException.class,
                () -> bot.playCard(game));
            assertEquals(1, bot.getCardList().size());
        }
    }

    public void testAllSpecialTypesCanBeScoredAndPlayedAsLastCard()
    {
        for (BotDifficulty level : new BotDifficulty[] {
            BotDifficulty.NORMAL, BotDifficulty.MASTER})
        {
            for (String type : new String[] {"skip", "reverse", "+2", "wild", "+4"})
            {
                GameClass game = new GameClass(level);
                game.currentTurn = 1;
                BotClass bot = (BotClass)game.players[1];
                bot.getCardList().clear();
                boolean wild = type.equals("wild") || type.equals("+4");
                Card card = new SpecialCards(wild ? null : game.currentColor, type);
                bot.getCardList().addToEnd(card);
                int before = game.players[0].getCardList().size();
                assertSame(card, bot.chooseMove(game).getCard());
                assertTrue(bot.playCard(game));
                assertTrue(game.isWinner(bot));
                int penalty = type.equals("+2") ? 2 : type.equals("+4") ? 4 : 0;
                assertEquals(before + penalty, game.players[0].getCardList().size());
                assertSame(card, game.currentCard);
            }
        }
    }

}
