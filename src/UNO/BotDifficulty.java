package UNO;

/** The three bot strategy levels. */
public enum BotDifficulty
{
    SIMPLE,
    NORMAL,
    MASTER;

    @Override
    public String toString()
    {
        String name = name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
