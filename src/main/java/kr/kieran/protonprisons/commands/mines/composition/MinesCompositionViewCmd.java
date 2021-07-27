package kr.kieran.protonprisons.commands.mines.composition;

import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.patterns.BlockChance;
import com.sk89q.worldedit.world.block.BlockTypes;
import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.commands.CommandRequirements;
import kr.kieran.protonprisons.objects.ProtonMine;
import kr.kieran.protonprisons.utilities.Color;
import kr.kieran.protonprisons.utilities.NumberUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MinesCompositionViewCmd extends MinesCompositionCommand
{

    public MinesCompositionViewCmd()
    {
        // Aliases
        this.addAliases("view", "v");

        // Requirements
        this.setCommandRequirements(new CommandRequirements.Builder("protonprisons.commands.mines.composition.view").playerOnly(false).build());
    }

    @Override
    public void perform(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args)
    {
        // Args check
        if (args.size() != 1)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.usages.mines.composition.view")));
            return;
        }

        // Check if a mine doesn't exist with the name or id
        ProtonMine mine = this.getMineFromText(plugin, args.get(0));
        if (mine == null)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.mines.doesnt-exist")));
            return;
        }

        // Add all block chances to a string builder
        StringBuilder builder = new StringBuilder();
        for (BlockChance blockChance : mine.getBlockChances())
        {
            BaseBlock baseBlock = blockChance.getBlock();
            ItemStack item = new ItemStack(Material.getMaterial(baseBlock.getId()), 1, (short) baseBlock.getData());
            builder.append(Color.color(plugin.getConfig().getString("messages.mines.formats.composition").replace("%item%", "" + item.getType().name() + ":" + item.getDurability() + "").replace("%percent%", String.format("%,.1f", blockChance.getChance()))));
        }

        // Send the message
        sender.sendMessage(Color.color(plugin.getConfig().getString("messages.mines.formats.composition-view").replace("%mine%", mine.getName()).replace("%compositions%", builder.toString().trim())));
    }

    private ProtonMine getMineFromText(ProtonPrisonsPlugin plugin, String text)
    {
        return NumberUtil.isInt(text) ? plugin.getMineManager().getMineById(Integer.parseInt(text)) : plugin.getMineManager().getMineByName(text);
    }

}
