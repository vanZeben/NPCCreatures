package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCZombie;

/**
 * Represents a Zombie NPC.
 */
public class ZombieNPC extends NPC {

	public ZombieNPC(NPCZombie entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
