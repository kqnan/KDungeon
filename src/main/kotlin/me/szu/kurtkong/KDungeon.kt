package me.szu.kurtkong

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import taboolib.common.platform.Plugin
import taboolib.common.platform.command.command
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.sync
import taboolib.platform.BukkitPlugin


object KDungeon : Plugin() {
    var lazySpawn:BukkitTask?=null
    lateinit var plugin:JavaPlugin
    override fun onEnable() {
        plugin=BukkitPlugin.getInstance()

        lazySpawn=Bukkit.getScheduler().runTaskAsynchronously(plugin, PlayerDetectTask())
        regcmd()

    }
    fun regcmd(){
        command("KDungeon", aliases = listOf("kd")){
            literal("place"){
                execute<Player>{
                    sender, context, argument ->
                    submitAsync { ChunkLoadGenerate.placeStructure("test",sync { sender.location }.clone().add(5.0,0.0,5.0),"test", Material.DIAMOND_BLOCK)
                    }
                }
            }
        }
    }
}