package me.szu.kurtkong

import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.util.nbt.CompoundBinaryTag
import org.bukkit.Location
import taboolib.platform.BukkitAdapter
import java.util.concurrent.CopyOnWriteArrayList

object StructureData {
    //这里是否需要线程安全的容器
    val structures=CopyOnWriteArrayList<Structure>()

    fun addStructure(key:String ,clip:Clipboard,location: Location){
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
        var structure=Structure(key,minp.toBukkit(location.world!!),maxp.toBukkit(location.world!!),signs)
        //debug(structure.toString())
        structures.add(structure)
    }
    /**这里的位置是相对于世界坐标系的
     * */
   data class Structure(val key:String,val pos1:Location,val pos2:Location,val signs:HashMap<Location,CompoundBinaryTag>){
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