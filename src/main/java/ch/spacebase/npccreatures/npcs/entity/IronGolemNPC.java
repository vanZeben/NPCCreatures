package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCIronGolem;

public class IronGolemNPC extends BaseNPC {

	public IronGolemNPC(NPCIronGolem entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}
	
}
