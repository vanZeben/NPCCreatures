package com.topcat.npclib.entity;

import com.topcat.npclib.nms.NPCWolf;

public class WolfNPC extends NPC {

	public WolfNPC(NPCWolf entity, String name)
	{
		super(entity, name);
	}
	
    public void setSitting(boolean sitting) {
    	((NPCWolf)this.entity).setSitting(true);
    }
    
    public void setAngry(boolean angry) {
    	((NPCWolf)this.entity).setAngry(true);
    }
    
    public boolean isAngry() {
    	return ((NPCWolf)this.entity).isAngry();
    }
    
    public boolean isSitting() {
    	return ((NPCWolf)this.entity).isSitting();
    }
	
}
