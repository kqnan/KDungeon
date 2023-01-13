package me.szu.kurtkong

import com.sk89q.worldedit.bukkit.BukkitWorld
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import taboolib.common.platform.Awake
import taboolib.common.platform.Plugin
import taboolib.common.platform.command.command
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.sync
import taboolib.platform.BukkitPlugin


object KDungeon : Plugin() {
    var lazySpawn:BukkitTask?=null
    lateinit var plugin:JavaPlugin
    lateinit var generateTaskScheduler: GenerateTaskScheduler
    override fun onEnable() {
        plugin=BukkitPlugin.getInstance()

        lazySpawn=Bukkit.getScheduler().runTaskAsynchronously(plugin, PlayerDetectTask())
        regcmd()

    // generateTaskScheduler= GenerateTaskScheduler(10)
    }
    fun regcmd(){
        command("KDungeon", aliases = listOf("kd")){
            literal("place"){
                dynamic {
                   
                }
            }
            literal("queue"){
                execute<Player>{
                    sender, context, argument ->
                    sender.sendMessage("queue: $generateTaskScheduler")

                }
            }

        }
    }

    override fun onDisable() {
        generateTaskScheduler.stop()
    }
}