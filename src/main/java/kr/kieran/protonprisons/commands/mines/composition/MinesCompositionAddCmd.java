package kr.kieran.protonprisons.commands.mines.composition;

import com.sk89q.worldedit.world.block.BlockTypes;
import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.commands.CommandRequirements;
import kr.kieran.protonprisons.objects.BlockChance;
import kr.kieran.protonprisons.objects.ProtonMine;
import kr.kieran.protonprisons.utilities.Color;
import kr.kieran.protonprisons.utilities.NumberUtil;
import kr.kieran.protonprisons.utilities.SerializationUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MinesCompositionAddCmd extends MinesCompositionCommand
{

    public MinesCompositionAddCmd()
    {
        // Aliases
        this.addAliases("add", "a");

        // Requirements
        this.setCommandRequirements(new CommandRequirements.Builder("protonprisons.commands.mines.composition.add").playerOnly(false).build());
    }

    @Override
    public void perform(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args)
    {
        // Args check
        if (args.size() != 3)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.usages.mines.composition.add")));
            return;
        }

        // Check if a mine doesn't exist with the name or id
        ProtonMine mine = this.getMineFromText(plugin, args.get(0));
        if (mine == null)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.mines.doesnt-exist")));
            return;
        }

        // Get the material and durability
        String materialData = args.get(1);
        if (!materialData.contains(":"))
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.mines.invalid-material")));
            return;
        }
        ItemStack item = SerializationUtil.deserializeItem(materialData);

        // Get the percent & total percent of the mine composition
        double totalPercent = mine.getBlockChances().stream().mapToDouble(BlockChance::getChance).sum();
        double percent;
        try
        {
            percent = Double.parseDouble(args.get(2));
        }
        catch (NumberFormatException e)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.not-a-number").replace("%number%", args.get(2))));
            return;
        }

        // Check if the total percent exceeds 100%
        if (totalPercent + percent > 100.0d)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.mines.max-percent").replace("%percent%", String.format("%,.1f", 100.0d - totalPercent))));
            return;
        }

        // Add the block chance to the mine
        // TODO: Add data to the block
        BlockChance blockChance = new BlockChance(BlockTypes.get(item.getType().name()).getDefaultState(), percent);
        mine.addBlockChance(blockChance);

        // Update the mine composition in the database
        plugin.getMineManager().setMineComposition(mine, updatedMine -> {
            if (updatedMine == null)
            {
                sender.sendMessage(Color.color(plugin.getConfig().getString("messages.mines.error")));
                return;
            }
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.mines.composition-updated").replace("%mine%", updatedMine.getName())));
        });
    }

    private ProtonMine getMineFromText(ProtonPrisonsPlugin plugin, String text)
    {
        return NumberUtil.isInt(text) ? plugin.getMineManager().getMineById(Integer.parseInt(text)) : plugin.getMineManager().getMineByName(text);
    }

}
