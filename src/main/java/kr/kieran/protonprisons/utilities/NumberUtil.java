package kr.kieran.protonprisons.utilities;

public class NumberUtil
{

    public static boolean isInt(String text)
    {
        try
        {
            Integer.parseInt(text);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

}
