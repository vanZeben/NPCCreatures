package tk.npccreatures;

import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.Spout;

import tk.npccreatures.npcs.NPCManager;
import tk.npccreatures.npcs.NPCType;
import tk.npccreatures.npcs.entity.HumanNPC;
import tk.npccreatures.npcs.entity.NPC;

public class NPCCreatures extends JavaPlugin {

	protected NPCManager npcManager;
	public FileConfiguration config;
	public boolean isSpoutEnabled = false;
	public NPCCreaturesServerListener serverListener = new NPCCreaturesServerListener(this);
	public List<NPC> titleQueue;
	public boolean pickupsEnabled = false;

	@Override
	public void onDisable() {
		try {
			File npcCreaturesSave = new File("plugins" + File.separator + "NPCCreatures" + File.separator + "config.yml");
			if (!npcCreaturesSave.exists())
				npcCreaturesSave.mkdir();
			config.save(npcCreaturesSave);
			npcManager.despawnAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("[" + this.getDescription().getName() + "] " + this.getDescription().getName() + " version " + this.getDescription().getVersion() + " has been disabled!");
	}

	@Override
	public void onEnable() {
		this.titleQueue = new ArrayList<NPC>();
		npcManager = new NPCManager(this);
		config = this.getConfig();
		if (this.getServer().getPluginManager().getPlugin("Spout") != null) {
			this.isSpoutEnabled = true;
		}
		File npcCreaturesSave = new File("plugins" + File.separator + "NPCCreatures" + File.separator + "config.yml");
		if (!npcCreaturesSave.exists()) {
			try {
				config.save(npcCreaturesSave);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			config.load(npcCreaturesSave);
			if (!config.contains("pickup")) {
				// Compatibility code for old configuration format
				if (!config.isConfigurationSection("npcs")) {
					ConfigurationSection np = config.createSection("npcs");
					Map<String, Object> npcs = config.getValues(false);
					Object o;
					ConfigurationSection b;
					MemorySection d;
					Map<String, Object> vals;
					for (String id : npcs.keySet()) {
						if (id.equals("npcs"))
							continue;
						o = npcs.get(id);
						if (o instanceof MemorySection) {
							b = np.createSection(id);
							d = (MemorySection) o;
							vals = d.getValues(false);
							for (String id2 : vals.keySet()) {
								b.set(id2, d.get(id2));
							}
							config.set(id, null);
						}
					}
				}

				config.set("pickup", false);
				config.save(npcCreaturesSave);
			}
			if (config.getBoolean("pickup"))
				this.pickupsEnabled = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.loadNPCs();
		this.getServer().getPluginManager().registerEvents(this.serverListener, this);
		//this.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_ENABLE, serverListener, Event.Priority.Normal, this);
		//this.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_DISABLE, serverListener, Event.Priority.Normal, this);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new ResourceRunnable(this) {

			@Override
			public void run() {
				NPCCreatures plugin = (NPCCreatures) params[0];
				if (plugin.titleQueue.size() > 0) {
					for (NPC npc : plugin.titleQueue) {
						try {
							if (npc.getLastNameTime() <= 0) {
								if(npc instanceof HumanNPC) {
									npc.setName(npc.getNameColor() + npc.getName());
								} else if (plugin.isSpoutEnabled) {
									Spout.getServer().setTitle(npc, npc.getNameColor() + npc.getName());
								}
								plugin.titleQueue.remove(npc);
							} else {
								npc.setLastNameTime(npc.getLastNameTime() - 1);
							}
						} catch(ConcurrentModificationException cme) { }
					}
				}
			}

		}, 20, 20);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new ResourceRunnable(this) {

			@Override
			public void run() {
				NPCCreatures plugin = (NPCCreatures) params[0];
				for (NPC npc : plugin.npcManager.getNPCs()) {
					for (org.bukkit.entity.Entity e : npc.getWorld().getEntities()) {
						if (e instanceof Item) {
							if (npc.getPickupMode() == true && npc.getLocation().distanceSquared(e.getLocation()) < npc.getItemPickupDistance())
								npc.pickupItem((Item) e);
						}
					}
				}
			}
		}, 10, 10);
		System.out.println("[" + this.getDescription().getName() + "] " + this.getDescription().getName() + " version " + this.getDescription().getVersion() + " has been enabled!");
	}

	public void loadNPCs() {
		if (!config.isConfigurationSection("npcs"))
			config.createSection("npcs");
		Map<String, Object> npcs = config.getConfigurationSection("npcs").getValues(false);
		Object o;
		MemorySection data;
		for (String id : npcs.keySet()) {
			o = npcs.get(id);
			if (o instanceof MemorySection) {
				data = (MemorySection) o;
				try {
					npcManager.spawnNPC(data.getString("name"), new Location(this.getServer().getWorld(data.getString("world")), data.getInt("x"), data.getInt("y"), data.getInt("z"), (float) data.getDouble("yaw"), (float) data.getDouble("pitch")), NPCType.valueOf(data.getString("type")), id);
				} catch (Exception ex) {
					System.err.println("Error spawning an NPC - invalid config - skipping:");
					ex.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[]) {
		if (sender instanceof Player) {
			if (command.getName().equalsIgnoreCase("createnpc")) {
				if (args.length < 1) {
					sender.sendMessage(ChatColor.RED + "Usage: /createnpc <name> [type]");
					sender.sendMessage(ChatColor.RED + "Valid types are: zombie, creeper, spider, skeleton, enderman, blaze, magmacube, silverfish, pigzombie, slime, cavespider, villager, human, mooshroom, cow, pig, sheep, chicken, wolf, squid, snowman, ghast, giant, enderdragon.");
					return true;
				}
				String name = args[0];
				NPCType type = NPCType.HUMAN;
				if (args.length > 1) {
					try {
						type = NPCType.valueOf(args[1].toUpperCase());
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invalid NPC type. Valid types are: zombie, creeper, spider, skeleton, enderman, blaze, magmacube, silverfish, pigzombie, slime, cavespider, villager, human, mooshroom, cow, pig, sheep, chicken, wolf, squid, snowman.");
						return true;
					}
				}
				if (type == null) {
					sender.sendMessage(ChatColor.RED + "Invalid NPC type. Valid types are: zombie, creeper, spider, skeleton, enderman, blaze, magmacube, silverfish, pigzombie, slime, cavespider, villager, human, mooshroom, cow, pig, sheep, chicken, wolf, squid, snowman.");
					return true;
				}
				NPC npc = this.npcManager.spawnNPC(name, ((Player) sender).getLocation(), type);
				Hashtable<String, Object> data = new Hashtable<String, Object>();
				data.put("name", npc.getName());
				data.put("type", type.toString());
				data.put("x", ((Player) sender).getLocation().getX());
				data.put("y", ((Player) sender).getLocation().getY());
				data.put("z", ((Player) sender).getLocation().getZ());
				data.put("yaw", ((Player) sender).getLocation().getYaw());
				data.put("pitch", ((Player) sender).getLocation().getPitch());
				data.put("world", ((Player) sender).getLocation().getWorld().getName());
				this.config.getConfigurationSection("npcs").createSection(npc.getNPCId(), data);
				sender.sendMessage(ChatColor.GREEN + "Successfully created an npc with type " + type.toString() + "!");
				return true;
			}
			if (command.getName().equalsIgnoreCase("deletenpc")) {
				if (args.length < 1) {
					sender.sendMessage(ChatColor.RED + "Usage: /deletenpc <id>");
					return true;
				}
				this.npcManager.despawnById(args[0]);
				this.config.getConfigurationSection("npcs").set(args[0], null);
				sender.sendMessage(ChatColor.GREEN + "NPC successfully deleted!");
			}
		}
		return true;
	}

	public NPCManager getNPCManager() {
		return this.npcManager;
	}
}
