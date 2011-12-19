package me.steveice10.npccreatures;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Event;

import com.topcat.npclib.NPCManager;
import com.topcat.npclib.NPCType;
import com.topcat.npclib.entity.NPC;

public class NPCCreatures extends JavaPlugin {

	public NPCManager npcManager;
	public FileConfiguration config;
	public boolean isSpoutEnabled = false;
	public NPCCreaturesServerListener serverListener = new NPCCreaturesServerListener(this);
	
	@Override
	public void onDisable() {
		try {
			File npcCreatures = new File("plugins" + File.separator + "NPCCreatures" + File.separator + "config.yml");
			npcCreatures.mkdir();
			config.save(npcCreatures);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("["+this.getDescription().getName()+"] "+this.getDescription().getName()+" version "+this.getDescription().getVersion()+" has been disabled!");
	}

	@Override
	public void onEnable() {
		npcManager = new NPCManager(this);
		config = this.getConfig();
		if(this.getServer().getPluginManager().getPlugin("Spout") != null)
		{
			this.isSpoutEnabled = true;
		}
		File npcCreatures = new File("plugins" + File.separator + "NPCCreatures" + File.separator + "npcs.yml");
		npcCreatures.mkdir();
		try {
			config.load(npcCreatures);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.loadNPCs();
		this.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_ENABLE, serverListener, Event.Priority.Normal, this);
		this.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_DISABLE, serverListener, Event.Priority.Normal, this);
		System.out.println("["+this.getDescription().getName()+"] "+this.getDescription().getName()+" version "+this.getDescription().getVersion()+" has been enabled!");
	}
	
	public void loadNPCs()
	{
		Map<String, Object> npcs = config.getValues(false);
		Object o;
		MemorySection data;
		for(String id : npcs.keySet())
		{
			o = npcs.get(id);
			if(o instanceof MemorySection)
			{
				data = (MemorySection) o;
				npcManager.spawnNPC(data.getString("name"), new Location(this.getServer().getWorld(data.getString("world")), data.getInt("x"), data.getInt("y"), data.getInt("z"), (float) data.getDouble("yaw"), (float) data.getDouble("pitch")), NPCType.valueOf(data.getString("type")), id);
			}
		}
	}
	
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[]) {
		if((sender instanceof Player) && command.getName().equalsIgnoreCase("createnpc"))
		{
			if(args.length < 1)
			{
				sender.sendMessage(ChatColor.RED+"Usage: /createnpc <name> [type]");
				sender.sendMessage(ChatColor.RED+"Valid types are: zombie, creeper, spider, skeleton, enderman, blaze, magmacube, silverfish, pigzombie, slime, cavespider, villager, human, mooshroom, cow, pig, sheep, chicken, wolf, squid, snowman, ghast, giant, enderdragon.");
				return true;
			}
			String name = args[0];
			NPCType type = NPCType.HUMAN;
			if(args.length > 1) type = NPCType.valueOf(args[1].toUpperCase());
			if(type == null)
			{
				sender.sendMessage(ChatColor.RED+"Invalid NPC type. Valid types are: zombie, creeper, spider, skeleton, enderman, blaze, magmacube, silverfish, pigzombie, slime, cavespider, villager, human, mooshroom, cow, pig, sheep, chicken, wolf, squid, snowman.");
				return true;
			}
			NPC npc = this.npcManager.spawnNPC(name, ((Player)sender).getLocation(), type);
			Hashtable<String, Object> data = new Hashtable<String, Object>();
			data.put("name", npc.getName());
			data.put("type", type.toString());
			data.put("x", ((Player)sender).getLocation().getX());
			data.put("y", ((Player)sender).getLocation().getY());
			data.put("z", ((Player)sender).getLocation().getZ());
			data.put("yaw", ((Player)sender).getLocation().getYaw());
			data.put("pitch", ((Player)sender).getLocation().getPitch());
			data.put("world", ((Player)sender).getLocation().getWorld().getName());
			this.config.createSection(npc.getNPCId(), data);
			sender.sendMessage(ChatColor.GREEN+"Successfully created an npc with type "+type.toString()+"!");
			return true;
		}
		return true;
	}
	
}
