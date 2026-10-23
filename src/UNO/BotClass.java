package UNO;

/** A bot that chooses moves according to its difficulty setting. */
public class BotClass
    extends PlayerClass
{
    private final BotDifficulty difficulty;

    public BotClass()
    {
        this(BotDifficulty.SIMPLE);
    }


    public BotClass(BotDifficulty selectedDifficulty)
    {
        if (selectedDifficulty == null)
        {
            throw new IllegalArgumentException("Difficulty must not be null.");
        }
        difficulty = selectedDifficulty;
    }


    @Override
    public boolean isBot()
    {
        return true;
    }


    @Override
    public String getTitle()
    {
        return "Bot (" + difficulty + ")";
    }


    public BotDifficulty getDifficulty()
    {
        return difficulty;
    }


    /** Chooses one move from the legal moves offered by the game. */
    public Move chooseMove(GameClass game)
    {
        if (game == null)
        {
            throw new IllegalArgumentException("Game must not be null.");
        }
        LinkedChain<Move> moves = game.getLegalMoves(this);
        Move chosen = Move.draw();
        int bestScore = -1;
        for (int i = 0; i < moves.size(); i++)
        {
            Move candidate = moves.get(i);
            if (candidate.isDraw())
            {
                // Keep the draw move only when no playable card exists.
            }
            else if (difficulty == BotDifficulty.SIMPLE)
            {
                return candidate;
            }
            else
            {
                int score = scoreMove(candidate);
                if (score > bestScore)
                {
                    bestScore = score;
                    chosen = candidate;
                }
            }
        }
        return chosen;
    }


    @Override
    public boolean playCard(GameClass game)
    {
        if (game == null)
        {
            throw new IllegalArgumentException("Game must not be null.");
        }
        Move move = chooseMove(game);
        if (move.isDraw())
        {
            System.out.println(getTitle() + " draws a card.");
            return game.applyMove(this, move);
        }
        System.out.println(getTitle() + " plays: " + move.getCard());
        if (move.getChosenColor() != null)
        {
            System.out.println(
                getTitle() + " chooses " + move.getChosenColor() + ".");
        }
        return game.applyMove(this, move);
    }


    private int scoreMove(Move move)
    {
        Card card = move.getCard();
        int score = 1;
        if (card.isSpecialCard())
        {
            String type = card.getCardType();
            if (type.equals("+2"))
                score += 4;
            if (type.equals("skip"))
                score += 3;
            if (type.equals("reverse"))
                score += 2;
            if (type.equals("+4"))
                score += 5;
            if (type.equals("wild"))
                score += 3;
        }
        if (difficulty == BotDifficulty.MASTER)
        {
            String targetColor = move.getChosenColor();
            if (targetColor == null)
            {
                targetColor = card.getCardColor();
            }
            for (int i = 0; i < cardHand.size(); i++)
            {
                Card held = cardHand.get(i);
                if (held != card && targetColor.equals(held.getCardColor()))
                {
                    score += 2;
                }
            }
            if (cardHand.size() == 1)
            {
                score += 6;
            }
        }
        return score;
    }


    /** Selects the color most common in the bot's hand. */
    @Override
    public String chooseColor()
    {
        String[] colors = { "Red", "Blue", "Green", "Yellow" };
        int[] counts = new int[colors.length];
        for (int i = 0; i < cardHand.size(); i++)
        {
            String color = cardHand.get(i).getCardColor();
            for (int j = 0; j < colors.length; j++)
            {
                if (colors[j].equals(color))
                {
                    counts[j]++;
                }
            }
        }
        int best = 0;
        for (int i = 1; i < counts.length; i++)
        {
            if (counts[i] > counts[best])
            {
                best = i;
            }
        }
        return colors[best];
    }
}
