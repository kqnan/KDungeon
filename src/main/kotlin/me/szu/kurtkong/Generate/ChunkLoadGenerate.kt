package me.szu.kurtkong.Generate

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import me.szu.kurtkong.KDungeon
import me.szu.kurtkong.StructureData
import me.szu.kurtkong.config.ConfigObject
import me.szu.kurtkong.debug
import me.szu.kurtkong.place
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.world.ChunkPopulateEvent

import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.mirrorNow
import java.io.File
import java.io.FileInputStream




object ChunkLoadGenerate {
    fun placeStructure(key:String,loc:Location,schem:String,pedestal:Material){
        var clipboard: Clipboard
        var file= File(schem)
        if(!file.exists())return
        val format = ClipboardFormats.findByFile(file)
        format!!.getReader(FileInputStream(file)).use { reader -> clipboard = reader.read() }
        if(!StructureData.addStructure(key, clipboard, loc)) {
            clipboard.close()
            return
        }
        clipboard.place(loc,pedestal)
        debug("生成${key}: ${loc.x} ${loc.y} ${loc.z} ")
    }

    fun shouldGenerate(key:String,loc:Location):Boolean{
        //debug(ConfigObject.isChance(key).toString())
        if(!ConfigObject.isChance(key)){
              //debug("not chance")
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
    private fun generate(chunk:org.bukkit.Chunk){
        mirrorNow("generate"){
            val x = chunk.x.shl(4)
            val z = chunk.z.shl(4)
            val world=BukkitAdapter.adapt(chunk.world)
            val maxY = world.maxY
            val minY = world.minY
            val keys=ConfigObject.config.getConfigurationSection("Structures")!!.getKeys(false)
            key@for (key in keys) {
                if(ConfigObject.config.getInt("Structures.${key}.chance")<=0)continue


                out@for (k in minY .. maxY) {

                    for (i in 0..15) {
                        for (j in 0..15) {

                            var loc = Location(chunk.world, (x + i).toDouble(), k.toDouble(), (z + j).toDouble())
                            if(!ConfigObject.isHeight(key,loc)){
                                continue@out  //如果高度不对则立刻跳出
                            }
                            if(!ConfigObject.getWorlds(key).contains(loc.world)){
                                //  debug("world not include")
                                continue@key
                            }
                            var limit=ConfigObject.getAmountLimit(key)
                            var cnt=0
                            StructureData.structures.forEach { if(it.key==key)cnt++ }
                            if(cnt>=limit&&limit!=-1) {
                                //  debug("not limit")

                                continue@key
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