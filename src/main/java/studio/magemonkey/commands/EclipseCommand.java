package studio.magemonkey.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.magemonkey.Eclipse;

public class EclipseCommand implements CommandExecutor {

    private final Eclipse plugin;

    public EclipseCommand(@NotNull Eclipse plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /eclipse <reload|mount save>");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            player.sendMessage(ChatColor.GREEN + "Configuration reloaded.");
        } else if (args[0].equalsIgnoreCase("mount") && args.length >= 2 && args[1].equalsIgnoreCase("save")) {
            // Now instruct the player to use the mount item for spawning/despawning
            player.sendMessage(ChatColor.YELLOW + "Use your mount item to spawn or despawn your mount.");
        } else {
            player.sendMessage(ChatColor.RED + "Invalid command usage.");
        }
        return true;
    }
}
