package com.topcat.npclib.entity;

import com.topcat.npclib.nms.NPCSlime;

public class SlimeNPC extends NPC {

	public SlimeNPC(NPCSlime entity, String name)
	{
		super(entity, name);
	}
	
	public void setSize(int size)
	{
		((NPCSlime)this.entity).setSize(size);
	}
	
	public int getSize()
	{
		return ((NPCSlime)this.entity).getSize();
	}
	
}
