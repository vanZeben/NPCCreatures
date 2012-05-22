package ch.spacebase.npccreatures;

import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.Spout;

import ch.spacebase.npccreatures.npcs.NPCManager;
import ch.spacebase.npccreatures.npcs.NPCType;
import ch.spacebase.npccreatures.npcs.entity.HumanNPC;
import ch.spacebase.npccreatures.npcs.entity.NPC;

public class NPCCreatures extends JavaPlugin {

	public static final File config = new File("plugins" + File.separator + "NPCCreatures" + File.separator + "config.yml");
	public static Logger logger = Logger.getLogger("NPCCreatures");
	
	protected NPCManager npcManager;
	public boolean isSpoutEnabled = false;
	public NPCCreaturesServerListener serverListener = new NPCCreaturesServerListener(this);
	public List<NPC> titleQueue;
	public boolean pickupsEnabled = false;

	@Override
	public void onDisable() {
		try {
			if (!config.exists())
				config.mkdir();
			getConfig().save(config);
			npcManager.despawnAll();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.getLogger().info(this.getDescription().getFullName() + " has been disabled!");
	}

	@Override
	public void onEnable() {
		logger = this.getLogger();
		
		this.titleQueue = new ArrayList<NPC>();
		npcManager = new NPCManager(this);

		if (this.getServer().getPluginManager().getPlugin("Spout") != null) {
			this.isSpoutEnabled = true;
		}

		if (!config.exists()) {
			try {
				getConfig().save(config);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			getConfig().load(config);
			if (!getConfig().contains("pickup")) {
				// Compatibility code for old configuration format
				if (!getConfig().isConfigurationSection("npcs")) {
					ConfigurationSection np = getConfig().createSection("npcs");
					Map<String, Object> npcs = getConfig().getValues(false);
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

							getConfig().set(id, null);
						}
					}
				}

				getConfig().set("pickup", false);
				getConfig().save(config);
			}

			if (getConfig().getBoolean("pickup"))
				this.pickupsEnabled = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.loadNPCs();
		this.getServer().getPluginManager().registerEvents(this.serverListener, this);
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new ResourceRunnable(this) {
			@Override
			public void run() {
				NPCCreatures plugin = (NPCCreatures) params[0];
				if (plugin.titleQueue.size() > 0) {
					for (NPC npc : plugin.titleQueue) {
						try {
							if (npc.getLastNameTime() <= 0) {
								if (npc instanceof HumanNPC) {
									npc.setName(npc.getName());
								} else if (plugin.isSpoutEnabled) {
									Spout.getServer().setTitle(npc, (npc.getNameColor() != ChatColor.WHITE ? npc.getNameColor() : "") + npc.getName());
								}
								plugin.titleQueue.remove(npc);
							} else {
								npc.setLastNameTime(npc.getLastNameTime() - 1);
							}
						} catch (ConcurrentModificationException cme) {
						}
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

		this.getLogger().info(this.getDescription().getFullName() + " has been enabled!");
	}

	public void loadNPCs() {
		if (!getConfig().isConfigurationSection("npcs"))
			getConfig().createSection("npcs");
		Map<String, Object> npcs = getConfig().getConfigurationSection("npcs").getValues(false);
		Object o;
		MemorySection data;
		for (String id : npcs.keySet()) {
			o = npcs.get(id);
			if (o instanceof MemorySection) {
				data = (MemorySection) o;
				try {
					npcManager.spawnNPC(data.getString("name"), new Location(this.getServer().getWorld(data.getString("world")), data.getInt("x"), data.getInt("y"), data.getInt("z"), (float) data.getDouble("yaw"), (float) data.getDouble("pitch")), NPCType.getByName(data.getString("type")), id);
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
					sender.sendMessage(ChatColor.RED + "Valid types are: zombie, creeper, spider, skeleton, enderman, blaze, magmacube, silverfish, pigzombie, slime, cavespider, villager, human, mooshroom, cow, pig, sheep, chicken, wolf, squid, snowman, ghast, giant, enderdragon, irongolem, ocelot.");
					return true;
				}
				String name = args[0];
				NPCType type = NPCType.HUMAN;
				if (args.length > 1) {
					try {
						type = NPCType.getByName(args[1]);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invalid NPC type. Valid types are: zombie, creeper, spider, skeleton, enderman, blaze, magmacube, silverfish, pigzombie, slime, cavespider, villager, human, mooshroom, cow, pig, sheep, chicken, wolf, squid, snowman, ghast, giant, enderdragon, irongolem, ocelot.");
						return true;
					}
				}
				if (type == null) {
					sender.sendMessage(ChatColor.RED + "Invalid NPC type. Valid types are: zombie, creeper, spider, skeleton, enderman, blaze, magmacube, silverfish, pigzombie, slime, cavespider, villager, human, mooshroom, cow, pig, sheep, chicken, wolf, squid, snowman, ghast, giant, enderdragon, irongolem, ocelot.");
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
				
				getConfig().getConfigurationSection("npcs").createSection(npc.getNPCId(), data);
				
				try {
					getConfig().save(config);
				} catch (IOException e) {
					logger.warning("Unable to save NPC to config when creating an NPC!");
					e.printStackTrace();
				}
				
				sender.sendMessage(ChatColor.GREEN + "Successfully created an npc with type " + type.toString() + "!");
				return true;
			}
			if (command.getName().equalsIgnoreCase("deletenpc")) {
				if (args.length < 1) {
					sender.sendMessage(ChatColor.RED + "Usage: /deletenpc <id>");
					return true;
				}
				
				if (this.npcManager.getNPC(args[0]) == null) {
					sender.sendMessage(ChatColor.RED + "No NPC exists with the given ID.");
					return true;
				}
				
				this.npcManager.despawnById(args[0]);
				getConfig().getConfigurationSection("npcs").set(args[0], null);
				
				try {
					getConfig().save(config);
				} catch (IOException e) {
					logger.warning("Unable to save NPC to config when deleting an NPC!");
					e.printStackTrace();
				}
				
				sender.sendMessage(ChatColor.GREEN + "NPC successfully deleted!");
			}
		}
		return true;
	}

	public NPCManager getNPCManager() {
		return this.npcManager;
	}
	
	public static Logger getPluginLogger() {
		return logger;
	}
}
