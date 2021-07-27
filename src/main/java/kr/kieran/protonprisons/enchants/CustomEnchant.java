package kr.kieran.protonprisons.enchants;

import kr.kieran.protonprisons.objects.ProtonMine;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface CustomEnchant extends ProtonEnchant
{

    // EXECUTE
    void perform(Player player, ProtonMine mine, Location location, int level);

    // CHANCE
    double getBaseValue();
    double getMultiplier();

}
