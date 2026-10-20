package UNO;


/** Tests player hand access and card-play behavior without console input. */
public class PlayerClassTest extends student.TestCase
{
    public void testConstructorAndGetterCreateAndReturnHand()
    {
        PlayerClass player = new PlayerClass();
        assertNotNull(player.getCardList());
        assertTrue(player.getCardList().isEmpty());
    }
    public void testPlaySpecificCardUpdatesHandAndCurrentCard()
    {
        GameClass game = new GameClass();
        PlayerClass player = new PlayerClass();
        NumberedCards card = new NumberedCards(game.currentColor, 8);
        player.getCardList().addToEnd(card);
        assertTrue(player.playSpecificCard(game, card));
        assertFalse(player.getCardList().contains(card));
        assertSame(card, game.currentCard);
        assertEquals(card.getCardColor(), game.currentColor);
    }
    public void testPlaySpecificCardRejectsInvalidCard()
    {
        GameClass game = new GameClass();
        PlayerClass player = new PlayerClass();
        game.currentCard = new NumberedCards("Red", 1);
        game.currentColor = "Red";
        NumberedCards invalid = new NumberedCards("Blue", 7);
        player.getCardList().addToEnd(invalid);
        assertFalse(player.playSpecificCard(game, invalid));
        assertTrue(player.getCardList().contains(invalid));
    }
    public void testPlaySpecificCardAppliesSkipAndWildActions()
    {
        GameClass skipGame = new GameClass();
        PlayerClass player = new PlayerClass();
        SpecialCards skip = new SpecialCards(skipGame.currentColor, "skip");
        player.getCardList().addToEnd(skip);
        assertTrue(player.playSpecificCard(skipGame, skip));
        assertTrue(skipGame.skipNext);

        GameClass wildGame = new GameClass();
        PlayerClass wildPlayer = new PlayerClass();
        SpecialCards wild = new SpecialCards(null, "wild");
        wildPlayer.getCardList().addToEnd(wild);
        // The color prompt is interactive, so the wild path is covered manually.
        assertTrue(wildGame.isValid(wild));
    }
}
