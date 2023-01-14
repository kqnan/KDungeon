package me.szu.kurtkong

import me.szu.kurtkong.config.ConfigObject
import me.szu.kurtkong.intergrate.SpawnMythicMobs
import me.szu.kurtkong.ui.GuiForStructures.openStructureGUI
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import taboolib.common.platform.Plugin
import taboolib.common.platform.command.command
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.asList
import taboolib.platform.BukkitPlugin


object KDungeon : Plugin() {
    var lazySpawn:BukkitTask?=null
    lateinit var plugin:JavaPlugin
    var generateTaskScheduler: GenerateTaskScheduler?=null
    lateinit var playerDetectTask: PlayerDetectTask
    override fun onEnable() {
        plugin=BukkitPlugin.getInstance()
        playerDetectTask= PlayerDetectTask()
        Bukkit.getScheduler().runTaskAsynchronously(plugin, playerDetectTask)
        regcmd()
        // generateTaskScheduler= GenerateTaskScheduler(10)
        intergrate()
        var loc1=Location(Bukkit.getWorld("test_1"),-288.0,63.0,240.0)
        var loc2=Location(Bukkit.getWorld("test_1"),-288.0,62.0,219.0)
     //   debug(loc1.distance(loc2).toString())
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
                           generateTaskScheduler=GenerateTaskScheduler(it)

                        sender.info("启动完成")
                        }

                    }
                }
            }
            literal("check"){
                execute<Player>{
                    sender, context, argument ->
                    StructureData.structures.forEach {
                        its->
                        StructureData.structures.forEach {
                            it->
                            sender.sendMessage(its.pos1.distance(it.pos1).toString())
                        }
                    }
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

        }
    }

    override fun onDisable() {
        generateTaskScheduler?.stop()
    }
}