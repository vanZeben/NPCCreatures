package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCEnderDragon;

/**
 * Represents an EnderDragon NPC.
 */
public class EnderDragonNPC extends BaseNPC {

	public EnderDragonNPC(NPCEnderDragon entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
