package tk.npccreatures.npcs.entity;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.player.SpoutPlayer;

import tk.npccreatures.npcs.nms.NPCHuman;

/**
 * Represents a Human NPC.
 */
public class HumanNPC extends NPC {
	
	public HumanNPC(NPCHuman npcEntity, String name) {
		super(npcEntity, name);
	}
	
	/**
	 * Tells the NPC to animate an arm swing.
	 */
	public void animateArmSwing() {
		((WorldServer) getEntity().world).tracker.a(getEntity(), new Packet18ArmAnimation(getEntity(), 1));
	}
	
	/**
	 * Tells the NPC to do the hurt animation.
	 */
	public void actAsHurt() {
		((WorldServer) getEntity().world).tracker.a(getEntity(), new Packet18ArmAnimation(getEntity(), 2));
	}
	
	/**
	 * Sets the item the NPC is holding.
	 * @param The material for the NPC to hold.
	 */
	public void setItemInHand(Material m) {
		setItemInHand(m, (short) 0);
	}
	
	/**
	 * Sets the item the NPC is holding.
	 * @param The material for the NPC to hold.
	 * @param The data of the item to hold.
	 */
	public void setItemInHand(Material m, short damage) {
		((HumanEntity) getEntity().getBukkitEntity()).setItemInHand(new ItemStack(m, 1, damage));
	}
	
	/**
	 * Sets the name of the NPC.
	 * @param The new name of the NPC.
	 */
	public void setName(String name) {
		((NPCHuman) getEntity()).name = ChatColor.GREEN + name;
	}
	
	/**
	 * Gets the name of the NPC.
	 * @return The NPCs name.
	 */
	public String getName() {
		return ((NPCHuman) getEntity()).name.substring(2);
	}
	
	/**
	 * Sets the item the NPC is holding.
	 * @param Gets the inventory of the NPC.
	 * @return The inventory of the NPC.
	 */
	public PlayerInventory getInventory() {
		return ((HumanEntity) getEntity().getBukkitEntity()).getInventory();
	}
	
	/**
	 * Tells the NPC to sleep in a bed.
	 * @param The location of the bed to sleep in.
	 */
	public void putInBed(Location bed) {
		getEntity().setPosition(bed.getX(), bed.getY(), bed.getZ());
		getEntity().a((int) bed.getX(), (int) bed.getY(), (int) bed.getZ());
	}
	
	/**
	 * Wakes up the NPC.
	 */
	public void getOutOfBed() {
		((NPCHuman) getEntity()).a(true, true, true);
	}
	
	/**
	 * Sets the NPCs sneak state.
	 * @param The sneak state to set.
	 */
	public void setSneaking(boolean sneak) {
		getEntity().setSneak(sneak);
	}
	
	/**
	 * Gets a SpoutPlayer for the NPC.
	 * @return The SpoutPlayer of the NPC.
	 */
	public SpoutPlayer getSpoutPlayer() {
		try {
			Class.forName("org.getspout.spout.Spout");
			
			if (!(getEntity().getBukkitEntity() instanceof SpoutCraftPlayer)) {
				((NPCHuman) getEntity()).setBukkitEntity(new SpoutCraftPlayer((CraftServer) Bukkit.getServer(), (EntityPlayer) getEntity()));
			}
			
			return (SpoutPlayer) getEntity().getBukkitEntity();
		} catch (ClassNotFoundException e) {
			Bukkit.getServer().getLogger().warning("Cannot get spout player without spout installed");
		}
		return null;
	}
	
	/**
	 * Tells the NPC to look at a point.
	 * @param Location to look at.
	 */
	public void lookAtPoint(Location point) {
		if (getEntity().getBukkitEntity().getWorld() != point.getWorld()) {
			return;
		}
		Location npcLoc = ((LivingEntity) getEntity().getBukkitEntity()).getEyeLocation();
		double xDiff = point.getX() - npcLoc.getX();
		double yDiff = point.getY() - npcLoc.getY();
		double zDiff = point.getZ() - npcLoc.getZ();
		double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
		double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
		double newYaw = (Math.acos(xDiff / DistanceXZ) * 180 / Math.PI);
		double newPitch = (Math.acos(yDiff / DistanceY) * 180 / Math.PI) - 90;
		if (zDiff < 0.0) {
			newYaw = newYaw + (Math.abs(180 - newYaw) * 2);
		}
		getEntity().yaw = (float) (newYaw - 90);
		getEntity().pitch = (float) newPitch;
	}
	
}