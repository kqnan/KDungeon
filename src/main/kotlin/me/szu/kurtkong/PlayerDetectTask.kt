package me.szu.kurtkong

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.util.nbt.CompoundBinaryTag
import com.sk89q.worldedit.world.block.BaseBlock
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import taboolib.common.platform.function.onlinePlayers
import taboolib.common.platform.function.submitAsync
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
          //  debug(entry.value.toString())
            if(entry.value.getString("id") == "minecraft:sign"){
                //[spawn] [mm]
                var txt1=entry.value.getString("Text1").split("\"")[3]
                var txt2=entry.value.getString("Text2").split("\"")[3]
                var txt3=entry.value.getString("Text3").split("\"")[3]
                var txt4=entry.value.getString("Text4").split("\"")[3]
                if(txt1=="%KD%") {//第一行为标识符
                    //第二行为动作
                    //第三行为动作的参数
                    //第四行为动作的数量
                    when (txt2) {
                        "[spawn]" -> sync {
                            var amt=txt4.toIntOrNull()?:1
                            for (i in 1..amt){
                                entry.key.world?.spawnEntity(entry.key, EntityType.valueOf(txt3.uppercase()))
                            }
                            entry.key.block.type = Material.AIR
                        }
                    }
                }
            }
        }
    }
}