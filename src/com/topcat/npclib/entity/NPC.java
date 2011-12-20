package com.topcat.npclib.entity;

import com.topcat.npclib.NPCManager;
import com.topcat.npclib.pathing.NPCPath;
import com.topcat.npclib.pathing.NPCPathFinder;
import com.topcat.npclib.pathing.Node;
import com.topcat.npclib.pathing.PathReturn;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.packet.PacketEntityTitle;

import me.steveice10.npccreatures.NPCCreatures;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityLiving;

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
	private NPCCreatures plugin;
	
	public NPC(EntityLiving entity, String name) {
		super((CraftServer)Bukkit.getServer(), entity);
		this.entity = entity;
		this.name = name;
		this.plugin = ((NPCCreatures)((CraftServer)Bukkit.getServer()).getPluginManager().getPlugin("NPCCreatures"));
		try {
			Field field = Entity.class.getDeclaredField("bukkitEntity");
			field.setAccessible(true);
			field.set(this.entity, this);
		} catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public Entity getEntity() {
		return entity;
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
	
	public org.bukkit.entity.Entity getBukkitEntity() {
		return entity.getBukkitEntity();
	}
	
	public void pathFindTo(Location l, PathReturn callback) {
		pathFindTo(l, 3000, callback);
	}
	
	public void pathFindTo(Location l, int maxIterations, PathReturn callback) {
		if (path != null) {
			path.cancel = true;
		}
		path = new NPCPathFinder(getEntity().getBukkitEntity().getLocation(), l, maxIterations, callback);
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
	
	public void say(String message)
	{
		if(plugin.isSpoutEnabled) {
			Spout.getServer().setTitle(this, ChatColor.YELLOW+message+"\n"+Spout.getServer().getTitle(this));
			this.lastNameTime = 5;
			if(!plugin.titleQueue.contains(this))
			{
				plugin.titleQueue.add(this);
			}
		}
		Bukkit.getServer().broadcastMessage(this.getName()+": "+message);
	}
	
	public void say(String message, Player player)
	{
		if(plugin.isSpoutEnabled) {
			((SpoutCraftPlayer)SpoutManager.getPlayer(player)).sendDelayedPacket(new PacketEntityTitle(this.getEntityId(), ChatColor.YELLOW+message+"\n"+this.getName()));
			this.lastNameTime = 5;
			if(!plugin.titleQueue.contains(this))
			{
				plugin.titleQueue.add(this);
			}
		}
		player.sendMessage(this.getName()+": "+message);
	}
	
	public void say(String message, List<Player> players)
	{
		for(Player player : players)
		{
			if(plugin.isSpoutEnabled) {
				((SpoutCraftPlayer)SpoutManager.getPlayer(player)).sendDelayedPacket(new PacketEntityTitle(this.getEntityId(), ChatColor.YELLOW+message+"\n"+Spout.getServer().getTitle(this)));
				this.lastNameTime = 5;
				if(!plugin.titleQueue.contains(this))
				{
					plugin.titleQueue.add(this);
				}
			}
			player.sendMessage(this.getName()+": "+message);
		}
	}
	
	public void say(String message, int distance)
	{
		Player players[] = Bukkit.getServer().getOnlinePlayers();
		for(Player player : players)
		{
			if(player.getLocation().distanceSquared(this.getLocation()) <= distance)
			{
				if(plugin.isSpoutEnabled) {
					((SpoutCraftPlayer)SpoutManager.getPlayer(player)).sendDelayedPacket(new PacketEntityTitle(this.getEntityId(), ChatColor.YELLOW+message+"\n"+Spout.getServer().getTitle(this)));
					this.lastNameTime = 5;
					if(!plugin.titleQueue.contains(this))
					{
						plugin.titleQueue.add(this);
					}
				}
				player.sendMessage(this.getName()+": "+message);
			}
		}
	}
	
}