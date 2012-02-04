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

import net.minecraft.server.EntityLiving;
import net.minecraft.server.Packet22Collect;

/**
 * Represents an NPC.
 */
public class BaseNPC extends org.bukkit.craftbukkit.entity.CraftLivingEntity implements NPC {

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
	private ChatColor nameColor = ChatColor.GREEN;

	public BaseNPC(EntityLiving entity, String name) {
		super((CraftServer) Bukkit.getServer(), entity);
		this.name = name;
		this.plugin = ((NPCCreatures) Bukkit.getServer().getPluginManager().getPlugin("NPCCreatures"));
	}

	public String getName() {
		return this.name;
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
		if (plugin.isSpoutEnabled) {
			Spout.getServer().setTitle(this, ChatColor.YELLOW + message + "\n" + this.nameColor + this.getName());
			this.lastNameTime = 5;
			if (!plugin.titleQueue.contains(this)) {
				plugin.titleQueue.add(this);
			}
		}
		Bukkit.getServer().broadcastMessage(this.nameColor + this.getName() + ChatColor.WHITE + ": " + message);
	}

	public void say(String message, Player player) {
		if (plugin.isSpoutEnabled) {
			try {
				SpoutCraftPlayer.class.getMethod("sendDelayedPacket", SpoutPacket.class).invoke(((SpoutCraftPlayer) SpoutManager.getPlayer(player)), Class.forName("org.getspout.spoutapi.packet.PacketEntityTitle").getDeclaredConstructor(int.class, String.class).newInstance(this.getEntityId(), ChatColor.YELLOW + message + "\n" + this.nameColor + this.getName()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			this.lastNameTime = 5;
			if (!plugin.titleQueue.contains(this)) {
				plugin.titleQueue.add(this);
			}
		}
		player.sendMessage(this.nameColor + this.getName() + ChatColor.WHITE + ": " + message);
	}

	public void say(String message, List<Player> players) {
		for (Player player : players) {
			if (plugin.isSpoutEnabled) {
				try {
					SpoutCraftPlayer.class.getMethod("sendDelayedPacket", SpoutPacket.class).invoke(((SpoutCraftPlayer) SpoutManager.getPlayer(player)), Class.forName("org.getspout.spoutapi.packet.PacketEntityTitle").getDeclaredConstructor(int.class, String.class).newInstance(this.getEntityId(), ChatColor.YELLOW + message + "\n" + this.nameColor + this.getName()));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				this.lastNameTime = 5;
				if (!plugin.titleQueue.contains(this)) {
					plugin.titleQueue.add(this);
				}
			}
			player.sendMessage(this.nameColor + this.getName() + ChatColor.WHITE + ": " + message);
		}
	}

	public void say(String message, int distance) {
		Player players[] = Bukkit.getServer().getOnlinePlayers();
		for (Player player : players) {
			if (player.getLocation().distanceSquared(this.getLocation()) <= distance) {
				if (plugin.isSpoutEnabled) {
					try {
						SpoutCraftPlayer.class.getMethod("sendDelayedPacket", SpoutPacket.class).invoke(((SpoutCraftPlayer) SpoutManager.getPlayer(player)), Class.forName("org.getspout.spoutapi.packet.PacketEntityTitle").getDeclaredConstructor(int.class, String.class).newInstance(this.getEntityId(), ChatColor.YELLOW + message + "\n" + this.nameColor + this.getName()));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					this.lastNameTime = 5;
					if (!plugin.titleQueue.contains(this)) {
						plugin.titleQueue.add(this);
					}
				}
				player.sendMessage(this.nameColor + this.getName() + ChatColor.WHITE + ": " + message);
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
	
	public void setNameColor(ChatColor color) {
		this.nameColor = color;
		if(plugin.isSpoutEnabled) {
			Spout.getServer().setTitle(this, color + this.getName());
		}
	}
	
	public ChatColor getNameColor() {
		return this.nameColor;
	}
	
	public void setName(String name) {
		this.name = name;
		if(plugin.isSpoutEnabled) {
			Spout.getServer().setTitle(this, this.nameColor + name);
		}
	}

}