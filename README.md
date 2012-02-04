<center>![NPCCreatures](http://dl.dropbox.com/u/40737374/Villager.png)</center>
<center><h1>NPCCreatures V1.3_02</h></center>
<center><h3><b>[Download](http://dev.bukkit.org/server-mods/npccreatures/files/10-npccreatures-v1.3_02/)</b> | <b>[Source](https://github.com/Steveice10/NPCCreatures)</b> | <b>[Issues](http://dev.bukkit.org/server-mods/npccreatures/tickets/)</b> | <b>[Javadocs](http://npccreatures.tk/hierarchy.html)</b></h></center>



*NPCCreatures*... what is this?

For Server Admins
==============

You can spawn NPCs of any mob type you want - yes, even Enderdragons! - and if you (optionally) have Spout installed, you can give them overhead names.

    /createnpc <name> [type]

And yes, you can delete them, too.

    /deletenpc <id>

Note that you need the NPC ID, not the NPC name. To get the ID, look in the config.yml for the NPC's entry and get the ID from the section title.

For Plugin Devs
============

You can hook into this and spawn your own NPCs:

    NPCManager npcManager = ((NPCCreatures) plugin.getServer().getPlugin("NPCCreatures")).getNPCManager();
    npcManager.spawnNPC(name, location, type);

And get an NPC from a Bukkit Entity:

    NPC npc = npcManager.getNPC(entity);
    if(npc != null) {
        ...
    }

You can also make them talk:

    npc.say(message);
    npc.say(message, player);
    npc.say(message, playerList);
    npc.say(message, distance);
