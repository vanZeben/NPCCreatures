package tk.npccreatures.npcs.nms;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityMagmaCube;
import net.minecraft.server.Packet34EntityTeleport;
import net.minecraft.server.World;

public class NPCMagmaCube extends EntityMagmaCube {

	public NPCMagmaCube(World world)
	{
		super(world);
	}
	
	public void setBukkitEntity(org.bukkit.entity.Entity entity)
	{
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
	
	//PathFinding
	@Override
    protected void m_() {
    	return;
    }
	
	//Movement?
	@Override
    public void d() {
		final Location loc = this.getBukkitEntity().getLocation();
		final List<Player> players = this.world.getWorld().getPlayers();
		final Packet34EntityTeleport packet = new Packet34EntityTeleport(this);
		
		for(Player player : players)
		{
			if(player.getLocation().distanceSquared(loc) < 4096) {
				((CraftPlayer)player).getHandle().netServerHandler.sendPacket(packet);
			}
		}
        
    	return;
    }
	
	//onUpdate
	@Override
	public void w_()
	{
		super.w_();
		return;
	}
	
	//Attack?
	@Override
    public void a_(EntityHuman entityhuman) {
    	return;
    }
	
}
