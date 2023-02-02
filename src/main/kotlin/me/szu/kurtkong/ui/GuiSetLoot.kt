package me.szu.kurtkong.ui

import me.szu.kurtkong.config.ConfigObject
import me.szu.kurtkong.config.ItemsObject
import me.szu.kurtkong.debug
import me.szu.kurtkong.info
import me.szu.kurtkong.toPinYin
import me.szu.kurtkong.ui.GuiForItemAdding.openItemsAdding
import me.szu.kurtkong.ui.GuiForItems.openItemsGUI
import me.szu.kurtkong.ui.GuiMain.openMainGui
import me.szu.kurtkong.ui.GuiSettings.openSettings
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import taboolib.common.util.asList
import taboolib.library.xseries.getItemStack
import taboolib.library.xseries.setItemStack
import taboolib.module.chat.colored
import taboolib.module.configuration.util.getMap
import taboolib.module.nms.inputSign
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.isNotAir

object GuiSetLoot {
    fun Player.openSetLootGui(structure_key:String){
        var loot=ConfigObject.config.getMap<String,Int>("Structures.${structure_key}.loot").toMutableMap()
        this.openMenu<Linked<Pair<String, ItemStack>>>("物品库"){
            rows(6)
            elements {
                var list=ArrayList<Pair<String, ItemStack>>(ItemsObject.items.getKeys(false).size)
                for (key in ItemsObject.items.getKeys(false)) {
                    ItemsObject.items.getItemStack(key)?.let { list.add(Pair(key,it.clone())) }
                }
                return@elements list
            }
            slots(generateSequence(0) { if(it+1<45)it+1 else null }.toList())
            // 下一页位置以及物品
            setNextPage(53) { page, hasNextPage ->

                return@setNextPage  ItemBuilder(Material.PLAYER_HEAD).also { it.skullTexture= ItemBuilder.SkullTexture(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2NzJiODJmMGQxZjhjNDBjNTZiNDJkMzY5YWMyOTk0Yzk0ZGE0NzQ5MTAxMGMyY2U0MzAzZTM0NjViOTJhNyJ9fX0="
                )

                    it.name="&f下一页".colored()
                }.build()
            }
            set(49, ItemBuilder(Material.PLAYER_HEAD).also {
                it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWI5M2VkYmE0MmM3YmJmYTk0YjEyZjg5YmQ1NWQ5NTg2MjI1OWNkYjYyOTNjODNiOTBiOTMxYWU0ZDEzOTA4OCJ9fX0=")
                it.name="&f返回".colored()
            }.build()){
                this.clicker.openSettings(structure_key)
            }
            // 上一页位置以及物品
            setPreviousPage(45) { page, hasPreviousPage ->

                return@setPreviousPage ItemBuilder(Material.PLAYER_HEAD).also {
                    it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTVlZmQ5Njk3NGMwNDAzZjIyOWNmOTQxODVjZGQwZjcxOTczNjJhY2JkMDMxY2RmNTFmY2M4ZGFmYWM2Yjg1YSJ9fX0=")

                    it.name="&f上一页".colored()
                }.build()
            }
            onClick { event, element ->
                if(event.clickEvent().click== ClickType.LEFT){
                        //设置概率
                        event.clicker.inputSign {
                            it[0].toIntOrNull()?.let { its->
                                loot.put(element.first,its)
                                ConfigObject.config.set("Structures.${structure_key}.loot",loot)
                                ConfigObject.save()
                            }
                            event.clicker.openSetLootGui(structure_key)
                        }
                }else if(event.clickEvent().click== ClickType.RIGHT){
                   if(loot.contains(element.first)){
                       loot.remove(element.first)
                       val item=event.currentItem?:return@onClick
                       item.removeEnchantment(Enchantment.LURE)
                       event.inventory.setItem(event.rawSlot,item)
                   }
                }
            }
            onClose {
                ConfigObject.config.set("Structures.${structure_key}.loot",loot)
                ConfigObject.save()
            }
            onGenerate (async = true){ _, element, _, _ ->

                return@onGenerate ItemBuilder(element.second).also { its ->
                    its.lore.add("&c&l键值：${element.first}".colored() )
                    if(loot.contains(element.first)){
                        its.shiny()
                        its.lore.add("&a&l本物品已加入了${structure_key}遗迹的战利品列表".colored())
                        its.lore.add("&c&l右键点击把本物品移除${structure_key}遗迹的战利品列表".colored())
                        its.lore.add("&c&l本战利品在一个宝箱中出现的概率为：${loot[element.first]}".colored())
                    }
                    else {
                        its.lore.add("&a&l左键点击把本物品加入${structure_key}遗迹的战利品列表".colored())
                        its.lore.add("&c&l右键点击把本物品移除${structure_key}遗迹的战利品列表".colored())
                    }
                }.build()
            }

        }
    }
}