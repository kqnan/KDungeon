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

// TODO: 2023/1/14  开服第一次开启调度器，任务会很爆满。但是当关了再开，就不会卡了。
// TODO: 2023/1/14 调度器启动时系统会非常卡

object ChunkLoadGenerate {
    fun placeStructure(key:String,loc:Location,schem:String,pedestal:Material){
        var clipboard: Clipboard
        var file= File(schem)
        if(!file.exists())return
        val format = ClipboardFormats.findByFile(file)
        format!!.getReader(FileInputStream(file)).use { reader -> clipboard = reader.read() }
        if(!StructureData.addStructure(key,clipboard,loc)) {
            clipboard.close()
            return
        }
        clipboard.place(loc,pedestal)
        debug("生成${key}: ${loc.x} ${loc.y} ${loc.z} ")
    }

    fun shouldGenerate(key:String,loc:Location):Boolean{

        if(!ConfigObject.isChance(key)){
            //  debug("not chance")

            return false
        }
        var bottomlist=ConfigObject.getBottom_material(key)
        // var bottomMaterial=ArrayList<Material>()
        if(!bottomlist.contains("all")&&!bottomlist.contains("ALL")&& bottomlist.isNotEmpty()){

            var has=false
            bottomlist.forEach {
                has=it.uppercase()==loc.block.type.name
            }
            if(!has) {

                return false
            }

        }
        if(!ConfigObject.getWorlds(key).contains(loc.world)){
            //  debug("world not include")
            return false
        }
        if(!ConfigObject.isHeight(key,loc)){
            //  debug("not height")
            return false
        }
        if(!ConfigObject.isBiome(key,loc.world!!.getBiome(loc))){
            //   debug("not biome")

            return false
        }
        if(!ConfigObject.isAwayFromSpawn(key,loc)){
            //  debug("not awayfromworld ${loc.world!!.spawnLocation}")

            return false
        }
        var disbet=ConfigObject.getDistBet(key)
        for (structure in StructureData.structures) {
            var p1=structure.pos1
            var p2=structure.pos2
            var p3=structure.originLocation//Location(p1.world,(p1.x+p2.x)/2.0,(p1.y+p2.y)/2.0,(p1.z+p2.z)/2.0)
            if(loc.world!!.name==p3.world!!.name&&loc.distance(p3)<disbet&&key==structure.key){

                return false
            }
        }
        var limit=ConfigObject.getAmountLimit(key)
        var cnt=0
        StructureData.structures.forEach { if(it.key==key)cnt++ }
        if(cnt>=limit&&limit!=-1) {
            //  debug("not limit")

            return false
        }



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
            out@for (k in minY .. maxY) {

                for (i in 0..15) {
                    for (j in 0..15) {

                        var loc = Location(chunk.world, (x + i).toDouble(), k.toDouble(), (z + j).toDouble())
                        if(!ConfigObject.isHeight(key,loc)){
                            continue@out  //如果高度不对则立刻跳出
                        }

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
            KDungeon.generateTaskScheduler?.submit {
                generate(e.chunk)
            }
        }
    }
}