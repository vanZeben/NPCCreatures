package tk.npccreatures.npcs.nms;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityEnderDragon;
import net.minecraft.server.Packet34EntityTeleport;
import net.minecraft.server.World;

public class NPCEnderDragon extends EntityEnderDragon {

	public NPCEnderDragon(World world)
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
	
	//EnderCrystal link?
    @SuppressWarnings("unused")
	private void w() {
    	return;
    }
    
    //???
    @SuppressWarnings({ "unused", "rawtypes" })
	private void a(List list) {
    	return;
    }

    //???
    @SuppressWarnings({ "unused", "rawtypes" })
	private void b(List list) {
    	return;
    }
    
    //Explosions
    @SuppressWarnings("unused")
	private boolean a(AxisAlignedBB axisalignedbb) {
    	return false;
    }
    
    //Explosions
    @Override
    protected void ag() {
    	return;
    }

    //#winning
    @SuppressWarnings("unused")
	private void a(int i, int j) {
    	return;
    }
	
}
