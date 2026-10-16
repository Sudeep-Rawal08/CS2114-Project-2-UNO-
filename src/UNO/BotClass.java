package UNO;

/** A player whose legal action is chosen by a local strategy. */
public class BotClass
    extends PlayerClass
{
    private final BotDifficulty difficulty;

    /** Creates the original first-legal-move bot. */
    public BotClass()
    {
        this(BotDifficulty.SIMPLE);
    }


    /**
     * @param difficulty
     *            the local decision strategy to use
     */
    public BotClass(BotDifficulty difficulty)
    {
        if (difficulty == null)
        {
            throw new IllegalArgumentException("Bot difficulty is required");
        }
        this.difficulty = difficulty;
    }


    /** @return the selected local difficulty */
    public BotDifficulty getDifficulty()
    {
        return difficulty;
    }


    /**
     * Selects a legal action without changing cards or game state.
     * 
     * @param game
     *            this bot's active game
     * @return a play, a draw, or null if the hand is already empty
     */
    public Move chooseMove(GameClass game)
    {
        requireTurn(game);
        LinkedChain<Move> legal = game.getLegalMoves();
        Move[] candidates = legal.toArray(new Move[legal.size()]);
        if (candidates.length == 0)
        {
            return null;
        }
        if (difficulty == BotDifficulty.SIMPLE)
        {
            return candidates[0];
        }
        Card[] hand = getCardList().toArray(new Card[getCardList().size()]);
        Move best = null;
        int bestScore = Integer.MIN_VALUE;
        for (Move move : candidates)
        {
            if (!move.isDraw())
            {
                int score =
                    score(move, hand, game.nextPlayer().getCardList().size());
                if (score > bestScore)
                {
                    best = move;
                    bestScore = score;
                }
            }
        }
        return best == null ? candidates[candidates.length - 1] : best;
    }


    @Override
    public boolean playCard(GameClass game)
    {
        Move selected = chooseMove(game);
        if (selected == null)
        {
            return false;
        }
        if (selected.isDraw())
        {
            Card drawn = game.applyMove(selected);
            game.output.println("Bot (" + difficulty + ") draws one card.");
            if (!game.isValid(drawn))
            {
                game.output.println("Bot passes.");
                return false;
            }
            selected = moveForDrawnCard(drawn, game);
        }
        Card played = game.applyMove(selected);
        game.output.println("Bot (" + difficulty + ") plays: " + played);
        return true;
    }


    private Move moveForDrawnCard(Card drawn, GameClass game)
    {
        if (!(drawn instanceof SpecialCards) || !((SpecialCards)drawn).isWild())
        {
            return Move.play(drawn, null);
        }
        Move best = Move.play(drawn, "Red");
        if (difficulty != BotDifficulty.SIMPLE)
        {
            Card[] hand = getCardList().toArray(new Card[getCardList().size()]);
            int bestScore = Integer.MIN_VALUE;
            for (String color : Card.regularColors)
            {
                Move candidate = Move.play(drawn, color);
                int score = score(
                    candidate,
                    hand,
                    game.nextPlayer().getCardList().size());
                if (score > bestScore)
                {
                    best = candidate;
                    bestScore = score;
                }
            }
        }
        return best;
    }


    private int score(Move move, Card[] hand, int opponentCards)
    {
        Card selected = move.getCard();
        boolean wild = selected instanceof SpecialCards
            && ((SpecialCards)selected).isWild();
        String color = wild ? move.getChosenColor() : selected.getCardColor();
        int matching = 0;
        for (Card card : hand)
        {
            if (card != selected && color.equals(card.getCardColor())
                && !(card instanceof SpecialCards
                    && ((SpecialCards)card).isWild()))
            {
                matching++;
            }
        }
        if (difficulty == BotDifficulty.NORMAL)
        {
            return matching * 10 - (wild ? 1000 : 0);
        }
        if (hand.length == 1)
        {
            return 1000000;
        }
        int result = matching * 10 - (wild ? 20 : 0);
        if (opponentCards <= 2 && selected instanceof SpecialCards)
        {
            String type = ((SpecialCards)selected).getCardType();
            if ("skip".equals(type))
            {
                result += 1000;
            }
            else if ("+2".equals(type))
            {
                result += 2000;
            }
            else if ("+4".equals(type))
            {
                result += 4000;
            }
        }
        return result;
    }


    private void requireTurn(GameClass game)
    {
        if (game == null || game.currentPlayer() != this)
        {
            throw new IllegalArgumentException("This must be the bot's turn");
        }
    }
}
