package me.sobki.animatedleaves.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sobki.animatedleaves.ALPlayer;
import me.sobki.animatedleaves.AnimatedLeaves;

public class ToggleCommand implements CommandExecutor {

	/**
	 * If necessary this class will be replaced by a subcommand management system; however, where we only have two commands (toggle, reload), two classes implementing CommandExecutor will do.
	 */

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("altoggle")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(CommandMessage.PREFIX.format() + CommandMessage.MUST_BE_PLAYER.format());
				return true;
			}
			Player player = (Player) sender;
			if (!player.hasPermission("animatedleaves.toggle")) {
				sender.sendMessage(CommandMessage.PREFIX.format() + CommandMessage.INSUFFICIENT_PERMISSION.format());
				return true;
			}
			if (args.length < 1) {
				toggle(player, player);
			} else {
				if (args[0].equalsIgnoreCase("all")) {
					if (!player.hasPermission("animatedleaves.toggle.all")) {
						sender.sendMessage(CommandMessage.PREFIX.format() + CommandMessage.INSUFFICIENT_PERMISSION.format());
						return true;
					}
					boolean enabledAll = !AnimatedLeaves.plugin.getAnimationHandler().isEnabledAll();
					AnimatedLeaves.plugin.getAnimationHandler().setEnabledAll(enabledAll);
					String toggle = enabledAll ? "&a&lON" : "&c&lOFF";
					Bukkit.broadcastMessage(CommandMessage.PREFIX.format() + CommandMessage.TOGGLED_ALL.replace("%enabled%", toggle).replace("%player%", player.getName()).format());
					return true;
				}
				if (!player.hasPermission("animatedleaves.toggle.other")) {
					sender.sendMessage(CommandMessage.PREFIX.format() + CommandMessage.INSUFFICIENT_PERMISSION.format());
					return true;
				}
				Player target = AnimatedLeaves.plugin.getServer().getPlayer(args[0]);
				if (target == null) {
					sender.sendMessage(CommandMessage.PREFIX.format() + CommandMessage.PLAYER_NOT_FOUND.replace("%player%", args[0]).format());
					return true;
				}
				toggle(player, target);
			}
		}
		return true;
	}

	public void toggle(Player sender, Player target) {
		ALPlayer alPlayer = AnimatedLeaves.plugin.getAnimationHandler().getPlayers().get(target);
		alPlayer.setEnabled(!alPlayer.isEnabled());
		boolean enabled = alPlayer.isEnabled();
		String toggle = enabled ? "&a&lON" : "&c&lOFF";
		if (sender.getUniqueId().equals(target.getUniqueId())) {
			target.sendMessage(CommandMessage.PREFIX.format() + CommandMessage.TOGGLED.replace("%enabled%", toggle).format());
		} else {
			target.sendMessage(CommandMessage.PREFIX.format() + CommandMessage.TOGGLED_OTHER.replace("%enabled%", toggle).replace("%player%", sender.getName()).format());
			sender.sendMessage(CommandMessage.PREFIX.format() + CommandMessage.TOGGLED_OTHER_SENDER.replace("%enabled%", toggle).replace("%player%", target.getName()).format());
		}
	}
}
