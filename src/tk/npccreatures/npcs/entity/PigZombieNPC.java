package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCPigZombie;

/**
 * Represents a PigZombie NPC.
 */
public class PigZombieNPC extends NPC {

	public PigZombieNPC(NPCPigZombie entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
