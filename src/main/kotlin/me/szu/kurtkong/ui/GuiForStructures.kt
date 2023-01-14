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

        this.openMenu<Linked<StructureData.Structure>>("�����ɵ��ż�"){

            // ����Ӧ����ʾ����
            rows(6)
            // �ɷ�����Ʒλ�ã�����ط�Ӧ���ṩһ��MutableList<Int>���б�
            //slots(mutableListOf().toList())
            // ��ʾ�ڽ����ϵ�����Ԫ�ؼ���
            // ��ʾ����Ʒ������ܴ������һ��ʵ���࣬��������Ӧ���п��Ա�ʾItemStack��һ������
            elements {
                return@elements StructureData.structures
            }
            slots(generateSequence(0) { if(it+1<45)it+1 else null }.toList())
            // ��һҳλ���Լ���Ʒ
            setNextPage(53) { page, hasNextPage ->

                return@setNextPage ItemStack(Material.GRASS_BLOCK)
            }
            // ��һҳλ���Լ���Ʒ
            setPreviousPage(45) { page, hasPreviousPage ->

                return@setPreviousPage ItemStack(Material.STONE)
            }
            onClick { event, element ->
                event.clicker.teleport(element.pos1.clone().add(1.0,1.0,1.0))
            }
            onGenerate { _, element, _, _ ->
                return@onGenerate ItemBuilder(ConfigObject.getIcon(element.key)).also { its ->
                    its.lore.forEach { it.colored() }
                    its.lore.add("&f��һ�ǵ�: ${element.pos1.x} , ${element.pos1.y} , ${element.pos1.z}".colored())
                    its.lore.add("&f�ڶ��ǵ�: ${element.pos2.x} , ${element.pos2.y} , ${element.pos2.z}".colored())
                    its.lore.add("&a������͵��ż�����".colored())
                }.build()
            }

        }
    }

}