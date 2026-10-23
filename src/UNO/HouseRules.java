package UNO;

public class HouseRules
{
    public boolean stackingPlus;
    public boolean sevenSwap;
    public boolean placingMultiple;
    public boolean drawTillMatch;

    public HouseRules()
    {
        this.stackingPlus = false;
        this.sevenSwap = false;
        this.placingMultiple = false;
        this.drawTillMatch = false;
    }


    public void toggleStacking()
    {
        stackingPlus = !stackingPlus;
    }


    public void toggleSevenSwap()
    {
        sevenSwap = !sevenSwap;
    }


    public void togglePlacingMultiple()
    {
        placingMultiple = !placingMultiple;
    }


    
}
