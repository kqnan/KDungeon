package me.szu.kurtkong

import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operation
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import taboolib.common.platform.event.SubscribeEvent
import java.io.File
import java.io.FileInputStream


object ChunkLoad {
    fun placeStructure(loc:Location,schem:String,pedestal:Material){
        var clipboard: Clipboard
        var file= File("D:\\·þÎñ¶Ë´óÈ«\\ÐÇÔÆ¿Õµº\\1.19.2hpy¿Õµºv1.0.6\\plugins\\FastAsyncWorldEdit\\schematics\\${schem}.schem")
        val format = ClipboardFormats.findByFile(file)
        format!!.getReader(FileInputStream(file)).use { reader -> clipboard = reader.read() }
        StructureData.addStructure(clipboard,loc)
        clipboard.place(loc,pedestal)


    }
    @SubscribeEvent
    fun createStructures(){

    }
}