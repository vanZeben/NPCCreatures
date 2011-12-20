package com.topcat.npclib.entity;

import com.topcat.npclib.nms.NPCMagmaCube;

public class MagmaCubeNPC extends NPC {

	public MagmaCubeNPC(NPCMagmaCube entity, String name)
	{
		super(entity, name);
	}
	
	public void setSize(int size)
	{
		((NPCMagmaCube)this.entity).setSize(size);
	}
	
	public int getSize()
	{
		return ((NPCMagmaCube)this.entity).getSize();
	}
	
}
