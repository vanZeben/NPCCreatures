package ch.spacebase.npccreatures.npcs;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityTypes;
import net.minecraft.server.ItemInWorldManager;

import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.Spout;

import ch.spacebase.npccreatures.NPCCreatures;
import ch.spacebase.npccreatures.npcs.entity.*;
import ch.spacebase.npccreatures.npcs.nms.*;

/**
 * Manages NPCs
 */
public class NPCManager {

	private HashMap<String, NPC> npcs = new HashMap<String, NPC>();
	private BServer server;
	private int taskid;
	private Map<World, BWorld> bworlds = new HashMap<World, BWorld>();
	private NPCNetworkManager npcNetworkManager;
	public static JavaPlugin plugin;

	static {
		try {
			Method method = EntityTypes.class.getDeclaredMethod("a", new Class<?>[] { Class.class, String.class, int.class });
			method.setAccessible(true);
			method.invoke(null, NPCCreeper.class, "NPCCreeper", 50);
			method.invoke(null, NPCSkeleton.class, "NPCSkeleton", 51);
			method.invoke(null, NPCSpider.class, "NPCSpider", 52);
	        method.invoke(null, NPCGiant.class, "NPCGiant", 53);
	        method.invoke(null, NPCZombie.class, "NPCZombie", 54);
	        method.invoke(null, NPCSlime.class, "NPCSlime", 55);
	        method.invoke(null, NPCGhast.class, "NPCGhast", 56);
	        method.invoke(null, NPCPigZombie.class, "NPCPigZombie", 57);
	        method.invoke(null, NPCEnderman.class, "NPCEnderman", 58);
	        method.invoke(null, NPCCaveSpider.class, "NPCCaveSpider", 59);
	        method.invoke(null, NPCSilverfish.class, "NPCSilverfish", 60);
	        method.invoke(null, NPCBlaze.class, "NPCBlaze", 61);
	        method.invoke(null, NPCMagmaCube.class, "NPCMagmaCube", 62);
	        method.invoke(null, NPCEnderDragon.class, "NPCEnderDragon", 63);
	        method.invoke(null, NPCPig.class, "NPCPig", 90);
	        method.invoke(null, NPCSheep.class, "NPCSheep", 91);
	        method.invoke(null, NPCCow.class, "NPCCow", 92);
	        method.invoke(null, NPCChicken.class, "NPCChicken", 93);
	        method.invoke(null, NPCSquid.class, "NPCSquid", 94);
	        method.invoke(null, NPCWolf.class, "NPCWolf", 95);
	        method.invoke(null, NPCMooshroom.class, "NPCMooshroom", 96);
	        method.invoke(null, NPCSnowman.class, "NPCSnowman", 97);
	        method.invoke(null, NPCOcelot.class, "NPCOcelot", 98);
	        method.invoke(null, NPCIronGolem.class, "NPCIronGolem", 99);
	        method.invoke(null, NPCVillager.class, "NPCVillager", 120);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public NPCManager(JavaPlugin plugin) {
		server = BServer.getInstance();

		npcNetworkManager = new NPCNetworkManager();
		NPCManager.plugin = plugin;
		taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
	
			public void run() {
				HashSet<String> toRemove = new HashSet<String>();
				for (String i : npcs.keySet()) {
					Entity j = npcs.get(i).getHandle();
					j.aA();
					if (j.dead) {
						toRemove.add(i);
					}
				}
				for (String n : toRemove) {
					npcs.remove(n);
				}
			}
		}, 1L, 1L);
		Bukkit.getServer().getPluginManager().registerEvents(new SL(), plugin);
		Bukkit.getServer().getPluginManager().registerEvents(new WL(), plugin);
		// Bukkit.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_DISABLE,
		// new SL(), Priority.Normal, plugin);
		// Bukkit.getServer().getPluginManager().registerEvent(Event.Type.CHUNK_LOAD,
		// new WL(), Priority.Normal, plugin);
	}

	/**
	 * Gets a BWorld of the specified world.
	 * 
	 * @return The BWorld.
	 */
	public BWorld getBWorld(World world) {
		BWorld bworld = bworlds.get(world);
		if (bworld != null) {
			return bworld;
		}
		bworld = new BWorld(world);
		bworlds.put(world, bworld);
		return bworld;
	}

	private class SL implements Listener {
		@SuppressWarnings("unused")
		@EventHandler
		public void onPluginDisable(PluginDisableEvent event) {
			if (event.getPlugin() == plugin) {
				despawnAll();
				Bukkit.getServer().getScheduler().cancelTask(taskid);
			}
		}
	}

	private class WL implements Listener {
		@SuppressWarnings("unused")
		@EventHandler
		public void onChunkLoad(ChunkLoadEvent event) {
			for (NPC npc : npcs.values()) {
				if (npc != null && event.getChunk() == npc.getLocation().getBlock().getChunk()) {
					BWorld world = getBWorld(event.getWorld());
					world.getWorldServer().addEntity(npc.getHandle());
				}
			}
		}
	}

	/**
	 * Spawns an NPC.
	 * 
	 * @param Name
	 *            of the NPC.
	 * @param Location
	 *            for the NPC to spawn in.
	 * @param Type
	 *            of NPC to spawn.
	 * @return Spawned NPC.
	 */
	public NPC spawnNPC(String name, Location l, NPCType type) {
		int i = 0;
		String id = name + i;
		while (npcs.containsKey(id)) {
			id = name + i;
			i++;
		}
		return spawnNPC(name, l, type, id);
	}

	/**
	 * Spawns an NPC.
	 * 
	 * @param Name
	 *            of the NPC.
	 * @param Location
	 *            for the NPC to spawn in.
	 * @param Type
	 *            of NPC to spawn.
	 * @param ID
	 *            of the NPC.
	 * @return Spawned NPC.
	 */
	public NPC spawnNPC(String name, Location l, NPCType type, String id) {
		if (npcs.containsKey(id)) {
			server.getLogger().log(Level.WARNING, "NPC with that id already exists, existing NPC returned");
			return npcs.get(id);
		} else {
			if (name.length() > 16) { // Check and nag if name is too long,
				// spawn NPC anyway with shortened name.
				String tmp = name.substring(0, 16);
				server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
				server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
				name = tmp;
			}
			BWorld world = getBWorld(l.getWorld());
			Entity entity = this.createNPCEntity(type, world.getWorldServer(), name);
			entity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
			world.getWorldServer().addEntity(entity); // the right way
			if (((NPCCreatures) plugin).isSpoutEnabled)
				Spout.getServer().setTitle((LivingEntity) entity.getBukkitEntity(), ChatColor.GREEN + name);
			NPC npc = this.getNewNPC(entity, name);
			if (((NPCCreatures) plugin).pickupsEnabled)
				npc.setPickupMode(true);
			npc.setNPCId(id);
			npcs.put(id, npc);
			return npc;
		}
	}

	private NPC getNewNPC(Entity entity, String name) {
		if (entity instanceof NPCHuman)
			return new HumanNPC((NPCHuman) entity, name);
		if (entity instanceof NPCVillager)
			return new VillagerNPC((NPCVillager) entity, name);
		if (entity instanceof NPCZombie)
			return new ZombieNPC((NPCZombie) entity, name);
		if (entity instanceof NPCSpider)
			return new SpiderNPC((NPCSpider) entity, name);
		if (entity instanceof NPCCreeper)
			return new CreeperNPC((NPCCreeper) entity, name);
		if (entity instanceof NPCSkeleton)
			return new SkeletonNPC((NPCSkeleton) entity, name);
		if (entity instanceof NPCEnderman)
			return new EndermanNPC((NPCEnderman) entity, name);
		if (entity instanceof NPCBlaze)
			return new BlazeNPC((NPCBlaze) entity, name);
		if (entity instanceof NPCMagmaCube)
			return new MagmaCubeNPC((NPCMagmaCube) entity, name);
		if (entity instanceof NPCPigZombie)
			return new PigZombieNPC((NPCPigZombie) entity, name);
		if (entity instanceof NPCCaveSpider)
			return new CaveSpiderNPC((NPCCaveSpider) entity, name);
		if (entity instanceof NPCSlime)
			return new SlimeNPC((NPCSlime) entity, name);
		if (entity instanceof NPCSilverfish)
			return new SilverfishNPC((NPCSilverfish) entity, name);
		if (entity instanceof NPCSnowman)
			return new SnowmanNPC((NPCSnowman) entity, name);
		if (entity instanceof NPCPig)
			return new PigNPC((NPCPig) entity, name);
		if (entity instanceof NPCChicken)
			return new ChickenNPC((NPCChicken) entity, name);
		if (entity instanceof NPCCow)
			return new CowNPC((NPCCow) entity, name);
		if (entity instanceof NPCSheep)
			return new SheepNPC((NPCSheep) entity, name);
		if (entity instanceof NPCWolf)
			return new WolfNPC((NPCWolf) entity, name);
		if (entity instanceof NPCSquid)
			return new SquidNPC((NPCSquid) entity, name);
		if (entity instanceof NPCMooshroom)
			return new MooshroomNPC((NPCMooshroom) entity, name);
		if (entity instanceof NPCGhast)
			return new GhastNPC((NPCGhast) entity, name);
		if (entity instanceof NPCEnderDragon)
			return new EnderDragonNPC((NPCEnderDragon) entity, name);
		if (entity instanceof NPCGiant)
			return new GiantNPC((NPCGiant) entity, name);
		if (entity instanceof NPCOcelot)
			return new OcelotNPC((NPCOcelot) entity, name);
		if (entity instanceof NPCIronGolem)
			return new IronGolemNPC((NPCIronGolem) entity, name);
		return null;
	}

	private Entity createNPCEntity(NPCType type, WorldServer world, String name) {
		if (type == NPCType.HUMAN) {
			return new NPCHuman(world, name, new ItemInWorldManager(world));
		} else if(type != null && type.getNPCClass() != null) {
			try {
				return type.getNPCClass().getConstructor(net.minecraft.server.World.class).newInstance(world);
			} catch (Exception e) {
				NPCCreatures.getPluginLogger().severe("Failed to create NPC entity!");
				e.printStackTrace();
			}
		}
		
		return null;
	}

	/**
	 * Despawns an NPC with the given ID.
	 * 
	 * @param NPC
	 *            ID to despawn.
	 */
	public void despawnById(String id) {
		NPC npc = npcs.get(id);
		if (npc != null) {
			npcs.remove(id);
			npc.removeFromWorld();
		}
	}

	/**
	 * Despawns an NPC with the given name.
	 * 
	 * @param NPC
	 *            name to despawn.
	 */
	public void despawnByName(String npcName) {
		if (npcName.length() > 16) {
			npcName = npcName.substring(0, 16); // Ensure you can still despawn
		}
		HashSet<String> toRemove = new HashSet<String>();
		for (String n : npcs.keySet()) {
			NPC npc = npcs.get(n);
			if (npc != null && npc.getName().equals(npcName)) {
				toRemove.add(n);
				npc.removeFromWorld();
			}
		}
		for (String n : toRemove) {
			npcs.remove(n);
		}
	}

	/**
	 * Despawns all NPCs.
	 */
	public void despawnAll() {
		for (NPC npc : npcs.values()) {
			if (npc != null) {
				npc.removeFromWorld();
			}
		}
		
		// Just in case
		for (World world : Bukkit.getServer().getWorlds()) {
			for(org.bukkit.entity.LivingEntity entity : world.getLivingEntities()) {
				if(entity instanceof NPC) {
					NPC npc = ((NPC) entity);
					npc.removeFromWorld();
				}
			}
		}
		
		npcs.clear();
	}

	/**
	 * Gets the NPC with the given ID.
	 * 
	 * @param ID
	 *            to look for.
	 */
	public NPC getNPC(String id) {
		return npcs.get(id);
	}

	/**
	 * Gets whether an entity is an NPC or not.
	 * 
	 * @param Entity
	 *            to check.
	 * @return Whether the entity is an NPC or not.
	 */
	@Deprecated()
	public boolean isNPC(org.bukkit.entity.Entity e) {
		return (e instanceof NPC);
	}

	/**
	 * Gets npcs with the given name.
	 * 
	 * @param Name
	 *            to look for.
	 * @return NPCs with the name.
	 */
	public List<NPC> getNPCByName(String name) {
		List<NPC> ret = new ArrayList<NPC>();
		Collection<NPC> i = npcs.values();
		for (NPC e : i) {
			if (e.getName().equalsIgnoreCase(name)) {
				ret.add(e);
			}
		}
		return ret;
	}

	/**
	 * Gets all the spawned NPCs.
	 * 
	 * @return NPCs spawned.
	 */
	public List<NPC> getNPCs() {
		return new ArrayList<NPC>(npcs.values());
	}

	/**
	 * Gets the ID of the given entity.
	 * 
	 * @param The
	 *            entity to get the ID of.
	 * @return The entity's NPC ID.
	 */
	@Deprecated
	public String getNPCIdFromEntity(org.bukkit.entity.Entity e) {
		if (e instanceof HumanEntity) {
			for (String i : npcs.keySet()) {
				if (npcs.get(i).getEntityId() == ((HumanEntity) e).getEntityId()) {
					return i;
				}
			}
		}
		return null;
	}

	/**
	 * Renames the NPC with the specified ID.
	 * 
	 * @param The
	 *            ID of the NPC.
	 * @param The
	 *            name to set it to.
	 */
	public void rename(String id, String name) {
		if (name.length() > 16) {
			String tmp = name.substring(0, 16);
			server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		NPC npc = getNPC(id);
		npc.setName(name);
	}

	/**
	 * Gets a BServer of the current server instance.
	 * 
	 * @return The BServer of the current server instance.
	 */
	public BServer getServer() {
		return server;
	}

	/**
	 * Gets the current NPCNetworkManager instance.
	 * 
	 * @return The current NPCNetworkManager instance.
	 */
	public NPCNetworkManager getNPCNetworkManager() {
		return npcNetworkManager;
	}

	/**
	 * Gets an NPC from an entity.
	 * 
	 * @return The NPC.
	 */
	@Deprecated
	public NPC getNPC(org.bukkit.entity.Entity entity) {
		if (entity instanceof NPC)
			return (NPC) entity;
		return null;
	}

}