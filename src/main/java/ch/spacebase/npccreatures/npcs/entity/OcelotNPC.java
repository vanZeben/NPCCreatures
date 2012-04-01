package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCOcelot;

public class OcelotNPC extends BaseNPC {

	public OcelotNPC(NPCOcelot entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}
	
}
