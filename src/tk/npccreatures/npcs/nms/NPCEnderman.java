package tk.npccreatures.npcs.nms;

import java.util.ConcurrentModificationException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import net.minecraft.server.DamageSource;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityEnderman;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.Packet34EntityTeleport;
import net.minecraft.server.World;

public class NPCEnderman extends EntityEnderman {

	public NPCEnderman(World world) {
		super(world);
		this.damage = 0;
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
	protected Entity findTarget() {
		return null;
	}

	@Override
	public void setCarriedId(int i) {
		return;
	}

	@Override
	public void setCarriedData(int i) {
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

		for (int i = 0; i < 2; ++i) {
			this.world.a("portal", this.locX + (this.random.nextDouble() - 0.5D) * this.width, this.locY + this.random.nextDouble() * this.length - 0.25D, this.locZ + (this.random.nextDouble() - 0.5D) * this.width, (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
		}

		return;
	}

	// onUpdate
	@Override
	public void am() {
		super.am();
		return;
	}

	// Attack?
	@Override
	protected void a(Entity entity, float f) {
		return;
	}

	// Targetting
	@SuppressWarnings("unused")
	private boolean c(EntityHuman entityhuman) {
		return false;
	}

	// Movement?
	@Override
	protected boolean b(double d0, double d1, double d2) {
		return false;
	}

}
