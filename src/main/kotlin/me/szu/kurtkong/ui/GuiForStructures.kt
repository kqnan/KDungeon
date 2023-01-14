package me.szu.kurtkong.ui

import me.szu.kurtkong.StructureData
import me.szu.kurtkong.config.ConfigObject
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.chat.colored
import taboolib.module.ui.Menu
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.ItemBuilder

object GuiForStructures {
    fun Player.openStructureGUI(){

        this.openMenu<Linked<StructureData.Structure>>("已生成的遗迹"){

            // 界面应该显示几行
            rows(6)
            // 可放置物品位置，这个地方应该提供一个MutableList<Int>的列表
            //slots(mutableListOf().toList())
            // 显示在界面上的所有元素集合
            // 显示的物品，你可能传入的是一个实体类，但是至少应该有可以表示ItemStack的一个属性
            elements {
                return@elements StructureData.structures
            }
            slots(generateSequence(0) { if(it+1<45)it+1 else null }.toList())
            // 下一页位置以及物品
            setNextPage(53) { page, hasNextPage ->

                return@setNextPage ItemStack(Material.GRASS_BLOCK)
            }
            // 上一页位置以及物品
            setPreviousPage(45) { page, hasPreviousPage ->

                return@setPreviousPage ItemStack(Material.STONE)
            }
            onClick { event, element ->
                event.clicker.teleport(element.pos1.clone().add(1.0,1.0,1.0))
            }
            onGenerate { _, element, _, _ ->
                return@onGenerate ItemBuilder(ConfigObject.getIcon(element.key)).also { its ->
                    its.lore.forEach { it.colored() }
                    its.lore.add("&f第一角点: ${element.pos1.x} , ${element.pos1.y} , ${element.pos1.z}".colored())
                    its.lore.add("&f第二角点: ${element.pos2.x} , ${element.pos2.y} , ${element.pos2.z}".colored())
                    its.lore.add("&a点击传送到遗迹附近".colored())
                }.build()
            }

        }
    }

}