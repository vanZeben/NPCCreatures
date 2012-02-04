package tk.npccreatures.npcs.nms;

import java.util.ConcurrentModificationException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import net.minecraft.server.DamageSource;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityAnimal;
import net.minecraft.server.EntityWolf;
import net.minecraft.server.EntityWeatherLighting;
import net.minecraft.server.Packet34EntityTeleport;
import net.minecraft.server.World;

public class NPCWolf extends EntityWolf {

	public NPCWolf(World world) {
		super(world);
	}

	public void setBukkitEntity(org.bukkit.entity.Entity entity) {
		this.bukkitEntity = entity;
	}

	@Override
	public void move(double arg0, double arg1, double arg2) {
		return;
	}

	@Override
	public boolean damageEntity(DamageSource source, int damage) {
		return false;
	}

	@Override
	public void die() {
		return;
	}

	@Override
	protected EntityAnimal createChild(EntityAnimal entityanimal) {
		return null;
	}

	@Override
	protected Entity findTarget() {
		return null;
	}

	@Override
	public void setTamed(boolean flag) {
		return;
	}

	// PathFinding
	@Override
	protected void m_() {
		return;
	}

	// Stroll
	@Override
	public void D() {
		return;
	}

	// Movement?
	@Override
	public void d() {
		try {
			final Location loc = this.getBukkitEntity().getLocation();
			final List<Player> players = this.world.getWorld().getPlayers();
			final Packet34EntityTeleport packet = new Packet34EntityTeleport(this);

			for (Player player : players) {
				if (player.getLocation().distanceSquared(loc) < 4096) {
					((CraftPlayer) player).getHandle().netServerHandler.sendPacket(packet);
				}
			}
		} catch (ConcurrentModificationException ex) {
		}

		return;
	}

	// onUpdate
	@Override
	public void am() {
		super.am();
		return;
	}

	// Lightning
	@Override
	public void a(EntityWeatherLighting entityweatherlighting) {
		return;
	}

	// Targeting
	@SuppressWarnings("unused")
	private void c(Entity entity, float f) {
		return;
	}

	// Attack?
	@Override
	protected void a(Entity entity, float f) {
		return;
	}

}
