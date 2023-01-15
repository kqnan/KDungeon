package me.szu.kurtkong.Generate

import me.szu.kurtkong.KDungeon
import me.szu.kurtkong.config.ConfigObject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.scheduler.BukkitTask
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

import taboolib.common.util.random
import taboolib.common.util.sync
import taboolib.platform.util.onlinePlayers

object DynamicGenerate {
    private var task: BukkitTask?=null
    @Awake(LifeCycle.ACTIVE)
    fun run(){
        if(task!=null){
            task?.cancel()
        }
        task= Bukkit.getScheduler().runTaskTimerAsynchronously(KDungeon.plugin, Runnable {
             if(!ConfigObject.mode.equals("dynamic",ignoreCase = true)) return@Runnable
            if (KDungeon.generateTaskScheduler == null) {
                return@Runnable
            }
             for (onlinePlayer in onlinePlayers) {
                val loc= sync {
                    onlinePlayer.location
                }

                val points= generatePoints(loc)

                KDungeon.generateTaskScheduler?.submit(){ generate(points) }
            }
        },0,ConfigObject.interval.toLong())
    }
    @Awake(LifeCycle.DISABLE)
    fun disable(){
        task?.cancel()
    }
    fun generatePoints(loc:Location):ArrayList<Location>{
        var offset=Bukkit.getViewDistance()*16
        var maxp=loc.clone().add(offset.toDouble(),0.0,offset.toDouble())
        var minp=loc.clone().add(-offset.toDouble(),0.0,-offset.toDouble())
        var points=ArrayList<Location>(ConfigObject.points.toInt())
        for (i in 1 .. ConfigObject.points.toInt()){
            points.add(Location(loc.world!!, random(minp.x,maxp.x),0.0, random(minp.z,maxp.z)))
        }
        return points
    }

    private fun generate(points:ArrayList<Location>){
        var keys=ConfigObject.config.getConfigurationSection("Structures")!!.getKeys(false)
        for (point in points) {
            var minh=point.world!!.minHeight
            var maxh=point.world!!.maxHeight
            for(y in minh.. maxh){
                var loc=Location(point.world,point.x,y.toDouble(),point.z)
                for (key in keys) {
                    if(ChunkLoadGenerate.shouldGenerate(key,loc)){
                        ChunkLoadGenerate.placeStructure(key,loc,ConfigObject.getScheme(key),ConfigObject.getPedestal_Material(key))
                    }
                }
            }

        }
    }
}