package me.sobki.animatedleaves;

import org.bukkit.plugin.java.JavaPlugin;

public class AnimatedLeaves extends JavaPlugin {

	public static AnimatedLeaves plugin;

	private AnimationHandler aHandler;

	@Override
	public void onEnable() {
		plugin = this;
		saveDefaultConfig();
		aHandler = new AnimationHandler(this);
	}

	@Override
	public void onDisable() {
		aHandler.getPlayers().clear();
	}

}
