/*
 * Copyright (c) 2021. Made by Kieraaaan for the plugin ProtonCore
 */

package kr.kieran.protonprisons.managers;

import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.enchants.AbstractEnchant;
import kr.kieran.protonprisons.enchants.CustomEnchant;
import kr.kieran.protonprisons.enchants.Enchant;
import kr.kieran.protonprisons.enchants.PotionEnchant;
import kr.kieran.protonprisons.enchants.enchants.DragonsFistEnchant;
import kr.kieran.protonprisons.enchants.enchants.EfficiencyEnchant;
import kr.kieran.protonprisons.enchants.enchants.FortuneEnchant;
import kr.kieran.protonprisons.enchants.enchants.GemFinderEnchant;
import kr.kieran.protonprisons.enchants.enchants.HasteEnchant;
import kr.kieran.protonprisons.enchants.enchants.JackhammerEnchant;
import kr.kieran.protonprisons.enchants.enchants.KeyFinderEnchant;
import kr.kieran.protonprisons.enchants.enchants.LaserEnchant;
import kr.kieran.protonprisons.enchants.enchants.MerchantEnchant;
import kr.kieran.protonprisons.enchants.enchants.NukeEnchant;
import kr.kieran.protonprisons.enchants.enchants.RelocationsPaypalEnchant;
import kr.kieran.protonprisons.enchants.enchants.SafesEnchant;
import kr.kieran.protonprisons.enchants.enchants.ScavengerEnchant;
import kr.kieran.protonprisons.enchants.enchants.SeasonFinderEnchant;
import kr.kieran.protonprisons.enchants.enchants.SpeedEnchant;
import kr.kieran.protonprisons.enchants.enchants.TokenPouchEnchant;
import kr.kieran.protonprisons.enchants.enchants.TokenatorEnchant;
import kr.kieran.protonprisons.enchants.enchants.ValueFinderEnchant;
import kr.kieran.protonprisons.objects.ProtonMine;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class EnchantManager
{

    private final ProtonPrisonsPlugin plugin;
    private final Set<AbstractEnchant> enchants = new HashSet<>();

    // Random
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public EnchantManager(ProtonPrisonsPlugin plugin)
    {
        this.plugin = plugin;
        this.registerEnchants();
    }

    private void registerEnchants()
    {
        // Register enchants
        this.enchants.addAll(
                Arrays.asList(
                        new DragonsFistEnchant(plugin), new EfficiencyEnchant(plugin), new FortuneEnchant(plugin),
                        new GemFinderEnchant(plugin), new HasteEnchant(plugin), new JackhammerEnchant(plugin),
                        new KeyFinderEnchant(plugin), new LaserEnchant(plugin), new MerchantEnchant(plugin),
                        new NukeEnchant(plugin), new RelocationsPaypalEnchant(plugin), new SafesEnchant(plugin),
                        new ScavengerEnchant(plugin), new SeasonFinderEnchant(plugin), new SpeedEnchant(plugin),
                        new TokenatorEnchant(plugin), new TokenPouchEnchant(plugin), new ValueFinderEnchant(plugin)
                )
        );

        // Set the acceptingNew field to true or else we can't register enchants
        try
        {
            Field field = Enchantment.class.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.set(null, true);
        }
        catch (Exception e)
        {
            plugin.getLogger().log(Level.SEVERE, "Failed to modify Enchantment#acceptingNew to register enchants");
        }

        // Loop over all enchants and register them using Enchantment#registerEnchantment
        final Set<Enchant> blacklistedEnchants = EnumSet.of(Enchant.EFFICIENCY, Enchant.FORTUNE);
        this.enchants.stream().filter(enchant -> !blacklistedEnchants.contains(enchant.getEnchant())).forEach(Enchantment::registerEnchantment);
    }

    public AbstractEnchant getEnchantByType(Enchant enchant)
    {
        Optional<AbstractEnchant> optional = enchants.stream().filter(abstractEnchant -> abstractEnchant.getEnchant() == enchant).findFirst();
        return optional.orElse(null);
    }

    public void executeCustomEnchant(Map<AbstractEnchant, Integer> enchantLevels, Player player, ProtonMine protonMine, Location location)
    {
        this.enchants.stream()
                // Check if the enchantLevels map contains the enchant
                .filter(enchantLevels::containsKey)
                // Check if the enchant is a custom enchant enchant
                .filter(enchant -> CustomEnchant.class.isAssignableFrom(enchant.getClass()))
                // Map all enchants to CustomEnchant since we know we can after the previous check
                .map(enchant -> (CustomEnchant) enchant)
                // Use random to check whether or not to execute the enchant
                .filter(enchant -> RANDOM.nextDouble() < enchant.getBaseValue() + (enchantLevels.get(enchant) * enchant.getMultiplier()))
                // For each enchant which remains execute it
                .forEach(enchant -> enchant.perform(player, protonMine, location, enchantLevels.get(enchant)));
    }

    public void executePotionEnchant(Map<AbstractEnchant, Integer> enchantLevels, Player player)
    {
        this.enchants.stream()
                // Check if the enchantLevels map contains the enchant
                .filter(enchantLevels::containsKey)
                // Check if the enchant is a potion enchant enchant
                .filter(enchant -> PotionEnchant.class.isAssignableFrom(enchant.getClass()))
                // Map all enchants to PotionEnchant since we know we can after the previous check
                .map(enchant -> (PotionEnchant) enchant)
                // For each enchant which remains apply it
                .forEach(enchant -> enchant.apply(player, enchantLevels.get(enchant)));
    }

    public void disable() { this.enchants.clear(); }

}
