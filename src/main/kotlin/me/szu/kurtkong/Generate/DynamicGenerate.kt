package me.szu.kurtkong.Generate

import me.szu.kurtkong.KDungeon
import me.szu.kurtkong.config.ConfigObject
import me.szu.kurtkong.debug
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

                val points= generatePoints1(loc)

                KDungeon.generateTaskScheduler?.submit(){ generate(points) }
            }
        },0,ConfigObject.interval.toLong())

    }
    @Awake(LifeCycle.DISABLE)
    fun disable(){
        task?.cancel()
    }
    fun generatePoints(loc:Location):ArrayList<Location>{
        val offset=Bukkit.getViewDistance()*16
        val maxp=loc.clone().add(offset.toDouble(),0.0,offset.toDouble())
        val minp=loc.clone().add(-offset.toDouble(),0.0,-offset.toDouble())
        val points=ArrayList<Location>(ConfigObject.points.toInt())
        for (i in 1 .. ConfigObject.points.toInt()){
            points.add(Location(loc.world!!, random(minp.x,maxp.x),0.0, random(minp.z,maxp.z)))
        }
        return points
    }
    fun generatePoints1(loc:Location):ArrayList<Location>{
        val offset=(Bukkit.getViewDistance()*16 ).toDouble()

        val maxp=loc.clone().add(offset,0.0,offset)
        val minp=loc.clone().add(-offset,0.0,-offset)
        val points=ArrayList<Location>(ConfigObject.points.toInt())
        var halfoffset=offset/2.0
        for (i in 1 .. ConfigObject.points.toInt()){
            //points.add(Location(loc.world!!, random(minp.x,maxp.x),0.0, random(minp.z,maxp.z)))
            while (true){
                var x=random(minp.x,maxp.x)
                var z=random(minp.z,maxp.z)

                if((x in loc.x-halfoffset..loc.x+halfoffset )&&( z in loc.z-halfoffset .. loc.z+halfoffset)){
                    continue
                }
                else {
                    points.add(Location(loc.world!!,x,0.0,z))
                    break
                }
            }

        }
       // debug(points.toString())
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