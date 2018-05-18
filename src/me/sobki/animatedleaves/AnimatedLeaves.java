package me.sobki.animatedleaves;

import org.bukkit.plugin.java.JavaPlugin;

import me.sobki.animatedleaves.command.ReloadCommand;
import me.sobki.animatedleaves.command.ToggleCommand;

public class AnimatedLeaves extends JavaPlugin {

	public static AnimatedLeaves plugin;

	private AnimationHandler aHandler;

	@Override
	public void onEnable() {
		plugin = this;
		saveDefaultConfig();
		aHandler = new AnimationHandler(this);

		getCommand("altoggle").setExecutor(new ToggleCommand());
		getCommand("alreload").setExecutor(new ReloadCommand());
	}

	@Override
	public void onDisable() {
		aHandler.getPlayers().clear();
	}

	public void reload() {
		reloadConfig();
		aHandler.unload();
		aHandler = new AnimationHandler(this);
	}

	public AnimationHandler getAnimationHandler() {
		return aHandler;
	}

}
