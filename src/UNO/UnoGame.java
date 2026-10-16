package UNO;

import java.util.Random;
import java.util.Scanner;

/** Console entry point. */
public final class UnoGame
{
    private UnoGame()
    {
    }


    /**
     * @param args
     *            no command-line arguments are required or accepted
     */
    public static void main(String[] args)
    {
        if (args != null && args.length != 0)
        {
            System.out.println("Usage: java -cp build/classes UNO.UnoGame");
            return;
        }
        Scanner input = new Scanner(System.in);
        System.out.println("UNO prototype - local bot opponents");
        System.out
            .println("Codes: R/B/G/Y + 0-9, + (+2), R (reverse), S (skip).");
        System.out.println("Wild: WW; draw four: W4; draw: D; quit: Q.");
        while (true)
        {
            System.out.println(
                "Choose bot: 1 Simple, 2 Normal, 3 Master, or Q to quit:");
            if (!input.hasNextLine())
            {
                System.out.println("Goodbye.");
                return;
            }
            String answer = input.nextLine().trim();
            if ("Q".equalsIgnoreCase(answer))
            {
                System.out.println("Goodbye.");
                return;
            }
            if (!answer.matches("[123]"))
            {
                System.out.println("Invalid difficulty. Enter 1, 2, or 3.");
                continue;
            }
            BotDifficulty difficulty =
                BotDifficulty.values()[Integer.parseInt(answer) - 1];
            System.out.println("Selected bot: " + difficulty);
            GameClass game =
                new GameClass(difficulty, input, System.out, new Random());
            game.run(System.out);
            return;
        }
    }
}
