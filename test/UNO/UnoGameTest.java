package UNO;

import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

/** Console entry point and full game-loop termination checks. */
public class UnoGameTest
{
    @Test(timeout = 2000)
    public void mainSelectsEachDifficultyAndExitsOnQuit()
    {
        String[] levels = {"SIMPLE", "NORMAL", "MASTER"};
        for (int i = 0; i < levels.length; i++)
        {
            String text = runMain((i + 1) + "\nQ\n", new String[0]);
            assertTrue(text.contains("Selected bot: " + levels[i]));
            assertTrue(text.contains("Top:"));
            assertTrue(text.contains("Round ended"));
        }
    }

    @Test(timeout = 1000)
    public void mainRecoversFromInvalidDifficultyAndHandlesEofOrNullArgs()
    {
        String text = runMain("wrong\n2\n", null);
        assertTrue(text.contains("Invalid difficulty"));
        assertTrue(text.contains("Selected bot: NORMAL"));
        assertTrue(text.contains("Round ended"));
        assertTrue(runMain("", new String[0]).contains("Goodbye"));
        assertTrue(runMain("Q\n", new String[0]).contains("Goodbye"));
    }

    @Test
    public void mainRejectsUnsupportedArgumentsWithoutStartingRound()
    {
        String text = runMain("", new String[] {"--unsupported"});
        assertTrue(text.contains("Usage:"));
        assertFalse(text.contains("Top:"));
    }

    @Test(timeout = 1000)
    public void fullRoundCanFinishWithHumanWin()
    {
        GameFixture f = new GameFixture("R5\n", BotDifficulty.SIMPLE);
        GameFixture.hand(f.human, new NumberedCards("Red", 5));
        f.game.run(f.output);
        assertTrue(f.text().contains("Human wins!"));
        assertFalse(f.text().contains("Bot wins!"));
    }

    @Test(timeout = 1000)
    public void fullRoundRunsHumanThenBotAndAnnouncesBotWin()
    {
        GameFixture f = new GameFixture("D\n", BotDifficulty.MASTER);
        f.deck.clear();
        f.deck.add(new NumberedCards("Blue", 2));
        GameFixture.hand(f.bot, new NumberedCards("Red", 5));
        f.game.run(f.output);
        assertTrue(f.text().contains("Turn: Human"));
        assertTrue(f.text().contains("Turn: Bot"));
        assertTrue(f.text().contains("Bot wins!"));
        assertEquals(2, f.human.getCardList().size());
    }

    @Test(timeout = 1000)
    public void fullRoundCatchesEmptyDeckAndTerminatesOnEof()
    {
        GameFixture depleted = new GameFixture("D\n", BotDifficulty.SIMPLE);
        depleted.deck.clear();
        depleted.game.run(depleted.output);
        assertTrue(depleted.text().contains("Round stopped: no cards are available"));
        GameFixture eof = new GameFixture();
        eof.game.run(eof.output);
        assertTrue(eof.text().contains("Round ended"));
    }

    private static String runMain(String input, String[] args)
    {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try
        {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(bytes));
            UnoGame.main(args);
            return bytes.toString();
        }
        finally
        {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }
}
