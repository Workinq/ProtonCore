package kr.kieran.protonprisons.commands.mines.composition;

import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
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

public class MinesCompositionDeleteCmd extends MinesCompositionCommand
{

    public MinesCompositionDeleteCmd()
    {
        // Aliases
        this.addAliases("delete", "del", "d");

        // Requirements
        this.setCommandRequirements(new CommandRequirements.Builder("protonprisons.commands.mines.composition.delete").playerOnly(false).build());
    }

    @Override
    public void perform(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args)
    {
        // Args check
        if (args.size() != 2)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.usages.mines.composition.delete")));
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

        // Remove the block chance from the mine
        BlockType blockType = BlockTypes.get(item.getType().name());
        BlockChance block = this.getBlockChanceById(mine, blockType);
        if (block == null)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.mines.unknown-composition")));
            return;
        }
        mine.removeBlockChance(block);

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

    private BlockChance getBlockChanceById(ProtonMine mine, BlockType blockType)
    {
        for (BlockChance blockChance : mine.getBlockChances())
        {
            BlockState baseBlock = blockChance.getBlock();
            if (baseBlock.equals(blockType.getDefaultState())) return blockChance;
        }
        return null;
    }

}
