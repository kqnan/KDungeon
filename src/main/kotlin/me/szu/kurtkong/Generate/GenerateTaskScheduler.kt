package me.szu.kurtkong.Generate

import me.szu.kurtkong.KDungeon
import me.szu.kurtkong.debug
import me.szu.kurtkong.lambdaFunc.Function
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.scheduler.BukkitTask
import taboolib.common5.mirrorNow
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList

class GenerateTaskScheduler {

    private val threads=CopyOnWriteArrayList<Task>()
    private val bukkitTasks=ArrayList<BukkitTask>()
     var isStop=false
    private val tmp=CopyOnWriteArrayList<Location>()
    constructor(thread: Int){

        for (i in 1 .. thread){
            var t= Task()
            bukkitTasks.add(Bukkit.getScheduler().runTaskAsynchronously(KDungeon.plugin,t))
            threads.add(t)
        }

    }

    fun stop(){
        isStop=true
        threads.forEach {
            it.isStop=true
            it.queue.clear()
        }
        bukkitTasks.forEach { it.cancel() }
        tmp.clear()
    }

    override fun toString(): String {
        var s=java.lang.StringBuilder()
        threads.forEachIndexed{
            index, task ->
            s.append("${index}: ${task.queue.size} \n")
        }
        bukkitTasks.forEach {
            s.append("running: ${Bukkit.getScheduler().isCurrentlyRunning(it.taskId)}\n")
        }
        return s.toString()
    }
    fun submit( task: Function){
        if(isStop)return

        var idx=0
        var minn=Int.MAX_VALUE
        threads.forEachIndexed { index, Task ->
            if(minn>Task.queue.size){
                minn=Task.queue.size
                idx=index
            }
        }
        if(!threads.get(idx).isStop)threads.get(idx).queue.add(task)
    }
    class Task :Runnable{
        val  queue=ConcurrentLinkedQueue<Function>()
        var isStop=false
        override fun run() {
            debug("?߳̿?ʼ")
            while (!isStop){
                if(queue.isNotEmpty()){
                    var f=queue.poll()
                    mirrorNow("apply"){
                        try {
                            f.apply()
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }
            debug("?߳???ֹ")
        }


    }

}