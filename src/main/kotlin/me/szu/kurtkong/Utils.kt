package me.szu.kurtkong

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.BlockPosition
import com.comphenix.protocol.wrappers.WrappedBlockData
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.function.operation.Operation
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.util.nbt.CompoundBinaryTag
import com.sk89q.worldedit.util.nbt.ListBinaryTag
import com.sk89q.worldedit.world.block.BaseBlock
import de.tr7zw.changeme.nbtapi.*
import de.tr7zw.changeme.nbtapi.utils.GsonWrapper
import de.tr7zw.changeme.nbtapi.utils.ReflectionUtil
import me.szu.kurtkong.config.ConfigObject
import me.szu.kurtkong.config.ItemsObject
import net.sourceforge.pinyin4j.PinyinHelper
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.info
import taboolib.common.util.random
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.library.xseries.getItemStack
import taboolib.module.chat.colored
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import kotlin.math.max
import kotlin.math.min

fun debug( str:String){
    if(ConfigObject.debug.booleanValue()){
        info(str)
        taboolib.platform.util.onlinePlayers.forEach { if(it.isOp)it.sendMessage(str) }
    }
}
fun Location.toBlockVector3():BlockVector3{
    return BlockVector3.at(this.x,this.y,this.z)
}
fun Player.info(str:String){
    this.sendMessage("&a[KDungeon]$str".colored())
}
fun Player.warn(str:String){
    this.sendMessage("&6[KDungeon]$str".colored())
}
fun String.toPinYin():String{
    val sb = StringBuilder()
    for (i in 0 until this.length) {
        val ch: Char = this.get(i)
        val s = PinyinHelper.toHanyuPinyinStringArray(ch)
        if (s != null) {
            sb.append(s[0][0])
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}
fun Location.containWithin(loc1:Location,loc2:Location):Boolean{
    if(this.world!!.name!=loc1.world!!.name)return false
    if(this.x>=min(loc1.x,loc2.x)&&this.x<=max(loc1.x,loc2.x)){
        if(this.y>=min(loc1.y,loc2.y)&&this.y<=max(loc1.y,loc2.y)){
            if(this.z>=min(loc1.z,loc2.z)&&this.z<=max(loc1.z,loc2.z)){
                return true
            }
        }
    }
    return false
}
fun BlockVector3.toBukkit(world: World):Location{
    return BukkitAdapter.adapt(world,this)
}

fun GetLoots(key:String):Array<ItemStack>{
    val list=ArrayList<ItemStack>()
    for (lootKey in (ConfigObject.config.getConfigurationSection("Structures.${key}.loot")?:return list.toTypedArray()).getKeys(false)) {
        val chance=ConfigObject.config.getInt("Structures.${key}.loot.${lootKey}",0)
        if(random(1,100)<chance){
            ItemsObject.items.getItemStack(lootKey)?.let { list.add(it) }
        }
    }
    return list.toTypedArray()
}
fun FillChest(chest:BaseBlock,item:Array<ItemStack> ){
    val chestNBT=chest.nbtReference?:return
    val listtag=ListBinaryTag.builder()
    for ( i in item.indices){
        if(i>26)break
        val itemnbt=CompoundBinaryTag.builder()
        itemnbt.putByte("Slot",i.toByte())
        itemnbt.putString("id",item[i].type.key.toString())
        itemnbt.putByte("Count",item[i].amount.toByte())
        BukkitAdapter.adapt(item[i]).nbt?.let { itemnbt.put("tag", it) }
        listtag.add(itemnbt.build())
    }
    val newnbt=chestNBT.value.put("Items",listtag.build())
    chestNBT.setProperty("value",newnbt)
    //debug(chestNBT.value.toString())

}
fun Clipboard.place(loc: Location,pedestal:Material){
    this.region.iterator().forEach {
        if(this.getFullBlock(it).blockType==BukkitAdapter.asBlockType(Material.BEDROCK)){//??????????????????????
            var world=BukkitAdapter.adapt(loc.world)

            var block=world.getBlock(loc.toBlockVector3().add(it.subtract(this.origin)))//??????????????????=??????????+????????????

            if(!BukkitAdapter.adapt(block.blockType).isSolid){//????????????????????????????
                this.setBlock(it.x,it.y,it.z,BukkitAdapter.asBlockType(pedestal)!!.applyBlock(it))//??????????????????????
            }
            else {
                this.setBlock(it.x,it.y,it.z,BukkitAdapter.asBlockType(Material.BARRIER)!!.applyBlock(it))
            }
        }
    }


    var editSession=WorldEdit.getInstance().newEditSession((BukkitAdapter.adapt(loc.world).disableHistory() as com.sk89q.worldedit.world.World))

    val operation:Operation=ClipboardHolder(this)
        .createPaste(editSession)
        .to(BlockVector3.at(loc.x,loc.y,loc.z))
        .filter {
            return@filter  BukkitAdapter.adapt(this.getFullBlock(it).blockType)!=Material.BARRIER  }
            //????barrier????????
        .build()
    Operations.complete(operation)
    operation.cancel()
    this.flush()
    this.close()
    editSession.flushQueue()
    editSession.close()
}
fun HideBlock(loc:Location,player: Player){
    var packet=PacketContainer(PacketType.Play.Server.BLOCK_CHANGE)
    packet.blockPositionModifier.write(0, BlockPosition(loc.blockX,loc.blockY,loc.blockZ))
    packet.blockData.write(0, WrappedBlockData.createData(Material.AIR))
    ProtocolLibrary.getProtocolManager().sendServerPacket(player,packet)
}
