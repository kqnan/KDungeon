package me.szu.kurtkong

import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.util.nbt.CompoundBinaryTag
import me.szu.kurtkong.config.ConfigObject
import org.bukkit.Bukkit
import org.bukkit.Location
import taboolib.platform.BukkitAdapter
import java.util.concurrent.CopyOnWriteArrayList

object StructureData {
    //这里是否需要线程安全的容器
    val structures=CopyOnWriteArrayList<Structure>()
    var tmp=CopyOnWriteArrayList<Pair<Location,String>>()
//    init {
//        Bukkit.getScheduler().runTaskTimerAsynchronously(KDungeon.plugin, Runnable {
//            tmp.removeIf {
//                var res=true
//                for (structure in structures) {
//                    if(structure.key==it.second&& structure.originLocation == it.first){
//                        res=false
//                        break
//                    }
//                }
//                if(res )debug("tmp remove")
//                return@removeIf res
//            }
//        },0,1)
//    }
    private val lock=Any()
    fun addStructure(key:String ,clip:Clipboard,location: Location):Boolean{
        var loc=location.toBlockVector3()
        var minp=loc.add(clip.minimumPoint.subtract(clip.origin))
        var maxp=loc.add(clip.maximumPoint.subtract(clip.origin))
        var signs=HashMap<Location,CompoundBinaryTag>()
        for (blockVector3 in clip.region.iterator()) {
            var b=clip.getFullBlock(blockVector3)
            var nbt=b.nbt

            if(nbt!=null&& isSign(nbt)){//是木牌才进入表
                var worldloc=loc.add(blockVector3.subtract(clip.origin))
                signs[com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(location.world,worldloc)] = nbt
            }
        }
        var structure=Structure(key,minp.toBukkit(location.world!!),maxp.toBukkit(location.world!!),signs,location)
        //debug(structure.toString())

      var disbet= ConfigObject.getDistBet(key)
    //  var minb=minp.toBukkit(location.world!!)
     // var maxb=maxp.toBukkit(location.world!!)
        //var mid=location.clone()//Location(minb.world,(minb.x+maxb.x)/2.0,(minb.y+maxb.y)/2.0,(minb.z+maxb.z)/2.0)
     synchronized(lock){
         for (structure in StructureData.structures) {
             var p3=structure.originLocation//Location(p1.world,(p1.x+p2.x)/2.0,(p1.y+p2.y)/2.0,(p1.z+p2.z)/2.0)
             if(location.world!!.name==p3.world!!.name&&location.distance(p3)<disbet&&key==structure.key){
                 return false
             }
         }
         structures.add(structure)
     }
    return true
    }
    /**这里的位置是相对于世界坐标系的
     * */
   data class Structure(val key:String,val pos1:Location,val pos2:Location,val signs:HashMap<Location,CompoundBinaryTag>,
   val originLocation: Location
   ){
        override fun toString(): String {
            return "key: ${key}  pos1:${pos1} pos2:${pos2} signs:${signs.toString()}"
        }
    }
    fun isSign(nbt:CompoundBinaryTag):Boolean{
        if(nbt.getString("id")=="minecraft:sign"){
            if(nbt.getString("Text1").split("\"")[3]=="%KD%"){
                return true
            }
        }
        return false
    }
}