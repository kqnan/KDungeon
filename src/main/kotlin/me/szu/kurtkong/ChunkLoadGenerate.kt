package me.szu.kurtkong

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.world.chunk.Chunk
import me.szu.kurtkong.config.ConfigObject
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkPopulateEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync
import java.io.File
import java.io.FileInputStream


object ChunkLoadGenerate {
    fun placeStructure(key:String,loc:Location,schem:String,pedestal:Material){
        var clipboard: Clipboard
        var file= File("D:\\·þÎñ¶Ë´óÈ«\\ÐÇÔÆ¿Õµº\\1.19.2hpy¿Õµºv1.0.6\\plugins\\FastAsyncWorldEdit\\schematics\\${schem}.schem")
        val format = ClipboardFormats.findByFile(file)
        format!!.getReader(FileInputStream(file)).use { reader -> clipboard = reader.read() }
        StructureData.addStructure(key,clipboard,loc)
        clipboard.place(loc,pedestal)

    }
    fun shouldGenerate(key:String,loc:Location):Boolean{
        if(!File(ConfigObject.getScheme(key)).exists()){
            debug("file not exist")
            return false
        }
        if(!ConfigObject.getWorlds(key).contains(loc.world)){
            debug("world not include")
            return false
        }
        if(!ConfigObject.isHeight(key,loc)){
            debug("not height")
            return false
        }
        if(!ConfigObject.isBiome(key,loc.world!!.getBiome(loc))){
            debug("not biome")
            return false
        }
        if(!ConfigObject.isAwayFromSpawn(key,loc)){
            debug("not awayfromworld")
            return false
        }
        var disbet=ConfigObject.getDistBet(key)
        for (structure in StructureData.structures) {
            var p1=structure.pos1
            var p2=structure.pos2
            var p3=Location(p1.world,(p1.x+p2.x)/2,(p1.y+p2.y)/2,(p1.z+p2.z)/2)
            if(loc.distance(p3)<disbet){
                debug("not disbet")
                return false
            }
        }
        var limit=ConfigObject.getAmountLimit(key)
        var cnt=0
        StructureData.structures.forEach { if(it.key==key)cnt++ }
        if(cnt>limit&&limit!=-1)return false
        var bottom=ConfigObject.getBottom_material(key)
        if(!bottom.contains(loc.block.type)){
            debug("not bottom")
            return false
        }
        if(!ConfigObject.isChance(key)){
            debug("not chance")
            return false
        }
        return true

    }
    @SubscribeEvent
    fun createStructures(e:ChunkLoadEvent){
        if(ConfigObject.mode.equals("load",ignoreCase = true)){
            var world=BukkitAdapter.adapt(e.world)
            var x=e.chunk.x
            var z=e.chunk.z

        }
    }
    @SubscribeEvent
    fun createStructures(e:ChunkPopulateEvent){
        if(ConfigObject.mode.equals("populate",ignoreCase = true)){

        }
    }
}