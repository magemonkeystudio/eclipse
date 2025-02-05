package studio.magemonkey.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.magemonkey.Eclipse;
import studio.magemonkey.service.MountService;

public class Command implements CommandExecutor {
    private final Eclipse plugin;
    private final MountService mountService;

    public Command(Eclipse plugin) {
        this.plugin = plugin;
        this.mountService = new MountService(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /eclipse <reload|mount save>");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.getConfigManager().loadConfigs();
            player.sendMessage(ChatColor.GREEN + "Configuration reloaded.");
        } else if (args[0].equalsIgnoreCase("mount") && args.length >= 2 && args[1].equalsIgnoreCase("save")) {
            mountService.saveMountFromPlayer(player);
        } else {
            player.sendMessage(ChatColor.RED + "Invalid command usage.");
        }

        return true;
    }
}