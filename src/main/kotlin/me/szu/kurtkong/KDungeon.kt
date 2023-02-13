package me.szu.kurtkong

import com.fastasyncworldedit.bukkit.util.BukkitItemStack
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.util.formatting.text.TextComponent
import com.sk89q.worldedit.util.formatting.text.serializer.ComponentSerializer
import com.sk89q.worldedit.util.formatting.text.serializer.gson.GsonComponentSerializer
import me.szu.kurtkong.Generate.ChunkLoadGenerate
import me.szu.kurtkong.Generate.GenerateTaskScheduler
import me.szu.kurtkong.config.ConfigObject
import me.szu.kurtkong.intergrate.SpawnMythicMobs
import me.szu.kurtkong.ui.GuiForStructures.openStructureGUI
import me.szu.kurtkong.ui.GuiMain.openMainGui
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Sign
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import taboolib.common.platform.Plugin
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.command
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.asList
import taboolib.common.util.sync
import taboolib.common5.Mirror
import taboolib.module.chat.uncolored
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.inputBook
import taboolib.platform.util.isAir
import java.io.File
import java.io.FileInputStream
import kotlin.math.max


object KDungeon : Plugin() {
    var lazySpawn:BukkitTask?=null
    lateinit var plugin:JavaPlugin
    var generateTaskScheduler: GenerateTaskScheduler?=null
    lateinit var playerDetectTask: PlayerDetectTask

    // TODO: 2023/1/14 测试条件判断
    // TODO: 2023/1/15 fawe的接口会产生历史文件 需要去掉
    // TODO: 2023/1/16 还是没有解决历史文件的问题

    override fun onEnable() {
        plugin=BukkitPlugin.getInstance()
        playerDetectTask= PlayerDetectTask()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, playerDetectTask)
        regcmd()
        intergrate()
//        fun placeStructure(loc:Location,schem:String,pedestal:Material){
//            var clipboard: Clipboard
//            var file= File(schem)
//            if(!file.exists())return
//            val format = ClipboardFormats.findByFile(file)
//            format!!.getReader(FileInputStream(file)).use { reader -> clipboard = reader.read() }
//            clipboard.region.iterator().forEach {
//                val b=clipboard.getFullBlock(it)
//
//                if(BukkitAdapter.adapt(b.blockType)==Material.CHEST){
//                    FillChest(b, arrayOf(ItemStack(Material.DIRT)))
//                    debug(b.nbt!!.toString())
//                    clipboard.setBlock(it.x,it.y,it.z,b)
//                }
//
//            }
//
//            clipboard.place(loc,pedestal)
//            clipboard.close()
//        }
//
//        placeStructure(Bukkit.getPlayer("Kurt_Kong")!!.location,"D:\\服务端大全\\星云空岛\\1.19.2hpy空岛v1.0.6\\plugins\\FastAsyncWorldEdit\\schematics\\t2.schem",Material.DIRT)

    }
    fun intergrate(){
        playerDetectTask.registerProcessor("[mm]",SpawnMythicMobs.processor)
    }

    fun regcmd(){
        command("KDungeon", aliases = listOf("kd")){
            literal("place"){
                dynamic {
                    suggestion<Player>(uncheck = true){sender, context ->
                     return@suggestion  ConfigObject.config.getConfigurationSection("Structures")!!.getKeys(false).asList()
                    }
                   execute<Player>{
                        sender, context, argument ->
                        var loc =sender.location
                        submitAsync {
                            if(ChunkLoadGenerate.shouldGenerate(argument,loc)){
                                ChunkLoadGenerate.placeStructure(argument,loc,ConfigObject.getScheme(argument),ConfigObject.getPedestal_Material(argument))
                            }
                        }

                   }
                }
            }
            literal("loop"){
                execute<CommandSender>{
                    sender, context, argument ->
                    Bukkit.getScheduler().runTaskAsynchronously(KDungeon.plugin, Runnable { while (true){} })

                }
            }
            literal("start"){
                dynamic("线程数") {
                    execute<Player>{
                        sender, context, argument ->
                        argument.toIntOrNull()?.let {
                            if(generateTaskScheduler!=null && !generateTaskScheduler!!.isStop)return@execute
                           generateTaskScheduler= GenerateTaskScheduler(it)
                        sender.info("启动完成")
                        }

                    }
                }
            }
            literal("mirror"){
                execute<ProxyCommandSender>(){
                    sender, context, argument ->

                    Mirror.report(sender)

                }
            }
            literal("check"){
                execute<Player>{
                    sender, context, argument ->
                    StructureData.structures.forEach {
                        its->
                        StructureData.structures.forEach {
                            it->
                            if(its.originLocation.distance(it.originLocation)>0 &&its.originLocation.distance(it.originLocation)  <max(ConfigObject.getDistBet(it.key),ConfigObject.getDistBet(its.key))){
                                debug("$its  \n $it")
                            }
                        }

                    }
                    sender.sendMessage("tmpSize:"+StructureData.tmp.size.toString()+  "   structuresSize:"+StructureData.structures.size )
                }
            }
            literal("stop"){
                execute<Player>{
                    sender, context, argument ->
                    generateTaskScheduler?.stop()
                    sender.info("已停止")
                }
            }
            literal("queue"){
                execute<Player>{
                    sender, context, argument ->
                    sender.sendMessage("queue: $generateTaskScheduler")

                }
            }
            literal("menu"){
                execute<Player>{
                    sender, context, argument ->
                    sender.openMainGui()//.openStructureGUI(StructureData.structures)

                }
            }

            literal("sign"){
                execute<Player>{
                    sender, context, argument ->
                    sender.inputBook("输入木牌字条",true, emptyList()){
                        if(it.isEmpty())return@inputBook
                        var res= sender.rayTraceBlocks(10.0)
                        var block: Block = res?.hitBlock ?: return@inputBook
                        block.location.clone().add(0.0,1.0,0.0).block.type = Material.OAK_SIGN
                        block=block.location.clone().add(0.0,1.0,0.0).block
                        var sign=block.state as Sign
                        //debug(it.toString())
                        it.forEachIndexed { index, s ->sign.setLine(index,s.uncolored())  }
                        for (line in sign.lines) {
                         //   debug(line)
                        }
                        sign.update(true)
                        block.blockData = sign.blockData
                        block.world.setBlockData(block.location,sign.blockData)
                    }

                }
            }

        }
    }

    override fun onDisable() {
        generateTaskScheduler?.stop()
    }
}