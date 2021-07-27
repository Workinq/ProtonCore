package kr.kieran.protonprisons.commands.mines;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import kr.kieran.protonprisons.ProtonPrisonsPlugin;
import kr.kieran.protonprisons.commands.AbstractCommand;
import kr.kieran.protonprisons.commands.CommandRequirements;
import kr.kieran.protonprisons.objects.ProtonMine;
import kr.kieran.protonprisons.utilities.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MinesCreateCmd extends AbstractCommand
{

    public MinesCreateCmd()
    {
        // Aliases
        this.addAliases("create");

        // Requirements
        this.setCommandRequirements(new CommandRequirements.Builder("protonprisons.commands.mines.create").playerOnly(true).build());
    }

    @Override
    public void perform(ProtonPrisonsPlugin plugin, CommandSender sender, List<String> args)
    {
        // Args
        Player player = (Player) sender;

        // Args check
        if (args.size() != 1)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.usages.mines.create")));
            return;
        }

        // Check if a mine with the same name already exists
        ProtonMine mine = plugin.getMineManager().getMineByName(args.get(0));
        if (mine != null)
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.mines.already-exists")));
            return;
        }

        // Check if the player has a region selected
        WorldEditPlugin worldEdit = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");
        Selection selection = worldEdit.getSelection(player);
        if (!(selection instanceof CuboidSelection))
        {
            player.sendMessage(Color.color(plugin.getConfig().getString("messages.mines.invalid-selection")));
            return;
        }

        // Get the minimum & maximum points of the selection
        World world = selection.getWorld();
        if (world == null) return;
        Location minimumPoint = selection.getMinimumPoint();
        Location maximumPoint = selection.getMaximumPoint();

        // Create the mine
        plugin.getMineManager().createMine(args.get(0), world.getName(), minimumPoint.getBlockX(), maximumPoint.getBlockX(), minimumPoint.getBlockY(), maximumPoint.getBlockY(), minimumPoint.getBlockZ(), maximumPoint.getBlockZ(), newMine -> {
            if (newMine == null)
            {
                sender.sendMessage(Color.color(plugin.getConfig().getString("messages.mines.error")));
                return;
            }
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.mines.created").replace("%mine%", newMine.getName()).replace("%id%", String.valueOf(newMine.getId()))));
        });
    }

}
