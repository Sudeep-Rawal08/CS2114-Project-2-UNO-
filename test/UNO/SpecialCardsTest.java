package UNO;


/** Tests special-card construction, actions, color selection, and display. */
public class SpecialCardsTest extends student.TestCase
{
    public void testConstructorAndGetCardTypeAcceptSupportedTypes()
    {
        String[] types = {"+2", "+4", "reverse", "skip", "wild"};
        for (String type : types)
        {
            SpecialCards card = new SpecialCards(
                type.equals("wild") || type.equals("+4") ? null : "Red", type);
            assertEquals(type, card.getCardType());
        }
                {
            boolean caughtExpected = false;
            try
            {
                new SpecialCards("Red", "draw five");
            }
            catch (IllegalArgumentException e)
            {
                caughtExpected = true;
            }
            assertTrue("Expected IllegalArgumentException", caughtExpected);
        };
                {
            boolean caughtExpected = false;
            try
            {
                new SpecialCards("Purple", "skip");
            }
            catch (IllegalArgumentException e)
            {
                caughtExpected = true;
            }
            assertTrue("Expected IllegalArgumentException", caughtExpected);
        };
    }
    public void testSetCardColorAcceptsOnlyRegularColors()
    {
        SpecialCards card = new SpecialCards("Red", "skip");
        card.setCardColor("Blue");
        assertEquals("Blue", card.getCardColor());
                {
            boolean caughtExpected = false;
            try
            {
                card.setCardColor("Wild");
            }
            catch (IllegalArgumentException e)
            {
                caughtExpected = true;
            }
            assertTrue("Expected IllegalArgumentException", caughtExpected);
        };
                {
            boolean caughtExpected = false;
            try
            {
                card.setCardColor(null);
            }
            catch (IllegalArgumentException e)
            {
                caughtExpected = true;
            }
            assertTrue("Expected IllegalArgumentException", caughtExpected);
        };
    }
    public void testReverseSkipAndDrawTwoApplyEffects()
    {
        GameClass reverseGame = new GameClass();
        new SpecialCards("Red", "reverse").reverseOrder(reverseGame);
        assertEquals(-1, reverseGame.currentDirection);
        assertTrue(reverseGame.skipNext);

        GameClass skipGame = new GameClass();
        new SpecialCards("Red", "skip").skipPlayer(skipGame);
        assertTrue(skipGame.skipNext);

        GameClass drawGame = new GameClass();
        int before = drawGame.players[1].getCardList().size();
        new SpecialCards("Red", "+2").addTwo(drawGame);
        assertEquals(before + 2, drawGame.players[1].getCardList().size());
        assertTrue(drawGame.skipNext);
    }
    public void testWildAndPlusFourSetChosenColorAndPlusFourDrawsFour()
    {
        GameClass wildGame = new GameClass();
        SpecialCards wild = new SpecialCards(null, "wild");
        wild.playedWildcard(wildGame, "Green");
        assertEquals("Green", wild.getCardColor());
        assertEquals("Green", wildGame.currentColor);

        GameClass plusFourGame = new GameClass();
        int before = plusFourGame.players[1].getCardList().size();
        SpecialCards plusFour = new SpecialCards(null, "+4");
        plusFour.addFour(plusFourGame, "Yellow");
        assertEquals("Yellow", plusFourGame.currentColor);
        assertEquals("Yellow", plusFour.getCardColor());
        assertEquals(before + 4, plusFourGame.players[1].getCardList().size());
    }
    public void testActionsRejectNullGameAndInvalidChosenColor()
    {
        SpecialCards wild = new SpecialCards(null, "wild");
                {
            boolean caughtExpected = false;
            try
            {
                wild.playedWildcard(null, "Red");
            }
            catch (IllegalArgumentException e)
            {
                caughtExpected = true;
            }
            assertTrue("Expected IllegalArgumentException", caughtExpected);
        };
                {
            boolean caughtExpected = false;
            try
            {
                wild.playedWildcard(new GameClass(), "Wild");
            }
            catch (IllegalArgumentException e)
            {
                caughtExpected = true;
            }
            assertTrue("Expected IllegalArgumentException", caughtExpected);
        };
                {
            boolean caughtExpected = false;
            try
            {
                wild.addFour(new GameClass(), null);
            }
            catch (IllegalArgumentException e)
            {
                caughtExpected = true;
            }
            assertTrue("Expected IllegalArgumentException", caughtExpected);
        };
                {
            boolean caughtExpected = false;
            try
            {
                wild.reverseOrder(null);
            }
            catch (IllegalArgumentException e)
            {
                caughtExpected = true;
            }
            assertTrue("Expected IllegalArgumentException", caughtExpected);
        };
                {
            boolean caughtExpected = false;
            try
            {
                wild.skipPlayer(null);
            }
            catch (IllegalArgumentException e)
            {
                caughtExpected = true;
            }
            assertTrue("Expected IllegalArgumentException", caughtExpected);
        };
                {
            boolean caughtExpected = false;
            try
            {
                wild.addTwo(null);
            }
            catch (IllegalArgumentException e)
            {
                caughtExpected = true;
            }
            assertTrue("Expected IllegalArgumentException", caughtExpected);
        };
    }
    public void testToStringShowsOnlyWildOrPlusFourForWildCards()
    {
        assertEquals("Wild", new SpecialCards(null, "wild").toString());
        assertEquals("+4", new SpecialCards(null, "+4").toString());
        assertEquals("Red skip", new SpecialCards("Red", "skip").toString());
    }

    
}
