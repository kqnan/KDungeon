package me.szu.kurtkong.ui

import me.szu.kurtkong.StructureData
import me.szu.kurtkong.config.ConfigObject
import me.szu.kurtkong.ui.GuiMain.openMainGui
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.chat.colored
import taboolib.module.ui.Menu
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.ItemBuilder

object GuiForStructures {
    fun Player.openStructureGUI(structures :List<StructureData.Structure>){

        this.openMenu<Linked<StructureData.Structure>>("�����ɵ��ż�"){

            rows(6)
            elements {
                return@elements structures
            }
            slots(generateSequence(0) { if(it+1<45)it+1 else null }.toList())
            // ��һҳλ���Լ���Ʒ
            setNextPage(53) { page, hasNextPage ->

                return@setNextPage  ItemBuilder(Material.PLAYER_HEAD).also { it.skullTexture=ItemBuilder.SkullTexture(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2NzJiODJmMGQxZjhjNDBjNTZiNDJkMzY5YWMyOTk0Yzk0ZGE0NzQ5MTAxMGMyY2U0MzAzZTM0NjViOTJhNyJ9fX0="
                )

                    it.name="&f��һҳ".colored()
                }.build()
            }
            set(49,ItemBuilder(Material.PLAYER_HEAD).also {
                it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWI5M2VkYmE0MmM3YmJmYTk0YjEyZjg5YmQ1NWQ5NTg2MjI1OWNkYjYyOTNjODNiOTBiOTMxYWU0ZDEzOTA4OCJ9fX0=")
                it.name="&f����".colored()
            }.build()){
                this.clicker.openMainGui()
            }
            // ��һҳλ���Լ���Ʒ
            setPreviousPage(45) { page, hasPreviousPage ->

                return@setPreviousPage ItemBuilder(Material.PLAYER_HEAD).also {
                    it.skullTexture=ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTVlZmQ5Njk3NGMwNDAzZjIyOWNmOTQxODVjZGQwZjcxOTczNjJhY2JkMDMxY2RmNTFmY2M4ZGFmYWM2Yjg1YSJ9fX0=")

                    it.name="&f��һҳ".colored()
                }.build()
            }
            onClick { event, element ->
                event.clicker.teleport(element.pos1.clone().add(1.0,1.0,1.0))
            }
            onGenerate (async = true){ _, element, _, _ ->
                return@onGenerate ItemBuilder(ConfigObject.getIcon(element.key)).also { its ->
                    its.lore.forEach { it.colored() }
                    its.lore.add("&f��һ�ǵ�: ${element.pos1.x} , ${element.pos1.y} , ${element.pos1.z}".colored())
                    its.lore.add("&f�ڶ��ǵ�: ${element.pos2.x} , ${element.pos2.y} , ${element.pos2.z}".colored())
                    its.lore.add("&f���磺${element.pos1.world?.name}".colored())
                    its.lore.add("&a������͵��ż�����".colored())
                }.build()
            }

        }
    }

}