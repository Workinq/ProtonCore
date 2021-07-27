package kr.kieran.protonprisons.enchants;

import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractEnchant extends Enchantment
{

    // MISC
    public static final BaseBlock AIR = new BaseBlock(BlockTypes.AIR.getDefaultState());

    private final Enchant enchant;
    public Enchant getEnchant() { return enchant; }

    private final String name;
    @Override public String getName() { return this.name; }

    public AbstractEnchant(Enchant enchant)
    {
        super(enchant.getId());
        this.enchant = enchant;
        this.name = enchant.name();
    }

    // Override
    @Override public EnchantmentTarget getItemTarget() { return EnchantmentTarget.TOOL; }
    @Override public int getStartLevel() { return 1; }
    @Override public boolean canEnchantItem(ItemStack item) { return item != null && item.getType().name().endsWith("_PICKAXE"); }
    @Override public boolean conflictsWith(Enchantment other) { return false; }

}
