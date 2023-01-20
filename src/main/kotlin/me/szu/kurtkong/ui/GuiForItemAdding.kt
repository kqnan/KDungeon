package me.szu.kurtkong.ui

import me.szu.kurtkong.config.ItemsObject
import me.szu.kurtkong.info
import me.szu.kurtkong.ui.GuiForItems.openItemsGUI
import me.szu.kurtkong.ui.GuiMain.openMainGui
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import taboolib.library.xseries.getItemStack
import taboolib.library.xseries.setItemStack
import taboolib.module.chat.colored
import taboolib.module.nms.inputSign
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.module.ui.type.Stored
import taboolib.platform.util.ItemBuilder

object GuiForItemAdding {
    fun Player.openItemsAdding(){

        this.openMenu<Stored>("为物品库添加物品"){
            rows(6)

            set(49, ItemBuilder(Material.PLAYER_HEAD).also {
                it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWI5M2VkYmE0MmM3YmJmYTk0YjEyZjg5YmQ1NWQ5NTg2MjI1OWNkYjYyOTNjODNiOTBiOTMxYWU0ZDEzOTA4OCJ9fX0=")
                it.name="&f返回".colored()
            }.build()){
                this.clicker.openItemsGUI()
            }
            rule {
                this.checkSlot { inventory, itemStack, slot ->
                    return@checkSlot slot !in 45 .. 53
                }
                this.writeItem { inventory, itemStack, slot ->

                }
            }

        }
    }
}