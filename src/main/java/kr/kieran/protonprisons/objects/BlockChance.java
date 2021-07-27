/*
 * Copyright (c) 2021. Made by Kieraaaan for the plugin ProtonCore
 */

package kr.kieran.protonprisons.objects;

import com.sk89q.worldedit.world.block.BlockState;

public class BlockChance
{

    private final BlockState block;
    public BlockState getBlock() { return this.block; }

    private final double chance;
    public double getChance() { return this.chance; }

    public BlockChance(final BlockState block, final double chance)
    {
        this.block = block;
        this.chance = chance;
    }

}
