package tk.npccreatures.npcs.entity;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.packet.SpoutPacket;

import tk.npccreatures.NPCCreatures;
import tk.npccreatures.npcs.NPCManager;
import tk.npccreatures.npcs.pathing.NPCPath;
import tk.npccreatures.npcs.pathing.NPCPathFinder;
import tk.npccreatures.npcs.pathing.Node;
import tk.npccreatures.npcs.pathing.PathReturn;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.Packet22Collect;

/**
 * Represents an NPC.
 */
public class NPC extends org.bukkit.craftbukkit.entity.CraftLivingEntity {

	protected Entity entity;
	private NPCPathFinder path;
	private Iterator<Node> pathIterator;
	private Node last;
	private NPCPath runningPath;
	private int taskid;
	private Runnable onFail;
	private String name;
	private String npcId;
	public int lastNameTime = 0;
	protected NPCCreatures plugin;
	private int itemPickupDistance = 2;
	private boolean pickupMode = false;

	public NPC(EntityLiving entity, String name) {
		super((CraftServer) Bukkit.getServer(), entity);
		this.entity = entity;
		this.name = name;
		this.plugin = ((NPCCreatures) Bukkit.getServer().getPluginManager().getPlugin("NPCCreatures"));
	}

	/**
	 * Gets the NMS entity of the NPC.
	 * 
	 * @return The NMS entity of the NPC.
	 */
	public Entity getEntity() {
		return entity;
	}

	/**
	 * Gets the name of the NPC.
	 * 
	 * @return The NPCs name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the ID of the npc.
	 * 
	 * @return The NPCs ID.
	 */
	public String getNPCId() {
		return this.npcId;
	}

	/**
	 * Sets the ID of an NPC
	 * 
	 * @param ID
	 *            to set.
	 */
	public void setNPCId(String id) {
		this.npcId = id;
	}

	/**
	 * Removes the entity from the world.
	 */
	public void removeFromWorld() {
		try {
			entity.world.removeEntity(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// TODO: Confirm javadoc.
	/**
	 * Path finds the NPC to the given location.
	 * 
	 * @param Location
	 *            to path find to.
	 * @param PathReturn
	 *            to run the path find.
	 */
	public void pathFindTo(Location l, PathReturn callback) {
		pathFindTo(l, 3000, callback);
	}

	// TODO: Confirm javadoc.
	/**
	 * Path finds the NPC to the given location.
	 * 
	 * @param Location
	 *            to path find to.
	 * @param Max
	 *            iterations for the path.
	 * @param PathReturn
	 *            to run the path find.
	 */
	public void pathFindTo(Location l, int maxIterations, PathReturn callback) {
		if (path != null) {
			path.cancel = true;
		}
		path = new NPCPathFinder(getEntity().getBukkitEntity().getLocation(), l, maxIterations, callback);
		path.start();
	}

	/**
	 * Tells the NPC to walk to a location.
	 * 
	 * @param Location
	 *            to walk to.
	 */
	public void walkTo(Location l) {
		walkTo(l, 3000);
	}

	/**
	 * Tells the NPC to walk to a location.
	 * 
	 * @param Location
	 *            to walk to.
	 * @param Max
	 *            iterations for the path.
	 */
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

	/**
	 * Tells an NPC to walk using a specified path.
	 * 
	 * @param Path
	 *            to use.
	 */
	public void usePath(NPCPath path) {
		usePath(path, new Runnable() {
			@Override
			public void run() {
				walkTo(runningPath.getEnd(), 3000);
			}
		});
	}

	/**
	 * Tells an NPC to walk using a specified path.
	 * 
	 * @param Path
	 *            to use.
	 * @param Runnable
	 *            to call if the path fails.
	 */
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

	/**
	 * Tells the NPC to do the next step on its path.
	 */
	private void pathStep() {
		if (pathIterator.hasNext()) {
			Node n = pathIterator.next();
			Block b = null;
			float angle = getEntity().yaw;
			float look = getEntity().pitch;
			if (last == null || runningPath.checkPath(n, last, true)) {
				b = n.b;
				if (last != null) {
					angle = ((float) Math.toDegrees(Math.atan2(last.b.getX() - b.getX(), last.b.getZ() - b.getZ())));
					look = (float) (Math.toDegrees(Math.asin(last.b.getY() - b.getY())) / 2);
				}
				getEntity().setPositionRotation(b.getX() + 0.5, b.getY(), b.getZ() + 0.5, angle, look);
			} else {
				onFail.run();
			}
			last = n;
		} else {
			getEntity().setPositionRotation(runningPath.getEnd().getX(), runningPath.getEnd().getY(), runningPath.getEnd().getZ(), runningPath.getEnd().getYaw(), runningPath.getEnd().getPitch());
			Bukkit.getServer().getScheduler().cancelTask(taskid);
			taskid = 0;
		}
	}

	/**
	 * Sends a chat message from the NPC.
	 * 
	 * @param Message
	 *            to send.
	 */
	public void say(String message) {
		if (plugin.isSpoutEnabled) {
			Spout.getServer().setTitle(this, ChatColor.YELLOW + message + "\n" + ChatColor.GREEN + this.getName());
			this.lastNameTime = 5;
			if (!plugin.titleQueue.contains(this)) {
				plugin.titleQueue.add(this);
			}
		}
		Bukkit.getServer().broadcastMessage(ChatColor.GREEN + this.getName() + ChatColor.WHITE + ": " + message);
	}

	/**
	 * Sends a chat message from the NPC to a certain player.
	 * 
	 * @param Message
	 *            to send.
	 * @param Player
	 *            to send the message to.
	 */
	public void say(String message, Player player) {
		// TODO: LLLA
		if (plugin.isSpoutEnabled) {
			try {
				SpoutCraftPlayer.class.getMethod("sendDelayedPacket", SpoutPacket.class).invoke(((SpoutCraftPlayer) SpoutManager.getPlayer(player)), Class.forName("org.getspout.spoutapi.packet.PacketEntityTitle").getDeclaredConstructor(int.class, String.class).newInstance(this.getEntityId(), ChatColor.YELLOW + message + "\n" + ChatColor.GREEN + this.getName()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			this.lastNameTime = 5;
			if (!plugin.titleQueue.contains(this)) {
				plugin.titleQueue.add(this);
			}
		}
		player.sendMessage(ChatColor.GREEN + this.getName() + ChatColor.WHITE + ": " + message);
	}

	/**
	 * Sends a chat message from the NPC to a group of players.
	 * 
	 * @param Message
	 *            to send.
	 * @param Players
	 *            to send the message to.
	 */
	public void say(String message, List<Player> players) {
		for (Player player : players) {
			if (plugin.isSpoutEnabled) {
				try {
					SpoutCraftPlayer.class.getMethod("sendDelayedPacket", SpoutPacket.class).invoke(((SpoutCraftPlayer) SpoutManager.getPlayer(player)), Class.forName("org.getspout.spoutapi.packet.PacketEntityTitle").getDeclaredConstructor(int.class, String.class).newInstance(this.getEntityId(), ChatColor.YELLOW + message + "\n" + ChatColor.GREEN + this.getName()));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				this.lastNameTime = 5;
				if (!plugin.titleQueue.contains(this)) {
					plugin.titleQueue.add(this);
				}
			}
			player.sendMessage(ChatColor.GREEN + this.getName() + ChatColor.WHITE + ": " + message);
		}
	}

	/**
	 * Sends a chat message from the NPC to players within a certain distance.
	 * 
	 * @param Message
	 *            to send.
	 * @param Distance
	 *            to send the message.
	 */
	public void say(String message, int distance) {
		Player players[] = Bukkit.getServer().getOnlinePlayers();
		for (Player player : players) {
			if (player.getLocation().distanceSquared(this.getLocation()) <= distance) {
				if (plugin.isSpoutEnabled) {
					try {
						SpoutCraftPlayer.class.getMethod("sendDelayedPacket", SpoutPacket.class).invoke(((SpoutCraftPlayer) SpoutManager.getPlayer(player)), Class.forName("org.getspout.spoutapi.packet.PacketEntityTitle").getDeclaredConstructor(int.class, String.class).newInstance(this.getEntityId(), ChatColor.YELLOW + message + "\n" + ChatColor.GREEN + this.getName()));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					this.lastNameTime = 5;
					if (!plugin.titleQueue.contains(this)) {
						plugin.titleQueue.add(this);
					}
				}
				player.sendMessage(ChatColor.GREEN + this.getName() + ChatColor.WHITE + ": " + message);
			}
		}
	}

	/**
	 * Makes the NPC pickup an item.
	 * 
	 * @param Item
	 *            to pickup.
	 */
	public void pickupItem(Item item) {
		((CraftServer) this.plugin.getServer()).getServer().getTracker(((CraftWorld) this.getWorld()).getHandle().dimension).a(entity, new Packet22Collect(item.getEntityId(), this.getEntityId()));
		item.remove();
	}

	/**
	 * Sets whether the NPC picks up items or not.
	 * 
	 * @param Mode
	 *            to set.
	 */
	public void setPickupMode(boolean mode) {
		this.pickupMode = true;
	}

	/**
	 * Gets whether the NPC picks up items or not.
	 * 
	 * @return Current pickup mode.
	 */
	public boolean getPickupMode() {
		return this.pickupMode;
	}

	/**
	 * Sets the distance to pickup items over.
	 * 
	 * @param Distance
	 *            to pickup items.
	 */
	public void setItemPickupDistance(int distance) {
		this.itemPickupDistance = distance;
	}

	/**
	 * Gets the distance to pickup items over.
	 * 
	 * @return Distance to pickup items.
	 */
	public int getItemPickupDistance() {
		return this.itemPickupDistance;
	}

}