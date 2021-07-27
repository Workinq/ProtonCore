/*
 * Copyright (c) 2021. Made by Kieraaaan for the plugin ProtonCore
 */

package kr.kieran.protonprisons.enchants;

public interface ProtonEnchant
{

    // NAME & LORE
    String getEnchantName();
    String getLoreName();

    // LEVEL
    int getMaxLevel();

    // COST
    double getBaseCost();
    double getCostMultiplier();

}
