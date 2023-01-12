package me.szu.kurtkong

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.util.nbt.CompoundBinaryTag
import com.sk89q.worldedit.world.block.BaseBlock
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import taboolib.common.platform.function.onlinePlayers
import taboolib.common.util.sync
import taboolib.platform.util.toBukkitLocation

class PlayerDetectTask :Runnable {
    override fun run() {
        while (true){
            var players= onlinePlayers()
            for (player in players) {
                var loc= sync { player.location.toBukkitLocation() }
                StructureData.structures.removeIf {
                    var loc1=it.pos1
                    var loc2=it.pos2
                    var res=false
                    //debug("loc: $loc  loc1:$loc1 loc2:$loc2")
                  //  debug(loc.containWithin(loc1,loc2).toString())
                    if(loc.containWithin(loc1,loc2))//进入了区域
                    {
                        res=true
                        parseNBT(it.signs)//解析木牌
                    }
                    return@removeIf res
                }

            }
        }
    }
    private  fun parseNBT(data:HashMap<Location,CompoundBinaryTag>){
        for (entry in data.entries) {
            debug(entry.value.toString())
            if(entry.value.getString("id") == "minecraft:sign"){
                //[spawn] [mm]
                var txt1=entry.value.getString("Text1").split("\"")[3]
                var txt2=entry.value.getString("Text2").split("\"")[3]

                when(txt1){
                    "[spawn]"-> sync { entry.key.world?.spawnEntity(entry.key, EntityType.valueOf(txt2.uppercase()))
                        entry.key.block.type = Material.AIR
                    }
                }
            }
        }
    }
}