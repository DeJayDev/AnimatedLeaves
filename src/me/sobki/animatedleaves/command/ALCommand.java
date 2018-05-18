package me.sobki.animatedleaves.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ALCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("al")) {
			if (!sender.hasPermission("animatedleaves.help")) {
				sender.sendMessage(CommandMessage.PREFIX.format() + CommandMessage.INSUFFICIENT_PERMISSION.format());
				return true;
			}
			sender.sendMessage(CommandMessage.PREFIX.format() + CommandMessage.HELP_TITLE.format());
			for (CommandMessage message : CommandMessage.HELP) {
				sender.sendMessage(message.format());
			}
		}
		return true;
	}

}
