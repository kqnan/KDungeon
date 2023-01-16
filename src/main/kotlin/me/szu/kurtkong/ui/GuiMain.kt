package me.szu.kurtkong.ui

import me.szu.kurtkong.StructureData
import me.szu.kurtkong.config.ConfigObject
import me.szu.kurtkong.ui.GuiForStructures.openStructureGUI
import me.szu.kurtkong.ui.GuiSettings.openSettings
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import taboolib.common.util.asList
import taboolib.module.chat.colored
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.ItemBuilder

object GuiMain {
    fun  Player.openMainGui(){
        this.openMenu<Linked<String>>("已生成的遗迹"){

            rows(6)
            elements {
                return@elements ConfigObject.config.getConfigurationSection("Structures")?.getKeys(false)?.asList()?: emptyList()
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
            set(49,ItemBuilder(Material.PLAYER_HEAD).also {
                it.name= "&f查看所有遗迹位置".colored()
                it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmZhN2RjYzMyYTQzMzJmMDQ4ZWZlYzkzZWY1NDY2ZjM0NDBkYjQ5YzI1ODJmOTIwZTYyODRhNTRhY2UzZGJlIn19fQ==")
            }.build()){
                this.clicker.openStructureGUI(StructureData.structures)
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
                    var list=ArrayList<StructureData.Structure>()
                    StructureData.structures.forEach {
                        if(it.key==element)list.add(it)
                    }
                    event.clicker.openStructureGUI(list)
                }
                else if(event.clickEvent().click==ClickType.RIGHT){
                    event.clicker.openSettings(element)
                }
            }
            onGenerate (async = true){ _, element, _, _ ->
                return@onGenerate ItemBuilder(ConfigObject.getIcon(element)).also { its ->
                    its.lore.forEach { it.colored() }
                    its.lore.add("&f生成概率: ${ConfigObject.config.getString("Structures.${element}.chance")}".colored())
                   // its.lore.add("&f文件路径: ${ConfigObject.config.getString("Structures.${element}.schema")}".colored())
                    its.lore.add("&f生成世界: ${ConfigObject.config.getStringList("Structures.${element}.world")}".colored())
                    its.lore.add("&f高度限制: ${ConfigObject.config.getString("Structures.${element}.height")}".colored())
                    its.lore.add("&f群系限制: ${ConfigObject.config.getStringList("Structures.${element}.biomes")}".colored())
                    its.lore.add("&f距离出生点: ${ConfigObject.config.getString("Structures.${element}.awayFromSpawn")}".colored())
                    its.lore.add("&f同种间距: ${ConfigObject.config.getString("Structures.${element}.distanceBetween")}".colored())
                    its.lore.add("&f数量限制: ${ConfigObject.config.getString("Structures.${element}.amountLimit")}".colored())
                    its.lore.add("&f底座材质: ${ConfigObject.config.getString("Structures.${element}.pedestal_material")}".colored())
                    its.lore.add("&f介质限制: ${ConfigObject.config.getStringList("Structures.${element}.bottom_material")}".colored())
                    var cnt=0
                    StructureData.structures.forEach { if(it.key==element)cnt++ }
                    its.lore.add("&a目前数量：${cnt}".colored())
                    its.lore.add("&a左键点击查看此种遗迹的所有位置".colored())
                    its.lore.add("&a右键点击设置".colored())
                }.build()
            }

        }
    }
}