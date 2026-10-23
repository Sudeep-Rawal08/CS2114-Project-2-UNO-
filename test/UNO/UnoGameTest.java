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
    public void testMainEndsCleanlyWhenInputEnds() throws Exception
    {
        for (String difficulty : new String[] {"1", "2", "3"})
        {
            String output = runMain(difficulty + "\n");
            assertTrue(output.contains("Input ended. Ending the round."));
            assertTrue(output.contains("Thanks for playing!"));
        }
    }

    public void testMainHandlesEmptyOrInvalidDifficultyInput() throws Exception
    {
        assertTrue(runMain("").contains("No input available"));
        assertTrue(runMain("wrong\n1\n").contains("Invalid choice"));
    }

    public void testMainEndsAfterDrawingWhenInputEnds() throws Exception
    {
        String output = runMain("1\nD\n");
        assertTrue(output.contains("Thanks for playing!"));
    }

    private String runMain(String input) throws Exception
    {
        String javaCommand = System.getProperty("java.home")
            + java.io.File.separator + "bin" + java.io.File.separator + "java";
        java.io.File outputFile = java.io.File.createTempFile("uno-console-", ".txt");
        Process process = new ProcessBuilder(javaCommand, "-cp",
            System.getProperty("java.class.path"), "UNO.UnoGame")
            .redirectErrorStream(true).redirectOutput(outputFile).start();
        try
        {
            process.getOutputStream().write(input.getBytes("UTF-8"));
            process.getOutputStream().close();
            assertTrue("Game did not terminate", process.waitFor(10,
                java.util.concurrent.TimeUnit.SECONDS));
            String text = new String(java.nio.file.Files.readAllBytes(
                outputFile.toPath()), java.nio.charset.StandardCharsets.UTF_8);
            assertEquals(text, 0, process.exitValue());
            return text;
        }
        finally
        {
            process.destroyForcibly();
            java.nio.file.Files.deleteIfExists(outputFile.toPath());
        }
    }

    public void testMainCanPlayThroughARound() throws Exception
    {
        assertNotNull(new UnoGame());
        StringBuilder input = new StringBuilder("1\n");
        for (int i = 0; i < 110; i++) input.append("D\nn\n");
        String output = runMain(input.toString());
        assertTrue(output.contains("Would you like to play again?"));
        assertTrue(output.contains("Thanks for playing!"));
        assertFalse(output.contains("Input ended. Ending the round."));
    }

    public void testWelcomeShowsLargeAsciiUnoTitle() throws Exception
    {
        String output = runMain("");
        assertTrue(output.contains("UU     UU  NNN    NN   OOOOOOO"));
        assertTrue(output.contains("UUUUUUU   NN    NNN   OOOOOOO"));
        assertTrue(output.contains("Welcome to UNO!"));
    }

}
