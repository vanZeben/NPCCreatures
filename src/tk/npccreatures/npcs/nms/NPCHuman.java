package tk.npccreatures.npcs.nms;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.player.SpoutPlayer;

import tk.npccreatures.npcs.NPCManager;

/**
 * 
 * @author martin
 */
public class NPCHuman extends EntityPlayer {

	private int lastTargetId;
	private long lastBounceTick;
	private int lastBounceId;
	private Player spoutPlayer;

	public NPCHuman(NPCManager npcManager, World world, String s, ItemInWorldManager itemInWorldManager) {
		super(npcManager.getServer().getMCServer(), world, ChatColor.GREEN + s, itemInWorldManager);

		itemInWorldManager.b(1);

		this.netServerHandler = new NPCNetHandler(npcManager, this);
		this.lastTargetId = -1;
		this.lastBounceId = -1;
		this.lastBounceTick = 0;
		try {
			Class.forName("org.getspout.spout.Spout");
			this.spoutPlayer = new SpoutCraftPlayer((CraftServer) Bukkit.getServer(), this);
		} catch (ClassNotFoundException e) {
		}
	}

	public void setBukkitEntity(org.bukkit.craftbukkit.entity.CraftPlayer entity) {
		this.bukkitEntity = entity;
	}

	@Override
	public boolean b(EntityHuman entity) {
		EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NpcEntityTargetEvent.NpcTargetReason.NPC_RIGHTCLICKED);
		CraftServer server = ((WorldServer) this.world).getServer();
		server.getPluginManager().callEvent(event);

		return super.b(entity);
	}

	@Override
	public void a_(EntityHuman entity) {
		if (lastTargetId == -1 || lastTargetId != entity.id) {
			EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NpcEntityTargetEvent.NpcTargetReason.CLOSEST_PLAYER);
			CraftServer server = ((WorldServer) this.world).getServer();
			server.getPluginManager().callEvent(event);
		}
		lastTargetId = entity.id;

		super.a_(entity);
	}

	@Override
	public void c(Entity entity) {
		if (lastBounceId != entity.id || System.currentTimeMillis() - lastBounceTick > 1000) {
			EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NpcEntityTargetEvent.NpcTargetReason.NPC_BOUNCED);
			CraftServer server = ((WorldServer) this.world).getServer();
			server.getPluginManager().callEvent(event);

			lastBounceTick = System.currentTimeMillis();
		}

		lastBounceId = entity.id;

		super.c(entity);
	}

	@Override
	public void move(double arg0, double arg1, double arg2) {
		setPosition(arg0, arg1, arg2);
	}
	
	public SpoutPlayer getSpoutPlayer() {
		try {
			Class.forName("org.getspout.spout.Spout");
			return (SpoutPlayer) this.spoutPlayer;
		} catch (ClassNotFoundException e) {
			Bukkit.getServer().getLogger().warning("Cannot get spout player without spout installed");
		}
		return null;
	}
	
	public void setSpoutPlayer(SpoutPlayer p) {
		try {
			Class.forName("org.getspout.spout.Spout");
			this.spoutPlayer = p;
		} catch (ClassNotFoundException e) {
			Bukkit.getServer().getLogger().warning("Cannot set spout player without spout installed");
		}
	}

}