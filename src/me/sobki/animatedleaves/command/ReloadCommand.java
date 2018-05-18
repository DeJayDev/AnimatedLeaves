package me.sobki.animatedleaves.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.sobki.animatedleaves.AnimatedLeaves;

public class ReloadCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("alreload")) {
			if (!sender.hasPermission("animatedleaves.reload")) {
				sender.sendMessage(CommandMessage.PREFIX.format() + CommandMessage.INSUFFICIENT_PERMISSION.format());
				return true;
			}
			AnimatedLeaves.plugin.reload();
			sender.sendMessage(CommandMessage.PREFIX.format() + CommandMessage.RELOADED.format());
		}
		return true;
	}

}
