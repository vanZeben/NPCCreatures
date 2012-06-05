package ch.spacebase.npccreatures.npcs.nms;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.player.SpoutPlayer;

import ch.spacebase.npccreatures.NPCCreatures;

public class NPCHuman extends EntityPlayer {

	private int lastTargetId;
	private long lastBounceTick;
	private int lastBounceId;
	private Player spoutPlayer;

	public NPCHuman(World world, String s, ItemInWorldManager itemInWorldManager) {
		super(((CraftServer) Bukkit.getServer()).getHandle().server, world, formatName(s), itemInWorldManager);

		itemInWorldManager.b(1);

		this.netServerHandler = new NPCNetHandler(((NPCCreatures) Bukkit.getServer().getPluginManager().getPlugin("NPCCreatures")).getNPCManager(), this);
		this.lastTargetId = -1;
		this.lastBounceId = -1;
		this.lastBounceTick = 0;
		
		try {
			Class.forName("org.getspout.spout.Spout");
			this.spoutPlayer = new SpoutCraftPlayer((CraftServer) Bukkit.getServer(), this);
		} catch (ClassNotFoundException e) {
		}
	}

	public static String formatName(String name)  {
	    if (name.contains("<black>") || name.contains("&0")) {
            name = name.replaceAll("<black>", "" + ChatColor.BLACK).replaceAll("&0", "" + ChatColor.BLACK);
        }
        if (name.contains("<darkblue>") || name.contains("&1")) {
            name = name.replaceAll("<darkblue>", "" + ChatColor.DARK_BLUE).replaceAll("&1", "" + ChatColor.DARK_BLUE);
        }
        if (name.contains("<darkgreen>") || name.contains("&2")) {
            name = name.replaceAll("<darkgreen>", "" + ChatColor.DARK_GREEN).replaceAll("&2", "" + ChatColor.DARK_GREEN);
        }
        if (name.contains("<teal>") || name.contains("&3")) {
            name = name.replaceAll("<teal>", "" + ChatColor.DARK_AQUA).replaceAll("&3", "" + ChatColor.DARK_AQUA);
        }
        if (name.contains("<darkred>") || name.contains("&4")) {
            name = name.replaceAll("<darkred>", "" + ChatColor.DARK_RED).replaceAll("&4", "" + ChatColor.DARK_RED);
        }
        if (name.contains("<darkpurple>") || name.contains("&5")) {
            name = name.replaceAll("<darkpurple>", "" + ChatColor.DARK_PURPLE).replaceAll("&5", "" + ChatColor.DARK_PURPLE);
        }
        if (name.contains("<gold>") || name.contains("&6")) {
            name = name.replaceAll("<gold>", "" + ChatColor.GOLD).replaceAll("&6", "" + ChatColor.GOLD);
        }
        if (name.contains("<gray>") || name.contains("&7")) {
            name = name.replaceAll("<gray>", "" + ChatColor.GRAY).replaceAll("&7", "" + ChatColor.GRAY);
        }
        if (name.contains("<darkgray>") || name.contains("&8")) {
            name = name.replaceAll("<darkgray>", "" + ChatColor.DARK_GRAY).replaceAll("&8", "" + ChatColor.DARK_GRAY);
        }
        if (name.contains("<blue>") || name.contains("&9")) {
            name = name.replaceAll("<blue>", "" + ChatColor.BLUE).replaceAll("&9", "" + ChatColor.BLUE);
        }
        if (name.contains("<green>") || name.contains("&a")) {
            name = name.replaceAll("<green>", "" + ChatColor.GREEN).replaceAll("&a", "" + ChatColor.GREEN);
        }
        if (name.contains("<aqua>") || name.contains("&b")) {
            name = name.replaceAll("<aqua>", "" + ChatColor.AQUA).replaceAll("&b", "" + ChatColor.AQUA);
        }
        if (name.contains("<red>") || name.contains("&c")) {
            name = name.replaceAll("<red>", "" + ChatColor.RED).replaceAll("&c", "" + ChatColor.RED);
        }
        if (name.contains("<purple>") || name.contains("&d")) {
            name = name.replaceAll("<purple>", "" + ChatColor.LIGHT_PURPLE).replaceAll("&d", "" + ChatColor.LIGHT_PURPLE);
        }
        if (name.contains("<yellow>") || name.contains("&e")) {
            name = name.replaceAll("<yellow>", "" + ChatColor.YELLOW).replaceAll("&e", "" + ChatColor.YELLOW);
        }
        if (name.contains("<white>") || name.contains("&f")) {
            name = name.replaceAll("<white>", "" + ChatColor.WHITE).replaceAll("&f", "" + ChatColor.WHITE);
        }
        if (name.contains("&l")) {
            name = name.replaceAll("&l", "" + ChatColor.BOLD);
        }
        if (name.contains("&o")) {
            name = name.replaceAll("&o", "" + ChatColor.ITALIC);
        }
        if (name.contains("&k")) {
            name = name.replaceAll("&k", "" + ChatColor.MAGIC);
        }
        if (name.contains("&m")) {
            name = name.replaceAll("&m", "" + ChatColor.STRIKETHROUGH);
        }
        if (name.contains("&n")) {
            name = name.replaceAll("&n", "" + ChatColor.UNDERLINE);
        }
        return name;
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