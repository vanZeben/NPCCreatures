package tk.npccreatures.npcs.nms;

import java.util.ConcurrentModificationException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import net.minecraft.server.DamageSource;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityCreeper;
import net.minecraft.server.EntityWeatherLighting;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet34EntityTeleport;
import net.minecraft.server.World;

public class NPCCreeper extends EntityCreeper {

	public NPCCreeper(World world) {
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

	// PathFinding
	@Override
	protected void m_() {
		return;
	}

	// Stroll
	@Override
	public void C() {
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
	public void w_() {
		super.af();
		if (this.aH > 0) {
			if (this.aI <= 0) {
				this.aI = 60;
			}

			--this.aI;
			if (this.aI <= 0) {
				--this.aH;
			}
		}

		this.d();
		double d0 = this.locX - this.lastX;
		double d1 = this.locZ - this.lastZ;
		float f = MathHelper.a(d0 * d0 + d1 * d1);
		float f1 = this.V;
		float f2 = 0.0F;

		this.X = this.Y;
		float f3 = 0.0F;

		if (f > 0.05F) {
			f3 = 1.0F;
			f2 = f * 3.0F;
			f1 = (float) Math.atan2(d1, d0) * 180.0F / 3.1415927F - 90.0F;
		}

		if (this.an > 0.0F) {
			f1 = this.yaw;
		}

		if (!this.onGround) {
			f3 = 0.0F;
		}

		this.Y += (f3 - this.Y) * 0.3F;

		float f4;

		for (f4 = f1 - this.V; f4 < -180.0F; f4 += 360.0F) {
			;
		}

		while (f4 >= 180.0F) {
			f4 -= 360.0F;
		}

		this.V += f4 * 0.3F;

		float f5;

		for (f5 = this.yaw - this.V; f5 < -180.0F; f5 += 360.0F) {
			;
		}

		while (f5 >= 180.0F) {
			f5 -= 360.0F;
		}

		boolean flag = f5 < -90.0F || f5 >= 90.0F;

		if (f5 < -75.0F) {
			f5 = -75.0F;
		}

		if (f5 >= 75.0F) {
			f5 = 75.0F;
		}

		this.V = this.yaw - f5;
		if (f5 * f5 > 2500.0F) {
			this.V += f5 * 0.2F;
		}

		if (flag) {
			f2 *= -1.0F;
		}

		while (this.yaw - this.lastYaw < -180.0F) {
			this.lastYaw -= 360.0F;
		}

		while (this.yaw - this.lastYaw >= 180.0F) {
			this.lastYaw += 360.0F;
		}

		while (this.V - this.W < -180.0F) {
			this.W -= 360.0F;
		}

		while (this.V - this.W >= 180.0F) {
			this.W += 360.0F;
		}

		while (this.pitch - this.lastPitch < -180.0F) {
			this.lastPitch -= 360.0F;
		}

		while (this.pitch - this.lastPitch >= 180.0F) {
			this.lastPitch += 360.0F;
		}

		this.Z += f2;
		return;
	}

	// Attack?
	@Override
	protected void a(Entity entity, float f) {
		return;
	}

	// Lightning
	@Override
	public void a(EntityWeatherLighting entityweatherlighting) {
		return;
	}

}
