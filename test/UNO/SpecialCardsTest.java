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

    
    public void testDirectDrawEffectsDoNotPartiallyConsumeDeck()
    {
        for (String type : new String[] {"+2", "+4"})
        {
            GameClass game = new GameClass();
            game.deck.clear();
            game.deck.addToEnd(new NumberedCards("Blue", 1));
            int before = game.players[1].getCardList().size();
            String color = game.currentColor;
            SpecialCards card = new SpecialCards(type.equals("+4") ? null : "Red", type);
            try
            {
                if (type.equals("+4")) card.addFour(game, "Green");
                else card.addTwo(game);
                fail("Expected insufficient penalty deck");
            }
            catch (IllegalStateException expected)
            {
                assertEquals(1, game.deck.size());
                assertEquals(before, game.players[1].getCardList().size());
                assertEquals(color, game.currentColor);
                assertFalse(game.skipNext);
            }
        }
    }

    public void testSpecialEffectDispatchAndNullGameRejection()
    {
        for (String type : new String[] {"skip", "reverse", "+2", "wild", "+4"})
        {
            GameClass game = new GameClass();
            boolean wild = type.equals("wild") || type.equals("+4");
            SpecialCards card = new SpecialCards(wild ? null : "Red", type);
            assertTrue(card.isSpecialCard());
            assertFalse(card.isNumberedCard());
            assertEquals(wild, card.isWildCard());
            assertEquals(-1, card.getCardNumber());
            int before = game.players[1].getCardList().size();
            card.playSpecialEffect(game, wild ? "Blue" : null);
            int count = type.equals("+2") ? 2 : type.equals("+4") ? 4 : 0;
            assertEquals(before + count, game.players[1].getCardList().size());
            assertEquals(!type.equals("wild"), game.skipNext);
            if (wild) assertEquals("Blue", game.currentColor);
            if (type.equals("reverse")) assertEquals(-1, game.currentDirection);
            org.junit.Assert.assertThrows(IllegalArgumentException.class,
                () -> card.playSpecialEffect(null, wild ? "Blue" : null));
            org.junit.Assert.assertThrows(IllegalArgumentException.class,
                () -> card.playSpecialEffect(game, wild ? "Purple" : "Blue"));
        }
    }

    public void testWildColorSetterBoundaryAndInvalidConstruction()
    {
        SpecialCards wild = new SpecialCards(null, "wild");
        wild.setCardColor(null);
        assertNull(wild.getCardColor());
        for (String invalid : new String[] {"Red", "Wild", "Purple", ""})
        {
            org.junit.Assert.assertThrows(IllegalArgumentException.class,
                () -> wild.setCardColor(invalid));
            assertNull(wild.getCardColor());
        }
        org.junit.Assert.assertThrows(IllegalArgumentException.class,
            () -> new SpecialCards("Red", null));
        org.junit.Assert.assertThrows(IllegalArgumentException.class,
            () -> new SpecialCards(null, "skip"));
        GameClass game = new GameClass();
        org.junit.Assert.assertThrows(IllegalArgumentException.class,
            () -> wild.addFour(null, "Red"));
    }

}
