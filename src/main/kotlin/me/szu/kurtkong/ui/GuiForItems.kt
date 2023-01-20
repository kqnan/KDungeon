package me.szu.kurtkong.ui

import me.szu.kurtkong.StructureData
import me.szu.kurtkong.config.ConfigObject
import me.szu.kurtkong.config.ItemsObject
import me.szu.kurtkong.info
import me.szu.kurtkong.ui.GuiForItemAdding.openItemsAdding
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
import taboolib.platform.util.ItemBuilder

object GuiForItems {
    fun Player.openItemsGUI(){

        this.openMenu<Linked<Pair<String,ItemStack>>>("物品库"){
            rows(6)
            elements {
                var list=ArrayList<Pair<String,ItemStack>>(ItemsObject.items.getKeys(false).size)
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
                this.clicker.openMainGui()
            }
            set(50,ItemBuilder(Material.PLAYER_HEAD).also {
                it.skullTexture=ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzEzNzRhMDY2MWNlNzczMTE5Yjg3YWM3ZDhmNDZlOTA5NTgyZTYwZTY1MjEzNGQzNmRlMTQ0YjQ3YzNjYTEwNyJ9fX0=")
            }.build()){
                this.clicker.openItemsAdding()
            }
            // 上一页位置以及物品
            setPreviousPage(45) { page, hasPreviousPage ->

                return@setPreviousPage ItemBuilder(Material.PLAYER_HEAD).also {
                    it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTVlZmQ5Njk3NGMwNDAzZjIyOWNmOTQxODVjZGQwZjcxOTczNjJhY2JkMDMxY2RmNTFmY2M4ZGFmYWM2Yjg1YSJ9fX0=")

                    it.name="&f上一页".colored()
                }.build()
            }
            onClick { event, element ->
                if(event.clickEvent().click==ClickType.LEFT){
                    event.clicker.inputSign {
                        var newkey=it[0]
                        if(!ItemsObject.items.getKeys(false).contains(newkey)){
                            ItemsObject.items.setItemStack(newkey,element.second)
                            ItemsObject.items[element.first] = null
                            ItemsObject.save()
                        }
                        else{
                            event.clicker.info("&a此键值已存在")
                        }
                        submit(delay=10) {
                            event.clicker.openItemsGUI()
                        }
                    }
                }else if(event.clickEvent().click==ClickType.RIGHT){
                    ItemsObject.items[element.first]=null
                    ItemsObject.save()
                    submit(delay=10) {
                        event.clicker.openItemsGUI()
                    }
                }
            }
            onGenerate (async = true){ _, element, _, _ ->
                return@onGenerate ItemBuilder(element.second).also { its ->
                        its.name?.let { its.name="&c&l键值：${element.first}".colored()+its.name }
                        its.lore.add("&a&l左键点击修改键名".colored())
                        its.lore.add("&c&l右键点击删除".colored())
                }.build()
            }

        }
    }
}