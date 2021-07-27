/*
 * Copyright (c) 2021. Made by Kieraaaan for the plugin ProtonCore
 */

package kr.kieran.protonprisons.utilities;

import java.util.concurrent.ThreadLocalRandom;

public class BlockUtil
{

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public int getDropCount(int level)
    {
        int amount = RANDOM.nextInt(level + 2) - 1;
        if (amount < 0) amount = 0;
        return (amount + 1);
    }

}
