package com.topcat.npclib.entity;

import com.topcat.npclib.nms.NPCCreeper;

public class CreeperNPC extends NPC {

	public CreeperNPC(NPCCreeper entity, String name)
	{
		super(entity, name);
	}
	
	public void setPowered(boolean powered)
	{
		((NPCCreeper)this.entity).setPowered(true);
	}
	
}
