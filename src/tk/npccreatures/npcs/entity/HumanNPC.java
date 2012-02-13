package tk.npccreatures.npcs.entity;

import java.util.Iterator;
import java.util.List;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet22Collect;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.player.SpoutPlayer;

import tk.npccreatures.NPCCreatures;
import tk.npccreatures.npcs.NPCManager;
import tk.npccreatures.npcs.nms.NPCHuman;
import tk.npccreatures.npcs.pathing.NPCPath;
import tk.npccreatures.npcs.pathing.NPCPathFinder;
import tk.npccreatures.npcs.pathing.Node;
import tk.npccreatures.npcs.pathing.PathReturn;

/**
 * Represents a Human NPC.
 */
public class HumanNPC extends org.bukkit.craftbukkit.entity.CraftPlayer implements NPC {

	private NPCPathFinder path;
	private Iterator<Node> pathIterator;
	private Node last;
	private NPCPath runningPath;
	private int taskid;
	private Runnable onFail;
	private String npcId;
	public int lastNameTime = 0;
	protected NPCCreatures plugin;
	private int itemPickupDistance = 2;
	private boolean pickupMode = false;
	private ChatColor nameColor = ChatColor.GREEN;
	
	public HumanNPC(NPCHuman npcEntity, String name) {
		super((CraftServer) Bukkit.getServer(), npcEntity);
		npcEntity.setBukkitEntity(this);
	}

	/**
	 * Tells the NPC to animate an arm swing.
	 */
	public void animateArmSwing() {
		((WorldServer) getHandle().world).tracker.a(getHandle(), new Packet18ArmAnimation(getHandle(), 1));
	}

	/**
	 * Tells the NPC to do the hurt animation.
	 */
	public void actAsHurt() {
		((WorldServer) getHandle().world).tracker.a(getHandle(), new Packet18ArmAnimation(getHandle(), 2));
	}

	/**
	 * Sets the name of the NPC.
	 * 
	 * @param The
	 *            new name of the NPC.
	 */
	public void setName(String name) {
		((NPCHuman) getHandle()).name = name;
		this.updateTitle();
	}

	@Override
	public String getName() {
		return ChatColor.stripColor(((NPCHuman) getHandle()).name);
	}

	/**
	 * Tells the NPC to sleep in a bed.
	 * 
	 * @param The
	 *            location of the bed to sleep in.
	 */
	public void putInBed(Location bed) {
		getHandle().setPosition(bed.getX(), bed.getY(), bed.getZ());
		getHandle().a((int) bed.getX(), (int) bed.getY(), (int) bed.getZ());
	}

	/**
	 * Wakes up the NPC.
	 */
	public void getOutOfBed() {
		((NPCHuman) getHandle()).a(true, true, true);
	}

	/**
	 * Sets the NPCs sneak state.
	 * 
	 * @param The
	 *            sneak state to set.
	 */
	public void setSneaking(boolean sneak) {
		getHandle().setSneak(sneak);
	}

	/**
	 * Gets a SpoutPlayer for the NPC.
	 * 
	 * @return The SpoutPlayer of the NPC.
	 */
	public SpoutPlayer getSpoutPlayer() {
		try {
			Class.forName("org.getspout.spout.Spout");
			return ((NPCHuman) getHandle()).getSpoutPlayer();
		} catch (ClassNotFoundException e) {
			Bukkit.getServer().getLogger().warning("Cannot get spout player without spout installed");
		}
		return null;
	}

	/**
	 * Tells the NPC to look at a point.
	 * 
	 * @param Location
	 *            to look at.
	 */
	public void lookAtPoint(Location point) {
		if (this.getWorld() != point.getWorld()) {
			return;
		}
		Location npcLoc = this.getEyeLocation();
		double xDiff = point.getX() - npcLoc.getX();
		double yDiff = point.getY() - npcLoc.getY();
		double zDiff = point.getZ() - npcLoc.getZ();
		double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
		double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
		double newYaw = (Math.acos(xDiff / DistanceXZ) * 180 / Math.PI);
		double newPitch = (Math.acos(yDiff / DistanceY) * 180 / Math.PI) - 90;
		if (zDiff < 0.0) {
			newYaw = newYaw + (Math.abs(180 - newYaw) * 2);
		}
		getHandle().yaw = (float) (newYaw - 90);
		getHandle().pitch = (float) newPitch;
	}

	public String getNPCId() {
		return this.npcId;
	}

	public void setNPCId(String id) {
		this.npcId = id;
	}

	public void removeFromWorld() {
		try {
			entity.world.removeEntity(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pathFindTo(Location l, PathReturn callback) {
		pathFindTo(l, 3000, callback);
	}

	public void pathFindTo(Location l, int maxIterations, PathReturn callback) {
		if (path != null) {
			path.cancel = true;
		}
		path = new NPCPathFinder(this.getLocation(), l, maxIterations, callback);
		path.start();
	}

	public void walkTo(Location l) {
		walkTo(l, 3000);
	}

	public void walkTo(final Location l, final int maxIterations) {
		pathFindTo(l, maxIterations, new PathReturn() {
			@Override
			public void run(NPCPath path) {
				usePath(path, new Runnable() {

					@Override
					public void run() {
						walkTo(l, maxIterations);
					}
				});
			}
		});
	}

	public void usePath(NPCPath path) {
		usePath(path, new Runnable() {
			@Override
			public void run() {
				walkTo(runningPath.getEnd(), 3000);
			}
		});
	}

	public void usePath(NPCPath path, Runnable onFail) {
		if (taskid == 0) {
			taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(NPCManager.plugin, new Runnable() {
				@Override
				public void run() {
					pathStep();
				}
			}, 6L, 6L);
		}
		pathIterator = path.getPath().iterator();
		runningPath = path;
		this.onFail = onFail;
	}

	private void pathStep() {
		if (pathIterator.hasNext()) {
			Node n = pathIterator.next();
			Block b = null;
			float angle = getHandle().yaw;
			float look = getHandle().pitch;
			if (last == null || runningPath.checkPath(n, last, true)) {
				b = n.b;
				if (last != null) {
					angle = ((float) Math.toDegrees(Math.atan2(last.b.getX() - b.getX(), last.b.getZ() - b.getZ())));
					look = (float) (Math.toDegrees(Math.asin(last.b.getY() - b.getY())) / 2);
				}
				getHandle().setPositionRotation(b.getX() + 0.5, b.getY(), b.getZ() + 0.5, angle, look);
			} else {
				onFail.run();
			}
			last = n;
		} else {
			getHandle().setPositionRotation(runningPath.getEnd().getX(), runningPath.getEnd().getY(), runningPath.getEnd().getZ(), runningPath.getEnd().getYaw(), runningPath.getEnd().getPitch());
			Bukkit.getServer().getScheduler().cancelTask(taskid);
			taskid = 0;
		}
	}

	public void say(String message) {
		Spout.getServer().setTitle(this, ChatColor.YELLOW + message + "\n" + this.nameColor + this.getName());
		this.lastNameTime = 5;
		if (!plugin.titleQueue.contains(this)) {
			plugin.titleQueue.add(this);
		}
		Bukkit.getServer().broadcastMessage(this.nameColor + this.getName() + ChatColor.WHITE + ": " + message);
		this.updateTitle();
	}

	public void say(String message, Player player) {
		this.setName(ChatColor.YELLOW + message + "\n" + this.nameColor + this.getName());
		this.lastNameTime = 5;
		if (!plugin.titleQueue.contains(this)) {
			plugin.titleQueue.add(this);
		}
		player.sendMessage(this.nameColor + this.getName() + ChatColor.WHITE + ": " + message);
		this.updateTitle(player);
		this.setName(this.nameColor + this.getName());
		player.sendMessage(this.nameColor + this.getName() + ChatColor.WHITE + ": " + message);
	}

	public void say(String message, List<Player> players) {
		for (Player player : players) {
			this.setName(ChatColor.YELLOW + message + "\n" + this.nameColor + this.getName());
			this.lastNameTime = 5;
			if (!plugin.titleQueue.contains(this)) {
				plugin.titleQueue.add(this);
			}
			player.sendMessage(this.nameColor + this.getName() + ChatColor.WHITE + ": " + message);
			this.updateTitle(player);
			this.setName(this.nameColor + this.getName());
		}
	}

	public void say(String message, int distance) {
		Player players[] = Bukkit.getServer().getOnlinePlayers();
		for (Player player : players) {
			if (player.getLocation().distanceSquared(this.getLocation()) <= distance) {
				this.setName(ChatColor.YELLOW + message + "\n" + this.nameColor + this.getName());
				this.lastNameTime = 5;
				if (!plugin.titleQueue.contains(this)) {
					plugin.titleQueue.add(this);
				}
				player.sendMessage(this.nameColor + this.getName() + ChatColor.WHITE + ": " + message);
				this.updateTitle(player);
				this.setName(this.nameColor + this.getName());
			}
		}
	}

	public void pickupItem(Item item) {
		((CraftServer) this.plugin.getServer()).getServer().getTracker(((CraftWorld) this.getWorld()).getHandle().dimension).a(entity, new Packet22Collect(item.getEntityId(), this.getEntityId()));
		item.remove();
	}

	public void setPickupMode(boolean mode) {
		this.pickupMode = mode;
	}

	public boolean getPickupMode() {
		return this.pickupMode;
	}

	public void setItemPickupDistance(int distance) {
		this.itemPickupDistance = distance;
	}

	public int getItemPickupDistance() {
		return this.itemPickupDistance;
	}
	
	public int getLastNameTime() {
		return this.lastNameTime;
	}
	
	public void setLastNameTime(int time) {
		this.lastNameTime = time;
	}
	
	private void updateTitle() {
		for(Player player : this.getServer().getOnlinePlayers()) {
			((CraftPlayer)player).getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(this.getEntityId()));
			((CraftPlayer)player).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn((EntityHuman) this.getHandle()));
		}
	}
	
	private void updateTitle(Player player) {
		((CraftPlayer)player).getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(this.getEntityId()));
		((CraftPlayer)player).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn((EntityHuman) this.getHandle()));
	}
	
	public void setNameColor(ChatColor color) {
		if(((NPCHuman) getHandle()).name.startsWith(color.toString())) return;
		this.setName(color + this.getName());
		this.nameColor = color;
		this.updateTitle();
	}
	
	public ChatColor getNameColor() {
		return this.nameColor;
	}

}