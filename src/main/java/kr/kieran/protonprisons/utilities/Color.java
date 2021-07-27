package kr.kieran.protonprisons.utilities;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Color
{

    public static String color(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> color(List<String> uncolored)
    {
        List<String> colored = new ArrayList<>();
        for (String string : uncolored)
        {
            colored.add(color(string));
        }
        return colored;
    }

    public static String strip(String colored)
    {
        return ChatColor.stripColor(colored);
    }

    public static String getProgressBar(double current, double max, int totalBars, String symbol, String completedColor, String notCompletedColor)
    {
        double percent = current / max;
        int progressBars = (int) (totalBars * percent);
        int leftOver = (totalBars - progressBars);
        StringBuilder builder = new StringBuilder();
        builder.append(color(completedColor));
        for (int i = 0; i < progressBars; i++)
        {
            builder.append(symbol);
        }
        builder.append(color(notCompletedColor));
        for (int i = 0; i < leftOver; i++)
        {
            builder.append(symbol);
        }
        return builder.toString();
    }

}
