package me.sobki.animatedleaves;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import me.sobki.animatedleaves.ParticleEffect.BlockData;

public class AnimationHandler implements Listener, Runnable {

	private final Map<Player, ALPlayer> PLAYERS = new HashMap<>();
	/*
	 * The radius around the player particles will be displayed from
	 */
	private final int PARTICLE_RADIUS;
	/*
	 * The factor used to scale the probability that a nearby leaf will display a particle
	 */
	private final float PARTICLE_SCALE;
	/*
	 * The amount of particles to be displayed
	 */
	private final int PARTICLE_AMOUNT;
	/*
	 * The amount of ticks between each particle update
	 */
	private final long PARTICLE_INTERVAL;

	private BukkitTask task;
	private boolean enabledAll;

	public AnimationHandler(AnimatedLeaves plugin) {
		this.PARTICLE_RADIUS = plugin.getConfig().getInt("Particle.Radius");
		this.PARTICLE_SCALE = (float) plugin.getConfig().getDouble("Particle.Scale");
		this.PARTICLE_AMOUNT = plugin.getConfig().getInt("Particle.Amount");
		this.PARTICLE_INTERVAL = plugin.getConfig().getLong("Particle.Interval");
		this.task = plugin.getServer().getScheduler().runTaskTimer(plugin, this, PARTICLE_INTERVAL, PARTICLE_INTERVAL);
		this.enabledAll = true;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		plugin.getServer().getOnlinePlayers().forEach(player -> {
			PLAYERS.put(player, new ALPlayer(player, PARTICLE_RADIUS));
		});
	}
	private final static String SEE_LEAVES_FALL_PERMISSION = "animatedleaves.see";
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		if (!enabledAll) {
			return;
		}
		PLAYERS.forEach((bukkitPlayer, player) -> {
			if (!player.isEnabled() || !bukkitPlayer.hasPermission(SEE_LEAVES_FALL_PERMISSION)) {
				return;
			}
			List<Block> leaves = new ArrayList<>(player.getLeaves());
			if (leaves.size() == 0) {
				return;
			}
			Collections.shuffle(leaves);
			int x = leaves.size();
			int chance = (int) Math.ceil((x * (x / (Math.sqrt(Math.pow(x, 3) + 2 * Math.pow(x, 2) + PARTICLE_SCALE / 2)))));
			for (int i = 0; i < chance; i++) {
				int index = (int) (Math.random() * (leaves.size() - 1));
				Block leaf = leaves.get(index);
				Location centre = leaf.getLocation().add(0.5, 0.5, 0.5);
				ParticleEffect.BLOCK_DUST.display(new BlockData(leaf.getType(), leaf.getData()), (float) (Math.random() * 0.5), (float) (Math.random() * 0.5), (float) (Math.random() * 0.5), 0.05F, PARTICLE_AMOUNT, centre, bukkitPlayer);
				leaves.remove(leaf);
			}
		});
	}

	public void unload() {
		HandlerList.unregisterAll(this);
		task.cancel();
		PLAYERS.clear();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		PLAYERS.put(event.getPlayer(), new ALPlayer(event.getPlayer(), PARTICLE_RADIUS));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		PLAYERS.remove(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onMove(PlayerMoveEvent event) {
		Location from = event.getFrom();
		Location to = event.getTo();
		ALPlayer player = PLAYERS.get(event.getPlayer());
		if (from.getBlockX() != to.getBlockX()) {
			if (from.getBlockX() > to.getBlockX()) { // West
				for (int x = from.getBlockX() - 1; x >= to.getBlockX(); x--) {
					player.updateX(x);
				}
			} else { // East
				for (int x = from.getBlockX() + 1; x <= to.getBlockX(); x++) {
					player.updateX(x);
				}
			}
		}
		if (from.getBlockY() != to.getBlockY()) {
			if (from.getBlockY() > to.getBlockY()) { // Down
				for (int y = from.getBlockY() - 1; y >= to.getBlockY(); y--) {
					player.updateY(y);
				}
			} else { // Up
				for (int y = from.getBlockY() + 1; y <= to.getBlockY(); y++) {
					player.updateY(y);
				}
			}
			player.updateY(to.getBlockY());
		}
		if (from.getBlockZ() != to.getBlockZ()) {
			if (from.getBlockZ() > to.getBlockZ()) { // North
				for (int z = from.getBlockZ() - 1; z >= to.getBlockZ(); z--) {
					player.updateZ(z);
				}
			} else { // South
				for (int z = from.getBlockZ() + 1; z <= to.getBlockZ(); z++) {
					player.updateZ(z);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onDecay(LeavesDecayEvent event) {
		Block block = event.getBlock();
		getEntitiesAroundPoint(block.getLocation(), PARTICLE_RADIUS).stream().filter(entity -> entity instanceof Player).forEach(entity -> {
			ALPlayer player = PLAYERS.get((Player) entity);
			if (player!=null)
				player.removeBlock(block);
		});
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		if (block.getType() != Material.LEAVES && block.getType() != Material.LEAVES_2) {
			return;
		}
		getEntitiesAroundPoint(block.getLocation(), PARTICLE_RADIUS).stream().filter(entity -> entity instanceof Player).forEach(entity -> {
			ALPlayer player = PLAYERS.get((Player) entity);
			if (player!=null)
				player.addBlock(block);
		});
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (block.getType() != Material.LEAVES && block.getType() != Material.LEAVES_2) {
			return;
		}
		getEntitiesAroundPoint(block.getLocation(), PARTICLE_RADIUS).stream().filter(entity -> entity instanceof Player).forEach(entity -> {
			ALPlayer player = PLAYERS.get((Player) entity);
			if (player!=null)
				player.removeBlock(block);
		});
	}

	/**
	 * Gets a {@code List<Entity>} of entities around a specified radius from the specified area
	 * 
	 * @param location
	 *            The base location
	 * @param radius
	 *            The radius of blocks to look for entities from the location
	 * @return A list of entities around a point
	 */
	public static List<Entity> getEntitiesAroundPoint(Location location, double radius) {
		List<Entity> entities = new ArrayList<Entity>();
		World world = location.getWorld();

		// To find chunks we use chunk coordinates (not block coordinates!)
		int smallX = (int) (location.getX() - radius) >> 4;
		int bigX = (int) (location.getX() + radius) >> 4;
		int smallZ = (int) (location.getZ() - radius) >> 4;
		int bigZ = (int) (location.getZ() + radius) >> 4;

		for (int x = smallX; x <= bigX; x++) {
			for (int z = smallZ; z <= bigZ; z++) {
				if (world.isChunkLoaded(x, z)) {
					entities.addAll(Arrays.asList(world.getChunkAt(x, z).getEntities()));
				}
			}
		}

		Iterator<Entity> entityIterator = entities.iterator();
		while (entityIterator.hasNext()) {
			Entity e = entityIterator.next();
			if (e.getWorld().equals(location.getWorld()) && e.getLocation().distanceSquared(location) > radius * radius) {
				entityIterator.remove();
			} else if (e instanceof Player && ((Player) e).getGameMode().equals(GameMode.SPECTATOR)) {
				entityIterator.remove();
			}
		}

		return entities;
	}

	public static void displayColoredParticle(Location loc, ParticleEffect type, String hexVal, float xOffset, float yOffset, float zOffset, Player... players) {
		int r = 0;
		int g = 0;
		int b = 0;
		if (hexVal.length() <= 6) {
			r = Integer.valueOf(hexVal.substring(0, 2), 16).intValue();
			g = Integer.valueOf(hexVal.substring(2, 4), 16).intValue();
			b = Integer.valueOf(hexVal.substring(4, 6), 16).intValue();
		} else if (hexVal.length() <= 7 && hexVal.substring(0, 1).equals("#")) {
			r = Integer.valueOf(hexVal.substring(1, 3), 16).intValue();
			g = Integer.valueOf(hexVal.substring(3, 5), 16).intValue();
			b = Integer.valueOf(hexVal.substring(5, 7), 16).intValue();
		}
		float red = r / 255.0F;
		float green = g / 255.0F;
		float blue = b / 255.0F;
		if (red <= 0) {
			red = 1 / 255.0F;
		}
		loc.setX(loc.getX() + Math.random() * xOffset);
		loc.setY(loc.getY() + Math.random() * yOffset);
		loc.setZ(loc.getZ() + Math.random() * zOffset);

		if (type != ParticleEffect.RED_DUST && type != ParticleEffect.REDSTONE && type != ParticleEffect.SPELL_MOB && type != ParticleEffect.MOB_SPELL && type != ParticleEffect.SPELL_MOB_AMBIENT
		        && type != ParticleEffect.MOB_SPELL_AMBIENT) {
			type = ParticleEffect.RED_DUST;
		}
		type.display(red, green, blue, 1F, 0, loc, players);
	}

	public static void displayColoredParticle(Location loc, String hexVal, Player... players) {
		displayColoredParticle(loc, ParticleEffect.RED_DUST, hexVal, 0, 0, 0, players);
	}

	public static void displayColoredParticle(Location loc, String hexVal, float xOffset, float yOffset, float zOffset, Player... players) {
		displayColoredParticle(loc, ParticleEffect.RED_DUST, hexVal, xOffset, yOffset, zOffset, players);
	}

	public boolean isEnabledAll() {
		return enabledAll;
	}

	public void setEnabledAll(boolean enabledAll) {
		this.enabledAll = enabledAll;
	}

	public Map<Player, ALPlayer> getPlayers() {
		return PLAYERS;
	}
}
