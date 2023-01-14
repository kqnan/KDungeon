package me.szu.kurtkong.config

import com.sk89q.worldedit.bukkit.BukkitWorld
import me.szu.kurtkong.debug
import me.szu.kurtkong.info
import me.szu.kurtkong.warn
import org.bukkit.*
import org.bukkit.block.Biome
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.info
import taboolib.common.util.random
import taboolib.common5.FileWatcher
import taboolib.library.xseries.getItemStack
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration
import taboolib.platform.util.onlinePlayers
import java.io.File

object ConfigObject {
    @Config
    lateinit var config:Configuration
    @ConfigNode(value="mode")
    lateinit var mode:String
    @ConfigNode(value = "debug")
    lateinit var debug:java.lang.Boolean
    @ConfigNode(value = "hide")
    lateinit var hide:java.lang.Double
    private val  path="plugins/KDungeon/config.yml"
    init {
        FileWatcher.INSTANCE.addSimpleListener(File(path), Runnable {
            config= Configuration.loadFromFile(File(path))
            mode= config.getString("load","populate")!!
            for (key in config.getConfigurationSection("Structures")!!.getKeys(false)) {
                var schema= config.getString("Structures.${key}.schema")
                if(!File(schema).exists()){
                    onlinePlayers.forEach { if(it.isOp){
                        it.warn("路径: $schema 无效")
                    }
                    }
                }
            }
            onlinePlayers.forEach { if(it.isOp){
                it.info("自动重载完成")
            }
            }
        })
    }
    fun getIcon(key:String):ItemStack{
        return config.getItemStack("Structures.${key}.icon")?:ItemStack(Material.PAPER)
    }
    fun getScheme(key: String):String{
        return config.getString("Structures.${key}.schema","")!!
    }
    fun isAwayFromSpawn(key:String, loc:Location):Boolean{
        var tmp= config.getString("Structures.${key}.awayFromSpawn")!!.removePrefix("[").removeSuffix("]").split(",")

        var min=tmp[0].toDouble()
        var max=tmp[1].toDouble()
        var spawn=loc.world!!.spawnLocation.clone()
        spawn.y=0.0
        var loc1=loc.clone()
        loc1.y=0.0
        return loc1.distance(spawn) in min..max
    }
    fun getDistBet(key:String):Double{
        return config.getDouble("Structures.${key}.distanceBetween")
    }
    fun getAmountLimit(key:String):Int{
        return config.getInt("Structures.${key}.amountLimit")
    }
    fun getPedestal_Material(key:String):Material{
        return Material.valueOf(config.getString("Structures.${key}.pedestal_material","AIR")!!.uppercase())
    }

    fun getBottom_material(key: String):List<String>{

        return config.getStringList("Structures.${key}.bottom_material")
    }
    fun isChance(key: String):Boolean{
        return random(0, 100)<=config.getInt("Structures.${key}.chance")
    }
    fun getWorlds(key:String):ArrayList<World>{
       var w= config.getStringList("Structures.${key}.world")
        var worlds=ArrayList<World>()
        for (s in w) {
            Bukkit.getWorld(s)?.let { worlds.add(it) }
        }
        return worlds
    }
    fun isBiome(key: String,biome: Biome):Boolean{
        var b= config.getStringList("Structures.${key}.biomes")
        if(b.isEmpty() ||b.contains("ALL")||b.contains("all"))return true
        return b.contains(biome.key.toString())
    }
    fun isHeight(key:String,loc:Location):Boolean{
        var h: String? = config.getString("Structures.${key}.height") ?: return  false
        var hh=h!!

        var hi=loc.world!!.getHighestBlockYAt(loc,HeightMap.WORLD_SURFACE)

      //  debug("${loc.blockY} ${hi} ${h.equals("surface",ignoreCase = true)}")
        if(hh.startsWith("[")&&hh.endsWith("]")){
            var tmp= hh.removePrefix("[").removeSuffix("]").split(",")
            var h1=tmp[0].toInt()
            var h2=tmp[1].toInt()
            return loc.blockY in h1..h2
        }
        else if(h.equals("sky",ignoreCase = true)){

            return loc.blockY>hi
        }
        else if(h.equals("surface",ignoreCase = true)){
            return loc.blockY==hi
        }
        else if(h.equals("underground",ignoreCase = true)){
            return loc.blockY<hi
        }
        return false
    }

}