package me.szu.kurtkong.ui

import me.szu.kurtkong.config.ConfigObject
import me.szu.kurtkong.ui.GuiMain.openMainGui
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import taboolib.common.util.asList
import taboolib.module.chat.colored
import taboolib.module.nms.inputSign
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.inputBook

object GuiSettings {
    fun Player.openSettings(key:String){
        this.openMenu<Basic>("�ż�${key}���趨"){
            rows(2)
            set(0,ItemBuilder(Material.PLAYER_HEAD).also {
            it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWRiYTgxYjNmYzNhZDNjNTA4NzQ1ZGY2MTAwN2I0ZTRhMDJhZWVjODZlMmNiMWM2NWUzNjc2ODExY2NmMDdmZiJ9fX0=")
                it.name="&f���ɸ���".colored()
                it.lore.add("&f�����ʽ��һ���Ǹ�����".colored())
                it.lore.add("&a��ǰֵ:${ConfigObject.config.getString("Structures.${key}.chance")}".colored())
            }.build()){
                closeInventory()
                this.clicker.inputSign {
                        if(it[0].isNotEmpty())ConfigObject.config.set("Structures.${key}.chance", it[0])
                        ConfigObject.save()
                    this.clicker.openSettings(key)
                }
            }

            set(1,ItemBuilder(Material.PLAYER_HEAD).also {
                it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODA4YWM1ZTI4ZGJkZmEyMjUwYzYwMjg3Njg2ZGIxNGNjYmViNzc2YzNmMDg2N2M5NTU1YjdlNDk1NmVmYmE3NyJ9fX0=")
                it.name="&f��������".colored()
                it.lore.add("&f�����ʽ��һ��������".colored())
                it.lore.add("&a��ǰֵ:${ConfigObject.config.getStringList("Structures.${key}.world")}".colored())
                it.lore.add("&a�������������".colored())
                it.lore.add("&a�Ҽ�����Ƴ�����".colored())
            }.build()){
                var list=ConfigObject.config.getStringList("Structures.${key}.world").toMutableList()
                if(this.clickEvent().click==ClickType.LEFT){
                    this.clicker.inputSign {

                        it.forEach { its -> if(its.isNotEmpty())list.add(its) }
                        ConfigObject.config.set("Structures.${key}.world",list)
                        ConfigObject.save()
                        this.clicker.openSettings(key)
                    }
                }
                else if(this.clickEvent().click==ClickType.RIGHT){
                    if(list.isNotEmpty()){
                        list.removeLast()
                    }
                    ConfigObject.config.set("Structures.${key}.world",list)
                    ConfigObject.save()
                    this.clicker.openSettings(key)
                }
            }

            set(2,ItemBuilder(Material.STICK).also {
                //it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWRiYTgxYjNmYzNhZDNjNTA4NzQ1ZGY2MTAwN2I0ZTRhMDJhZWVjODZlMmNiMWM2NWUzNjc2ODExY2NmMDdmZiJ9fX0=")
                it.name="&f�߶�����".colored()
                it.lore.add("&f�����ʽ��".colored())
                it.lore.add("&f - һ�����䣬��[1,100]".colored())
                it.lore.add("&f - sky ����ʾ���)".colored())
                it.lore.add("&f - surface (��ʾ����)".colored())
                it.lore.add("&f - underground (��ʾ����)".colored())
                it.lore.add("&a��ǰֵ:${ConfigObject.config.getString("Structures.${key}.height")}".colored())
            }.build()){
                closeInventory()
                this.clicker.inputSign {

                        if(it[0].isNotEmpty())ConfigObject.config["Structures.${key}.height"] = it[0]
                        ConfigObject.save()

                    this.clicker.openSettings(key)
                }
            }

            set(3,ItemBuilder(Material.PLAYER_HEAD).also {
                it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmZiODJkYzBjODI1MzY5NjRhNTIzZTRhZWFlMjM1ZmEyNjU2ZDVjODdkMDYyZGM2NTM5YTI0NTczMmZjZGU2ZSJ9fX0=")
                it.name="&fȺϵ����".colored()
                it.lore.add("&f�����ʽ��".colored())
                it.lore.add("&f - ��һ�������ռ䣨��ð�Ž�β)���ڶ���Ⱥϵ��".colored())
                it.lore.add("&f - ����һ������ؼ���ALL ��ʾ����Ⱥϵ".colored())
                it.lore.add("&a��ǰֵ:${ConfigObject.config.getStringList("Structures.${key}.biomes")}".colored())
                it.lore.add("&a���������Ⱥϵ".colored())
                it.lore.add("&a�Ҽ�����Ƴ�Ⱥϵ".colored())
            }.build()){
                var list=ConfigObject.config.getStringList("Structures.${key}.biomes").toMutableList()
                if(this.clickEvent().click==ClickType.LEFT){
                    this.clicker.inputSign {
                        if(it[0].isNotEmpty()){
                            if(it[0].equals("all",ignoreCase = true)){
                                list.add("ALL")
                            }
                            else if(it[1].isNotEmpty()){
                                list.add(it[0]+it[1])
                            }
                        }
                        ConfigObject.config.set("Structures.${key}.biomes",list)
                        ConfigObject.save()
                        this.clicker.openSettings(key)
                    }

                }
                else if(this.clickEvent().click==ClickType.RIGHT){
                    if(list.isNotEmpty()){
                        list.removeLast()
                    }
                    ConfigObject.config.set("Structures.${key}.biomes",list)
                    ConfigObject.save()
                    this.clicker.openSettings(key)
                }

            }

            set(4,ItemBuilder(Material.PLAYER_HEAD).also {
                it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ1ZWMwYTNiYTBjZjIwMjE2ZDgwZWQ1YmRhMTlkNjZkN2I5YjFlNTQ1MGFmODZhMzk3ZmI3YjY3NTk3YmZmOSJ9fX0=")
                it.name="&f���������".colored()
                it.lore.add("&f�����ʽ��һ�����䣬����[1,100]".colored())
                it.lore.add("&a��ǰֵ:${ConfigObject.config.getString("Structures.${key}.awayFromSpawn")}".colored())
            }.build()){

                this.clicker.inputSign {

                        if(it[0].isNotEmpty())ConfigObject.config.set("Structures.${key}.awayFromSpawn", it[0])
                        ConfigObject.save()

                    this.clicker.openSettings(key)
                }

            }
            set(5,ItemBuilder(Material.PLAYER_HEAD).also {
                it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ1ZWMwYTNiYTBjZjIwMjE2ZDgwZWQ1YmRhMTlkNjZkN2I5YjFlNTQ1MGFmODZhMzk3ZmI3YjY3NTk3YmZmOSJ9fX0=")
                it.name="&fͬ�ּ��".colored()
                it.lore.add("&f�����ʽ��һ���Ǹ�С��".colored())
                it.lore.add("&a��ǰֵ:${ConfigObject.config.getString("Structures.${key}.distanceBetween")}".colored())
            }.build()){

                this.clicker.inputSign {

                        if(it[0].isNotEmpty()) ConfigObject.config.set("Structures.${key}.distanceBetween", it[0])
                        ConfigObject.save()

                    this.clicker.openSettings(key)
                }

            }

            set(6,ItemBuilder(Material.PLAYER_HEAD).also {
                it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjFiZmI3YTc1YTBmMTVjZGUzYjgwZWE3MDE1NzkyNjU5ZGZjNjhkMTk2YTMzZTlkOWI3NGMwNTJjYjFkZDcxZCJ9fX0=")
                it.name="&f��������".colored()
                it.lore.add("&f�����ʽ��һ���Ǹ�����".colored())
                it.lore.add("&a��ǰֵ:${ConfigObject.config.getString("Structures.${key}.amountLimit")}".colored())
            }.build()){

                this.clicker.inputSign {

                        if(it[0].isNotEmpty())  ConfigObject.config.set("Structures.${key}.amountLimit", it[0])
                        ConfigObject.save()

                    this.clicker.openSettings(key)
                }

            }

            set(7,ItemBuilder(Material.PLAYER_HEAD).also {
                it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGUwN2ViYzIzOTMzZGZlZGI2MDIwYjE0NjQ5ZmVlMzg5Njg4MjY5Yzk4NTQ4OTA5NGY4MGI2MzkzYzY5MWNhOCJ9fX0=")
                it.name="&f��������".colored()
                it.lore.add("&f�����ʽ��һ���Ϸ��Ĳ�����".colored())
                it.lore.add("&a��ǰֵ:${ConfigObject.config.getString("Structures.${key}.pedestal_material")}".colored())
            }.build()){

                this.clicker.inputSign {

                    if(it[0].isNotEmpty())   ConfigObject.config.set("Structures.${key}.pedestal_material", it[0])
                        ConfigObject.save()

                    this.clicker.openSettings(key)
                }
            }

            set(8,ItemBuilder(Material.PLAYER_HEAD).also {
                it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmVlNTU0YWJlMTRmZGE5MmVmNWVjOTIxMjIyZmU2MGMyNjhhOGFiZGY0MTIwZDRmMjgzZTgwM2RlOGQzZmUwYiJ9fX0=")
                it.name="&f��������".colored()
                it.lore.add("&f�����ʽ��".colored())
                it.lore.add("&f - �Ϸ��Ĳ�����".colored())
                it.lore.add("&f - ������ALL���ʾ���в���".colored())
                it.lore.add("&a��ǰֵ:${ConfigObject.config.getStringList("Structures.${key}.bottom_material")}".colored())
                it.lore.add("&a���������".colored())
                it.lore.add("&a�Ҽ�����Ƴ�".colored())
            }.build()){
                var list=ConfigObject.config.getStringList("Structures.${key}.bottom_material").toMutableList()
                if(this.clickEvent().click==ClickType.LEFT){
                    this.clicker.inputSign {

                        it.forEach { its -> if(its.isNotEmpty())list.add(its) }
                        ConfigObject.config.set("Structures.${key}.bottom_material",list)
                        ConfigObject.save()
                        this.clicker.openSettings(key)
                    }

                }
                else if(this.clickEvent().click==ClickType.RIGHT){
                    if(list.isNotEmpty()){
                        list.removeLast()
                    }
                    ConfigObject.config.set("Structures.${key}.bottom_material",list)
                    ConfigObject.save()
                    this.clicker.openSettings(key)
                }

            }
            set(13,ItemBuilder(Material.PLAYER_HEAD).also {
                it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWI5M2VkYmE0MmM3YmJmYTk0YjEyZjg5YmQ1NWQ5NTg2MjI1OWNkYjYyOTNjODNiOTBiOTMxYWU0ZDEzOTA4OCJ9fX0=")
                it.name="&f����".colored()
            }.build()){
                this.clicker.openMainGui()
            }
        }
    }


}