package me.sobki.animatedleaves.command;

import org.bukkit.ChatColor;

public class CommandMessage {

	public static final CommandMessage PREFIX = new CommandMessage("&2[&fAL&2] &r");

	public static final CommandMessage MUST_BE_PLAYER = new CommandMessage("&cOnly players can perform this action.");
	public static final CommandMessage INSUFFICIENT_PERMISSION = new CommandMessage("&cYou do not have permission to perform this action.");
	public static final CommandMessage PLAYER_NOT_FOUND = new CommandMessage("&cPlayer &4%player% &cnot found.");

	public static final CommandMessage TOGGLED = new CommandMessage("&7Animated leaves toggled %enabled%&7.");
	public static final CommandMessage TOGGLED_OTHER = new CommandMessage("&7Animated leaves toggled %enabled% &7by &f%player%&7.");
	public static final CommandMessage TOGGLED_OTHER_SENDER = new CommandMessage("&7Animated leaves toggled %enabled% &7for &f%player%&7.");
	public static final CommandMessage RELOADED = new CommandMessage("&7Config reloaded.");

	private String message;
	private String temp;

	public CommandMessage(String message) {
		this.message = message;
		this.temp = message;
	}

	public CommandMessage replace(CharSequence target, CharSequence replacement) {
		this.temp = this.temp.replace(target, replacement);
		return this;
	}

	public String format() {
		String oldtemp = this.temp;
		this.temp = this.message;
		return ChatColor.translateAlternateColorCodes('&', oldtemp);
	}

}
