package tk.npccreatures.npcs;


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
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.Spout;

import tk.npccreatures.NPCCreatures;
import tk.npccreatures.npcs.entity.*;
import tk.npccreatures.npcs.nms.*;

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
	
    static
    {
    	try
    	{
			Method method = EntityTypes.class.getDeclaredMethod("a", new Class<?>[] {Class.class, String.class, int.class});
	    	method.setAccessible(true);
	    	method.invoke(null, new Object[] {NPCCreeper.class, "Creeper", 50});
	    	method.invoke(null, new Object[] {NPCSkeleton.class, "Skeleton", 51});
	    	method.invoke(null, new Object[] {NPCSpider.class, "Spider", 52});
	    	method.invoke(null, new Object[] {NPCGiant.class, "Giant", 53});
	    	method.invoke(null, new Object[] {NPCZombie.class, "Zombie", 54});
	    	method.invoke(null, new Object[] {NPCSlime.class, "Slime", 55});
	    	method.invoke(null, new Object[] {NPCGhast.class, "Ghast", 56});
	    	method.invoke(null, new Object[] {NPCPigZombie.class, "PigZombie", 57});
	    	method.invoke(null, new Object[] {NPCEnderman.class, "Enderman", 58});
	    	method.invoke(null, new Object[] {NPCCaveSpider.class, "CaveSpider", 59});
	    	method.invoke(null, new Object[] {NPCSilverfish.class, "Silverfish", 60});
	    	method.invoke(null, new Object[] {NPCBlaze.class, "Blaze", 61});
	    	method.invoke(null, new Object[] {NPCMagmaCube.class, "LavaSlime", 62});
	    	method.invoke(null, new Object[] {NPCEnderDragon.class, "EnderDragon", 63});
	    	method.invoke(null, new Object[] {NPCPig.class, "Pig", 90});
	    	method.invoke(null, new Object[] {NPCSheep.class, "Sheep", 91});
	    	method.invoke(null, new Object[] {NPCCow.class, "Cow", 92});
	    	method.invoke(null, new Object[] {NPCChicken.class, "Chicken", 93});
	    	method.invoke(null, new Object[] {NPCSquid.class, "Squid", 94});
	    	method.invoke(null, new Object[] {NPCWolf.class, "Wolf", 95});
	    	method.invoke(null, new Object[] {NPCMooshroom.class, "MushroomCow", 96});
	    	method.invoke(null, new Object[] {NPCSnowman.class, "SnowMan", 97});
	    	method.invoke(null, new Object[] {NPCVillager.class, "Villager", 120});
    	} catch(Exception ex)
    	{
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
					Entity j = npcs.get(i).getEntity();
					j.af();
					if (j.dead) {
						toRemove.add(i);
					}
				}
				for (String n : toRemove) {
					npcs.remove(n);
				}
			}
		}, 1L, 1L);
		Bukkit.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_DISABLE, new SL(), Priority.Normal, plugin);
		Bukkit.getServer().getPluginManager().registerEvent(Event.Type.CHUNK_LOAD, new WL(), Priority.Normal, plugin);
	}
	
	/**
	 * Gets a BWorld of the specified world.
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
	
	private class SL extends ServerListener {
		@Override
		public void onPluginDisable(PluginDisableEvent event) {
			if (event.getPlugin() == plugin) {
				despawnAll();
				Bukkit.getServer().getScheduler().cancelTask(taskid);
			}
		}
	}
	
	private class WL extends WorldListener {
		@Override
		public void onChunkLoad(ChunkLoadEvent event) {
			for (NPC npc : npcs.values()) {
				if (npc != null && event.getChunk() == npc.getLocation().getBlock().getChunk()) {
					BWorld world = getBWorld(event.getWorld());
					world.getWorldServer().addEntity(npc.getEntity());
				}
			}
		}
	}
	
	/**
	 * Spawns an NPC.
	 * @param Name of the NPC.
	 * @param Location for the NPC to spawn in.
	 * @param Type of NPC to spawn.
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
	 * @param Name of the NPC.
	 * @param Location for the NPC to spawn in.
	 * @param Type of NPC to spawn.
	 * @param ID of the NPC.
	 * @return Spawned NPC.
	 */
	public NPC spawnNPC(String name, Location l, NPCType type, String id) {
		if (npcs.containsKey(id)) {
			server.getLogger().log(Level.WARNING, "NPC with that id already exists, existing NPC returned");
			return npcs.get(id);
		} else {
			if (name.length() > 16) { // Check and nag if name is too long, spawn NPC anyway with shortened name.
				String tmp = name.substring(0, 16);
				server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
				server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
				name = tmp;
			}
			BWorld world = getBWorld(l.getWorld());
            Entity entity = this.createNPCEntity(type, world.getWorldServer(), ChatColor.GREEN + name);
			entity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
			world.getWorldServer().addEntity(entity); //the right way
			if(((NPCCreatures)plugin).isSpoutEnabled) Spout.getServer().setTitle((LivingEntity)entity.getBukkitEntity(), ChatColor.GREEN + name);
			NPC npc = this.getNewNPC(entity, ChatColor.GREEN + name);
			npc.setNPCId(id);
			npcs.put(id, npc);
			return npc;
		}
	}
	
	private NPC getNewNPC(Entity entity, String name)
	{
		if(entity instanceof NPCHuman) return new HumanNPC((NPCHuman)entity, name);
		if(entity instanceof NPCVillager) return new VillagerNPC((NPCVillager)entity, name);
		if(entity instanceof NPCZombie) return new ZombieNPC((NPCZombie)entity, name);
		if(entity instanceof NPCSpider) return new SpiderNPC((NPCSpider)entity, name);
		if(entity instanceof NPCCreeper) return new CreeperNPC((NPCCreeper)entity, name);
		if(entity instanceof NPCSkeleton) return new SkeletonNPC((NPCSkeleton)entity, name);
		if(entity instanceof NPCEnderman) return new EndermanNPC((NPCEnderman)entity, name);
		if(entity instanceof NPCBlaze) return new BlazeNPC((NPCBlaze)entity, name);
		if(entity instanceof NPCMagmaCube) return new MagmaCubeNPC((NPCMagmaCube)entity, name);
		if(entity instanceof NPCPigZombie) return new PigZombieNPC((NPCPigZombie)entity, name);
		if(entity instanceof NPCCaveSpider) return new CaveSpiderNPC((NPCCaveSpider)entity, name);
		if(entity instanceof NPCSlime) return new SlimeNPC((NPCSlime)entity, name);
		if(entity instanceof NPCSilverfish) return new SilverfishNPC((NPCSilverfish)entity, name);
		if(entity instanceof NPCSnowman) return new SnowmanNPC((NPCSnowman)entity, name);
		if(entity instanceof NPCPig) return new PigNPC((NPCPig)entity, name);
		if(entity instanceof NPCChicken) return new ChickenNPC((NPCChicken)entity, name);
		if(entity instanceof NPCCow) return new CowNPC((NPCCow)entity, name);
		if(entity instanceof NPCSheep) return new SheepNPC((NPCSheep)entity, name);
		if(entity instanceof NPCWolf) return new WolfNPC((NPCWolf)entity, name);
		if(entity instanceof NPCSquid) return new SquidNPC((NPCSquid)entity, name);
		if(entity instanceof NPCMooshroom) return new MooshroomNPC((NPCMooshroom)entity, name);
		if(entity instanceof NPCGhast) return new GhastNPC((NPCGhast)entity, name);
		if(entity instanceof NPCEnderDragon) return new EnderDragonNPC((NPCEnderDragon)entity, name);
		if(entity instanceof NPCGiant) return new GiantNPC((NPCGiant)entity, name);
		return null;
	}
	
	private Entity createNPCEntity(NPCType type, WorldServer world, String name)
	{
		if(type == NPCType.HUMAN) return new NPCHuman(this, world, name, new ItemInWorldManager(world));
		if(type == NPCType.VILLAGER) return new NPCVillager(world);
		if(type == NPCType.ZOMBIE) return new NPCZombie(world);
		if(type == NPCType.SPIDER) return new NPCSpider(world);
		if(type == NPCType.CREEPER) return new NPCCreeper(world);
		if(type == NPCType.SKELETON) return new NPCSkeleton(world);
		if(type == NPCType.ENDERMAN) return new NPCEnderman(world);
		if(type == NPCType.BLAZE) return new NPCBlaze(world);
		if(type == NPCType.MAGMACUBE) return new NPCMagmaCube(world);
		if(type == NPCType.PIGZOMBIE) return new NPCPigZombie(world);
		if(type == NPCType.CAVESPIDER) return new NPCCaveSpider(world);
		if(type == NPCType.SLIME) return new NPCSlime(world);
		if(type == NPCType.SILVERFISH) return new NPCSilverfish(world);
		if(type == NPCType.SNOWMAN) return new NPCSnowman(world);
		if(type == NPCType.PIG) return new NPCPig(world);
		if(type == NPCType.CHICKEN) return new NPCChicken(world);
		if(type == NPCType.COW) return new NPCCow(world);
		if(type == NPCType.SHEEP) return new NPCSheep(world);
		if(type == NPCType.WOLF) return new NPCWolf(world);
		if(type == NPCType.SQUID) return new NPCSquid(world);
		if(type == NPCType.MOOSHROOM) return new NPCMooshroom(world);
		if(type == NPCType.GHAST) return new NPCGhast(world);
		if(type == NPCType.ENDERDRAGON) return new NPCEnderDragon(world);
		if(type == NPCType.GIANT) return new NPCGiant(world);
		return null;
	}
	
	/**
	 * Despawns an NPC with the given ID.
	 * @param NPC ID to despawn.
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
	 * @param NPC name to despawn.
	 */
	public void despawnByName(String npcName) {
		if (npcName.length() > 16) {
			npcName = npcName.substring(0, 16); //Ensure you can still despawn
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
		npcs.clear();
	}
	
	/**
	 * Gets the NPC with the given ID.
	 * @param ID to look for.
	 */
	public NPC getNPC(String id) {
		return npcs.get(id);
	}
	
	/**
	 * Gets whether an entity is an NPC or not.
	 * @param Entity to check.
	 * @return Whether the entity is an NPC or not.
	 */
	@Deprecated
	public boolean isNPC(org.bukkit.entity.Entity e) {
		return (e instanceof NPC);
	}
	
	/**
	 * Gets npcs with the given name.
	 * @param Name to look for.
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
	 * @return NPCs spawned.
	 */
	public List<NPC> getNPCs() {
		return new ArrayList<NPC>(npcs.values());
	}
	
	/**
	 * Gets the ID of the given entity.
	 * @param The entity to get the ID of.
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
	 * @param The ID of the NPC.
	 * @param The name to set it to.
	 */
	public void rename(String id, String name) {
		if (name.length() > 16) { // Check and nag if name is too long, spawn NPC anyway with shortened name.
			String tmp = name.substring(0, 16);
			server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		HumanNPC npc = (HumanNPC) getNPC(id);
		npc.setName(name);
		BWorld b = getBWorld(npc.getLocation().getWorld());
		WorldServer s = b.getWorldServer();
		try {
			Method m = s.getClass().getDeclaredMethod("d", new Class[]{Entity.class});
			m.setAccessible(true);
			m.invoke(s, npc.getEntity());
			m = s.getClass().getDeclaredMethod("c", new Class[]{Entity.class});
			m.setAccessible(true);
			m.invoke(s, npc.getEntity());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		s.everyoneSleeping();
	}
	
	/**
	 * Gets a BServer of the current server instance.
	 * @return The BServer of the current server instance.
	 */
	public BServer getServer() {
		return server;
	}
	
	/**
	 * Gets the current NPCNetworkManager instance.
	 * @return The current NPCNetworkManager instance.
	 */
	public NPCNetworkManager getNPCNetworkManager() {
		return npcNetworkManager;
	}
	
	/**
	 * Gets an NPC from an entity.
	 * @return The NPC.
	 */
	@Deprecated
	public NPC getNPC(org.bukkit.entity.Entity entity)
	{
		if(entity instanceof NPC) return (NPC)entity;
		return null;
	}
	
}