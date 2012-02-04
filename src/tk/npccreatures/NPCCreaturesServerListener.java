package tk.npccreatures;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.getspout.spoutapi.Spout;

import tk.npccreatures.npcs.entity.NPC;

public class NPCCreaturesServerListener implements Listener {

	private NPCCreatures plugin;

	public NPCCreaturesServerListener(NPCCreatures plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPluginEnable(PluginEnableEvent event) {
		if (event.getPlugin().getDescription().getName().equals("Spout")) {
			plugin.isSpoutEnabled = true;
			for (NPC npc : plugin.npcManager.getNPCs()) {
				Spout.getServer().setTitle(npc, npc.getName());
			}
		}
	}

	@EventHandler
	public void onPluginDisable(PluginDisableEvent event) {
		if (event.getPlugin().getDescription().getName().equals("Spout")) {
			plugin.isSpoutEnabled = false;
		}
	}

}
