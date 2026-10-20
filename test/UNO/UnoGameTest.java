package UNO;

/** Integration tests for setting up a game with the selected bot difficulty. */
public class UnoGameTest extends student.TestCase
{
    public void testDefaultGameUsesSimpleDifficulty()
    {
        GameClass game = new GameClass();
        assertEquals(BotDifficulty.SIMPLE,
            ((BotClass)game.players[1]).getDifficulty());
    }

    public void testGameUsesSelectedSimpleDifficulty()
    {
        GameClass game = new GameClass(BotDifficulty.SIMPLE);
        assertEquals(BotDifficulty.SIMPLE,
            ((BotClass)game.players[1]).getDifficulty());
        assertEquals(7, game.players[0].getCardList().size());
        assertEquals(7, game.players[1].getCardList().size());
    }

    public void testGameUsesSelectedNormalDifficulty()
    {
        GameClass game = new GameClass(BotDifficulty.NORMAL);
        assertEquals(BotDifficulty.NORMAL,
            ((BotClass)game.players[1]).getDifficulty());
        assertTrue(game.currentCard instanceof NumberedCards);
    }

    public void testGameUsesSelectedMasterDifficulty()
    {
        GameClass game = new GameClass(BotDifficulty.MASTER);
        assertEquals(BotDifficulty.MASTER,
            ((BotClass)game.players[1]).getDifficulty());
        assertEquals(game.currentCard.getCardColor(), game.currentColor);
    }

    public void testGameRejectsNullDifficulty()
    {
        boolean caughtExpected = false;
        try
        {
            new GameClass(null);
        }
        catch (IllegalArgumentException exception)
        {
            caughtExpected = true;
        }
        assertTrue("Expected IllegalArgumentException", caughtExpected);
    }
}
