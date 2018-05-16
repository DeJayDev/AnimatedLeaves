package me.sobki.animatedleaves;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ALPlayer {

	private Player player;

	private int radius;
	private int x, y, z;
	private int minX, minY, minZ;
	private int maxX, maxY, maxZ;
	private Set<Block> leaves;

	public ALPlayer(Player player, int radius) {
		this.player = player;
		this.radius = radius;
		this.x = player.getLocation().getBlockX();
		this.y = player.getLocation().getBlockY();
		this.z = player.getLocation().getBlockZ();
		this.minX = x - radius;
		this.minY = y - radius;
		this.minZ = z - radius;
		this.maxX = x + radius;
		this.maxY = y + radius;
		this.maxZ = z + radius;
		this.leaves = new HashSet<>();
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					Block block = player.getWorld().getBlockAt(x, y, z);
					addBlock(block);
				}
			}
		}
	}

	public void addBlock(Block block) {
		if (block.getType() == Material.LEAVES || block.getType() == Material.LEAVES_2) {
			leaves.add(block);
		}
	}

	public void removeBlock(Block block) {
		leaves.remove(block);
	}

	public void updateX(int newX) {
		int minX = newX - radius;
		int maxX = newX + radius;
		if (newX > this.x) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					Block oldblock = player.getWorld().getBlockAt(this.minX, y, z);
					removeBlock(oldblock);
					Block newblock = player.getWorld().getBlockAt(maxX, y, z);
					addBlock(newblock);
				}
			}
		} else {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					Block oldblock = player.getWorld().getBlockAt(this.maxX, y, z);
					removeBlock(oldblock);
					Block newblock = player.getWorld().getBlockAt(minX, y, z);
					addBlock(newblock);
				}
			}
		}
		this.x = newX;
		this.minX = minX;
		this.maxX = maxX;
	}

	public void updateY(int newY) {
		int minY = newY - radius;
		int maxY = newY + radius;
		if (newY > this.y) {
			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					Block oldblock = player.getWorld().getBlockAt(x, this.minY, z);
					removeBlock(oldblock);
					Block newblock = player.getWorld().getBlockAt(x, maxY, z);
					addBlock(newblock);
				}
			}
		} else {
			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					Block oldblock = player.getWorld().getBlockAt(x, this.maxY, z);
					removeBlock(oldblock);
					Block newblock = player.getWorld().getBlockAt(x, minY, z);
					addBlock(newblock);
				}
			}
		}
		this.y = newY;
		this.minY = minY;
		this.maxY = maxY;
	}

	public void updateZ(int newZ) {
		int minZ = newZ - radius;
		int maxZ = newZ + radius;
		if (newZ > this.z) {
			for (int x = minX; x <= maxX; x++) {
				for (int y = minY; y <= maxY; y++) {
					Block oldblock = player.getWorld().getBlockAt(x, y, this.minZ);
					removeBlock(oldblock);
					Block newblock = player.getWorld().getBlockAt(x, y, maxZ);
					addBlock(newblock);
				}
			}
		} else {
			for (int x = minX; x <= maxX; x++) {
				for (int y = minY; y <= maxY; y++) {
					Block oldblock = player.getWorld().getBlockAt(x, y, this.maxZ);
					removeBlock(oldblock);
					Block newblock = player.getWorld().getBlockAt(x, y, minZ);
					addBlock(newblock);
				}
			}
		}
		this.z = newZ;
		this.minZ = minZ;
		this.maxZ = maxZ;
	}

	public Set<Block> getLeaves() {
		return leaves;
	}

}
