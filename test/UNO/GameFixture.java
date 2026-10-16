package UNO;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

/** Controlled game state for tests; no test utilities live in app classes. */
final class GameFixture
{
    final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    final PrintStream output = new PrintStream(bytes);
    final PlayerClass human;
    final BotClass bot;
    final LinkedChain<Card> deck = new LinkedChain<Card>();
    final GameClass game;

    GameFixture()
    {
        this("", BotDifficulty.SIMPLE);
    }

    GameFixture(String input, BotDifficulty difficulty)
    {
        human = new PlayerClass(new Scanner(input), output);
        bot = new BotClass(difficulty);
        for (int i = 0; i < 20; i++)
        {
            deck.addToEnd(new NumberedCards("Yellow", i % 10));
        }
        game = new GameClass(human, bot, deck,
            new NumberedCards("Red", 5), new Random(0), output);
        hand(human, new NumberedCards("Blue", 1));
        hand(bot, new NumberedCards("Green", 2));
    }

    static void hand(PlayerClass player, Card... cards)
    {
        player.getCardList().clear();
        for (Card card : cards)
        {
            player.getCardList().addToEnd(card);
        }
    }

    String text()
    {
        return bytes.toString();
    }
}
