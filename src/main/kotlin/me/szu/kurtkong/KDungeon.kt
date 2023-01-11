package me.szu.kurtkong

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operation
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import org.bukkit.Bukkit
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import java.io.File
import java.io.FileInputStream


object KDungeon : Plugin() {

    override fun onEnable() {
        var clipboard: Clipboard?=null
        var world=Bukkit.getWorld("world")
        var loc=Bukkit.getPlayer("Kurt_Kong")!!.location
        val format: ClipboardFormat? = ClipboardFormats.findByFile(File("D:\\·þÎñ¶Ë´óÈ«\\ÐÇÔÆ¿Õµº\\1.19.2hpy¿Õµºv1.0.6\\plugins\\FastAsyncWorldEdit\\schematics\\test.schem"))
        format!!.getReader(FileInputStream(File("D:\\·þÎñ¶Ë´óÈ«\\ÐÇÔÆ¿Õµº\\1.19.2hpy¿Õµºv1.0.6\\plugins\\FastAsyncWorldEdit\\schematics\\test.schem"))).use {
                reader -> clipboard = reader.read() }
        var editSession=WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))
        val operation: Operation = ClipboardHolder(clipboard)
            .createPaste(editSession)
            .to(BlockVector3.at(loc.x,loc.y,loc.z)) // configure here
            .filter(){it->return@filter true }
            .build()
        Operations.complete(operation)
        operation.cancel()
        clipboard!!.flush()
        clipboard!!.close()
        editSession.flushQueue()
        editSession.close()

    }
}