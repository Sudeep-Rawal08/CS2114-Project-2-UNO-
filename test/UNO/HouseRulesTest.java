package UNO;

/** Tests the stored switches only; house rules are not used by the game. */
public class HouseRulesTest extends student.TestCase
{
    public void testDefaultsAreDisabledAndInstancesAreIndependent()
    {
        HouseRules first = new HouseRules();
        HouseRules second = new HouseRules();
        assertFalse(first.stackingPlus);
        assertFalse(first.sevenSwap);
        assertFalse(first.placingMultiple);
        assertFalse(first.drawTillMatch);
        first.toggleStacking();
        assertFalse(second.stackingPlus);
    }

    public void testEveryToggleEnablesThenDisablesItsSwitch()
    {
        HouseRules rules = new HouseRules();
        rules.toggleStacking();
        rules.toggleSevenSwap();
        rules.togglePlacingMultiple();
        assertTrue(rules.stackingPlus);
        assertTrue(rules.sevenSwap);
        assertTrue(rules.placingMultiple);
        rules.toggleStacking();
        rules.toggleSevenSwap();
        rules.togglePlacingMultiple();
        assertFalse(rules.stackingPlus);
        assertFalse(rules.sevenSwap);
        assertFalse(rules.placingMultiple);
        assertFalse(rules.drawTillMatch);
    }
}
