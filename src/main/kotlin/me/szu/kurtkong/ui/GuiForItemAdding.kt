package me.szu.kurtkong.ui

import me.szu.kurtkong.config.ItemsObject
import me.szu.kurtkong.debug
import me.szu.kurtkong.info
import me.szu.kurtkong.toPinYin
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
import taboolib.module.nms.getName
import taboolib.module.nms.inputSign
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked
import taboolib.module.ui.type.Stored
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.hasName
import taboolib.platform.util.isNotAir
import taboolib.platform.util.replaceName

object GuiForItemAdding {

    fun Player.openItemsAdding(){

        this.openMenu<Basic>("为物品库添加物品"){

            rows(6)
            for(i in  45 .. 53){
                if(i!=49)set(i,ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).also { it.name="&f".colored() }.build()){
                    this.isCancelled=true
                }
            }
            set(49, ItemBuilder(Material.PLAYER_HEAD).also {
                it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWI5M2VkYmE0MmM3YmJmYTk0YjEyZjg5YmQ1NWQ5NTg2MjI1OWNkYjYyOTNjODNiOTBiOTMxYWU0ZDEzOTA4OCJ9fX0=")
                it.name="&f返回".colored()
            }.build()){
                var it=this
                var keys= ItemsObject.items.getKeys(false)

                for(i in 0..44){
                    if(it.inventory.getItem(i)?.isNotAir()?:continue){
                        var item=it.inventory.getItem(i)!!
                        var key=(item.itemMeta?.displayName?:item.type.name.lowercase()).toPinYin()
                        key=key.replace(".","_")
                        if(key.isEmpty())key=item.type.name.lowercase()
                        var tmp=key

                        var j=0
                        while (keys.contains(tmp)){
                            tmp=key+j.toString()
                            j++
                        }
                        debug(tmp)
                        ItemsObject.items.setItemStack(tmp,item)
                        keys=ItemsObject.items.getKeys(false)
                    }
                }
                ItemsObject.save()
                it.clicker.openItemsGUI()
            }
            onClick{
                it.isCancelled = it.rawSlot in 45 .. 53 && it.rawSlot!=49
            }



        }

    }
}