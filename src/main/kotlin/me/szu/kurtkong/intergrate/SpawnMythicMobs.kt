package me.szu.kurtkong.intergrate

import io.lumine.mythic.api.MythicPlugin
import io.lumine.mythic.api.MythicProvider
import io.lumine.mythic.api.adapters.AbstractLocation
import io.lumine.mythic.bukkit.adapters.BukkitLocation
import org.bukkit.Location
import org.bukkit.entity.Player

object SpawnMythicMobs {
    val processor={ player: Player, loc:Location, txt3:String, txt4:String->
        var amt=txt4.toIntOrNull()?:1
        for(i in 1..amt){
            spawn(loc,txt3)
        }
    }
    fun spawn(loc:Location,id:String){
        var moboption=MythicProvider.get().mobManager.getMythicMob(id)
        var mob=if(moboption.isPresent)moboption.get() else return

        mob.spawn(AbstractLocation(loc.world!!.name,loc.x,loc.y,loc.z) ,1.0)
    }
}