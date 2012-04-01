package ch.spacebase.npccreatures.npcs.entity;

import java.util.List;

import net.minecraft.server.EntityLiving;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import ch.spacebase.npccreatures.npcs.pathing.NPCPath;
import ch.spacebase.npccreatures.npcs.pathing.PathReturn;

/**
 * Represents an NPC.
 */
public interface NPC extends org.bukkit.entity.LivingEntity {

	/**
	 * Gets the name of the NPC.
	 * 
	 * @return The NPCs name.
	 */
	public String getName();

	/**
	 * Gets the ID of the npc.
	 * 
	 * @return The NPCs ID.
	 */
	public String getNPCId();

	/**
	 * Sets the ID of an NPC
	 * 
	 * @param ID
	 *            to set.
	 */
	public void setNPCId(String id);

	/**
	 * Removes the entity from the world.
	 */
	public void removeFromWorld();

	// TODO: Confirm javadoc.
	/**
	 * Path finds the NPC to the given location.
	 * 
	 * @param Location
	 *            to path find to.
	 * @param PathReturn
	 *            to run the path find.
	 */
	public void pathFindTo(Location l, PathReturn callback);

	// TODO: Confirm javadoc.
	/**
	 * Path finds the NPC to the given location.
	 * 
	 * @param Location
	 *            to path find to.
	 * @param Max
	 *            iterations for the path.
	 * @param PathReturn
	 *            to run the path find.
	 */
	public void pathFindTo(Location l, int maxIterations, PathReturn callback);

	/**
	 * Tells the NPC to walk to a location.
	 * 
	 * @param Location
	 *            to walk to.
	 */
	public void walkTo(Location l);

	/**
	 * Tells the NPC to walk to a location.
	 * 
	 * @param Location
	 *            to walk to.
	 * @param Max
	 *            iterations for the path.
	 */
	public void walkTo(final Location l, final int maxIterations);

	/**
	 * Tells an NPC to walk using a specified path.
	 * 
	 * @param Path
	 *            to use.
	 */
	public void usePath(NPCPath path);

	/**
	 * Tells an NPC to walk using a specified path.
	 * 
	 * @param Path
	 *            to use.
	 * @param Runnable
	 *            to call if the path fails.
	 */
	public void usePath(NPCPath path, Runnable onFail);

	/**
	 * Sends a chat message from the NPC.
	 * 
	 * @param Message
	 *            to send.
	 */
	public void say(String message);

	/**
	 * Sends a chat message from the NPC to a certain player.
	 * 
	 * @param Message
	 *            to send.
	 * @param Player
	 *            to send the message to.
	 */
	public void say(String message, Player player);

	/**
	 * Sends a chat message from the NPC to a group of players.
	 * 
	 * @param Message
	 *            to send.
	 * @param Players
	 *            to send the message to.
	 */
	public void say(String message, List<Player> players);

	/**
	 * Sends a chat message from the NPC to players within a certain distance.
	 * 
	 * @param Message
	 *            to send.
	 * @param Distance
	 *            to send the message.
	 */
	public void say(String message, int distance);

	/**
	 * Makes the NPC pickup an item.
	 * 
	 * @param Item
	 *            to pickup.
	 */
	public void pickupItem(Item item);

	/**
	 * Sets whether the NPC picks up items or not.
	 * 
	 * @param Mode
	 *            to set.
	 */
	public void setPickupMode(boolean mode);

	/**
	 * Gets whether the NPC picks up items or not.
	 * 
	 * @return Current pickup mode.
	 */
	public boolean getPickupMode();

	/**
	 * Sets the distance to pickup items over.
	 * 
	 * @param Distance
	 *            to pickup items.
	 */
	public void setItemPickupDistance(int distance);

	/**
	 * Gets the distance to pickup items over.
	 * 
	 * @return Distance to pickup items.
	 */
	public int getItemPickupDistance();

	/**
	 * Gets the ticks until the NPCs overhead name changes back from a message.
	 * 
	 * @return Number of ticks.
	 */
	public int getLastNameTime();

	/**
	 * Sets the ticks until the NPCs overhead name changes back from a message.
	 * 
	 * @param Number
	 *            of ticks.
	 */
	public void setLastNameTime(int time);

	/**
	 * Gets the NPCs overhead name color
	 * 
	 * @return Overhead name color
	 */
	public ChatColor getNameColor();

	/**
	 * Sets the NPCs overhead name color
	 * 
	 * @param Overhead
	 *            name color
	 */
	public void setNameColor(ChatColor color);

	/**
	 * Gets the NMS entity of this NPC.
	 * 
	 * @return The NMS entity
	 */
	public EntityLiving getHandle();

	/**
	 * Sets the NPCs name
	 * 
	 * @param New
	 *            name
	 */
	public void setName(String name);
}