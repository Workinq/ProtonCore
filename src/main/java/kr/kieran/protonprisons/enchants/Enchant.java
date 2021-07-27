/*
 * Copyright (c) 2021. Made by Kieraaaan for the plugin ProtonCore
 */

package kr.kieran.protonprisons.enchants;

import org.bukkit.ChatColor;

public enum Enchant
{

    DRAGONS_FIST(199, "Dragon's Fist", ChatColor.RED),
    EFFICIENCY(200, "Efficiency", "DIG_SPEED", ChatColor.AQUA),
    FORTUNE(201, "Fortune", "LOOT_BONUS_BLOCKS", ChatColor.AQUA),
    GEM_FINDER(202, "Gem Finder", ChatColor.RED),
    HASTE(203, "Haste", ChatColor.AQUA),
    JACKHAMMER(204, "Jackhammer", ChatColor.YELLOW),
    KEY_FINDER(205, "Key Finder", ChatColor.GREEN),
    LASER(206, "Laser", ChatColor.YELLOW),
    MERCHANT(207, "Merchant", ChatColor.RED),
    NUKE(208, "Nuke", ChatColor.YELLOW),
    RELOCATIONS_PAYPAL(209, "Relocation's PayPal", ChatColor.GREEN),
    SAFES(210, "Safes", ChatColor.GREEN),
    SCAVENGER(211, "Scavenger", ChatColor.GREEN),
    SEASON_FINDER(212, "Season Finder", ChatColor.GREEN),
    SPEED(213, "Speed", ChatColor.AQUA),
    TOKENATOR(214, "Tokenator", ChatColor.RED),
    TOKEN_POUCH(215, "Token Pouch", ChatColor.YELLOW),
    VALUE_FINDER(216, "Value Finder", ChatColor.RED),

    // END OF LIST
    ;

    private final int id;
    public int getId() { return id; }

    private final String name;
    public String getName() { return name; }

    private final String vanillaName;
    public String getVanillaName() { return vanillaName; }

    private final ChatColor color;
    public ChatColor getColor() { return color; }

    Enchant(int id, String name, ChatColor color) { this(id, name, null, color); }
    Enchant(int id, String name, String vanillaName, ChatColor color)
    {
        this.id = id;
        this.name = name;
        this.vanillaName = vanillaName;
        this.color = color;
    }

    public static Enchant getEnchantByName(String name)
    {
        for (Enchant enchant : values())
        {
            if (enchant.getName().equals(name)) return enchant;
        }
        return null;
    }

}
