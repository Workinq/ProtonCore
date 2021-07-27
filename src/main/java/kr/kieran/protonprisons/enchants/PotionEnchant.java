/*
 * Copyright (c) 2021. Made by Kieraaaan for the plugin ProtonCore
 */

package kr.kieran.protonprisons.enchants;

import org.bukkit.entity.Player;

public interface PotionEnchant extends ProtonEnchant
{

    // APPLY
    void apply(Player player, int enchantLevel);

}
