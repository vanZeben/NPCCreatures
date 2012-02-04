package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCSquid;

public class SquidNPC extends BaseNPC {

	/**
	 * Represents a Squid NPC.
	 */
	public SquidNPC(NPCSquid entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
