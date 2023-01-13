package me.szu.kurtkong

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.world.chunk.Chunk
import me.szu.kurtkong.config.ConfigObject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkPopulateEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync
import taboolib.common5.cbool
import java.io.File
import java.io.FileInputStream


object ChunkLoadGenerate {
    fun placeStructure(key:String,loc:Location,schem:String,pedestal:Material){
        var clipboard: Clipboard
        var file= File(schem)
        val format = ClipboardFormats.findByFile(file)
        format!!.getReader(FileInputStream(file)).use { reader -> clipboard = reader.read() }
        StructureData.addStructure(key,clipboard,loc)
        clipboard.place(loc,pedestal)

    }
    fun shouldGenerate(key:String,loc:Location):Boolean{
        if(!ConfigObject.isChance(key)){
            debug("not chance")
            return false
        }
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
            debug("not awayfromworld ${loc.world!!.spawnLocation}")
            return false
        }
        var disbet=ConfigObject.getDistBet(key)
        for (structure in StructureData.structures) {
            var p1=structure.pos1
            var p2=structure.pos2
            var p3=Location(p1.world,(p1.x+p2.x)/2.0,(p1.y+p2.y)/2.0,(p1.z+p2.z)/2.0)
            if(loc.world!!.name==p3.world!!.name&&loc.distance(p3)<disbet){
                debug("not disbet")
                return false
            }
        }
        var limit=ConfigObject.getAmountLimit(key)
        var cnt=0
        StructureData.structures.forEach { if(it.key==key)cnt++ }
        if(cnt>=limit&&limit!=-1) {
            debug("not limit")
            return false
        }
        var bottomlist=ConfigObject.getBottom_material(key)
        var bottomMaterial=ArrayList<Material>()
        if(!bottomlist.contains("all")&&!bottomlist.contains("ALL")&& bottomlist.isNotEmpty()){
            bottomlist.forEach {
                try{

                    bottomMaterial.add(Material.valueOf(it.uppercase()))}catch (e:Exception){}
            }
            if(!bottomMaterial.contains(loc.block.type)){

                debug("${loc.block.type}, not bottom")
           //     debug(loc.toString())
                return false
            }
        }
        debug("Éú³É${key}: ${loc.x} ${loc.y} ${loc.z} ")
        return true

    }
     fun generate(chunk:org.bukkit.Chunk){
        var x = chunk.x.shl(4)
        var z = chunk.z.shl(4)
        var world=BukkitAdapter.adapt(chunk.world)
        var maxY = world.maxY
        var minY = world.minY
        var keys=ConfigObject.config.getConfigurationSection("Structures")!!.getKeys(false)
        for (key in keys) {
            for (k in minY .. maxY) {
                for (i in 0..15) {
                    for (j in 0..15) {

                        var loc = Location(chunk.world, (x + i).toDouble(), k.toDouble(), (z + j).toDouble())
                      //  debug("${loc.blockX}  ${loc.blockY} ${loc.blockZ}")
                        if (shouldGenerate(key, loc)) {
                            placeStructure(
                                key,
                                loc,
                                ConfigObject.getScheme(key),
                                ConfigObject.getPedestal_Material(key)
                            )

                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun createStructures(e:ChunkLoadEvent) {
        if (ConfigObject.mode.equals("load", ignoreCase = true)) {
            KDungeon.generateTaskScheduler?.submit {
                generate(e.chunk)
            }
        }
    }
    @SubscribeEvent
    fun createStructures(e:ChunkPopulateEvent){
        if(ConfigObject.mode.equals("populate",ignoreCase = true)){
        }
    }
}