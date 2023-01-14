package me.szu.kurtkong

import com.sk89q.worldedit.bukkit.BukkitWorld
import io.lumine.mythic.bukkit.utils.Commands
import me.szu.kurtkong.config.ConfigObject
import me.szu.kurtkong.intergrate.SpawnMythicMobs
import me.szu.kurtkong.ui.GuiForStructures.openStructureGUI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import taboolib.common.platform.Awake
import taboolib.common.platform.Plugin
import taboolib.common.platform.command.command
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.asList
import taboolib.common.util.sync
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
                dynamic("�߳���") {
                    execute<Player>{
                        sender, context, argument ->
                        argument.toIntOrNull()?.let { generateTaskScheduler=GenerateTaskScheduler(it)
                        sender.info("�������")
                        }

                    }
                }
            }
            literal("stop"){
                execute<Player>{
                    sender, context, argument ->
                    generateTaskScheduler?.stop()
                    sender.info("��ֹͣ")
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