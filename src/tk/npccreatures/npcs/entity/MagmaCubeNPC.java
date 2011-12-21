package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCMagmaCube;

/**
 * Represents a MagmaCube NPC.
 */
public class MagmaCubeNPC extends NPC {

	public MagmaCubeNPC(NPCMagmaCube entity, String name)
	{
		super(entity, name);
	}
	
	/**
	 * Sets the size of the NPC.
	 * @param New NPC size.
	 */
	public void setSize(int size)
	{
		((NPCMagmaCube)this.entity).setSize(size);
	}
	
	/**
	 * Gets the size of the NPC.
	 * @return The size of the NPC.
	 */
	public int getSize()
	{
		return ((NPCMagmaCube)this.entity).getSize();
	}
	
}
