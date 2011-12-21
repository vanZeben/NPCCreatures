package tk.npccreatures;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.getspout.spoutapi.Spout;

import tk.npccreatures.npcs.entity.NPC;


public class NPCCreaturesServerListener extends ServerListener {

	private NPCCreatures plugin;
	
	public NPCCreaturesServerListener(NPCCreatures plugin)
	{
		this.plugin = plugin;
	}
	
	public void onPluginEnable(PluginEnableEvent event)
	{
		if(event.getPlugin().getDescription().getName().equals("Spout")) {
			plugin.isSpoutEnabled = true;
			for(NPC npc : plugin.npcManager.getNPCs())
			{
				Spout.getServer().setTitle((LivingEntity)npc, npc.getName());
			}
		}
	}
	
	public void onPluginDisable(PluginDisableEvent event)
	{
		if(event.getPlugin().getDescription().getName().equals("Spout")) {
			plugin.isSpoutEnabled = false;
		}
	}
	
}
