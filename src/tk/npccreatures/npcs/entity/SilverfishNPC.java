package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCSilverfish;

public class SilverfishNPC extends BaseNPC {

	/**
	 * Represents a Silverfish NPC.
	 */
	public SilverfishNPC(NPCSilverfish entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
