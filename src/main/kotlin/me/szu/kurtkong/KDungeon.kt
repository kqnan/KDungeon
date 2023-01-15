package me.szu.kurtkong

import me.szu.kurtkong.Generate.ChunkLoadGenerate
import me.szu.kurtkong.Generate.GenerateTaskScheduler
import me.szu.kurtkong.config.ConfigObject
import me.szu.kurtkong.intergrate.SpawnMythicMobs
import me.szu.kurtkong.ui.GuiForStructures.openStructureGUI
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Sign
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import taboolib.common.platform.Plugin
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.command
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.asList
import taboolib.common.util.sync
import taboolib.common5.Mirror
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.inputBook


object KDungeon : Plugin() {
    var lazySpawn:BukkitTask?=null
    lateinit var plugin:JavaPlugin
    var generateTaskScheduler: GenerateTaskScheduler?=null
    lateinit var playerDetectTask: PlayerDetectTask

    // TODO: 2023/1/14 测试条件判断
    // TODO: 2023/1/15 fawe的接口会产生历史文件 需要去掉

    override fun onEnable() {
        plugin=BukkitPlugin.getInstance()
        playerDetectTask= PlayerDetectTask()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, playerDetectTask)
        regcmd()
        // generateTaskScheduler= GenerateTaskScheduler(10)
        intergrate()


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
                            if(its.originLocation.distance(it.originLocation)>0 &&its.originLocation.distance(it.originLocation)  <ConfigObject.getDistBet(it.key) && its.key==it.key){
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
                    sender.openStructureGUI()

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
                        debug(it.toString())
                        it.forEachIndexed { index, s ->sign.setLine(index,s)  }
                        for (line in sign.lines) {
                            debug(line)
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