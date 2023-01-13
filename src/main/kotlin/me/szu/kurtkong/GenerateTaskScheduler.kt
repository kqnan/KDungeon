package me.szu.kurtkong

import net.minecraft.world.level.entity.ChunkEntities
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.ChunkSnapshot
import org.bukkit.scheduler.BukkitTask
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

class GenerateTaskScheduler {

    private val threads=CopyOnWriteArrayList<Task>()
    constructor(thread: Int){
        for (i in 1 .. thread){
            var t=Task()
            threads.add(t)
            Bukkit.getScheduler().runTaskAsynchronously(KDungeon.plugin,t)
        }

    }
    fun stop(){
        threads.forEach {
            it.isStop=true
            it.queue.clear()
        }
    }
    override fun toString(): String {
        var s=java.lang.StringBuilder()
        threads.forEachIndexed{
            index, task ->
            s.append("${index}: ${task.queue.size} \n")

        }
        return s.toString()
    }
    fun submit( task:Function){
        var idx=0
        var minn=Int.MAX_VALUE
        threads.forEachIndexed { index, Task ->
            if(minn>Task.queue.size){
                minn=Task.queue.size
                idx=index
            }
        }
        threads.get(idx).queue.add(task)
    }
    class Task :Runnable{
        val  queue=ConcurrentLinkedQueue<Function>()
        var isStop=false
        override fun run() {
            while (!isStop){
                if(queue.isNotEmpty()){
                    var f=queue.poll()
                    try {
                        f.apply()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }


    }

}