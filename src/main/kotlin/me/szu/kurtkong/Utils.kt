package me.szu.kurtkong

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.BlockPosition
import com.comphenix.protocol.wrappers.WrappedBlockData
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.bukkit.BukkitBlockRegistry.BukkitBlockMaterial
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.function.operation.Operation
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.world.block.BaseBlock
import com.sk89q.worldedit.world.block.BlockState
import com.sk89q.worldedit.world.block.BlockType
import com.sk89q.worldedit.world.registry.BlockMaterial
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.common.platform.function.onlinePlayers
import taboolib.module.chat.colored
import kotlin.math.max
import kotlin.math.min
fun debug( str:String){
    info(str)
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
fun Location.containWithin(loc1:Location,loc2:Location):Boolean{
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
fun Clipboard.place(loc: Location,pedestal:Material){
    this.region.iterator().forEach {
        if(this.getFullBlock(it).blockType==BukkitAdapter.asBlockType(Material.BEDROCK)){//原理图的这个位置是基岩
            var world=BukkitAdapter.adapt(loc.world)

            var block=world.getBlock(loc.toBlockVector3().add(it.subtract(this.origin)))//获取现实的这个位置=放置的位置+原理图的位置

            if(!BukkitAdapter.adapt(block.blockType).isSolid){//若不是固体方块，则替换为底座
                this.setBlock(it.x,it.y,it.z,BukkitAdapter.asBlockType(pedestal)!!.applyBlock(it))//在剪切板的位置替换底座
            }
            else {
                this.setBlock(it.x,it.y,it.z,BukkitAdapter.asBlockType(Material.BARRIER)!!.applyBlock(it))
            }
        }
    }


    var editSession=WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(loc.world))
    val operation:Operation=ClipboardHolder(this)
        .createPaste(editSession)
        .to(BlockVector3.at(loc.x,loc.y,loc.z))
        .filter {
            return@filter  BukkitAdapter.adapt(this.getFullBlock(it).blockType)!=Material.BARRIER  }
            //所有barrier都不放置
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