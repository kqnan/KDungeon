package me.szu.kurtkong.config

import taboolib.common.platform.function.submitAsync
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import java.io.File

object ItemsObject {
    @Config(autoReload = true,value="Items.yml")
    lateinit var items:Configuration

    fun save(){
        submitAsync {

            items.saveToFile(File("plugins/KDungeon/Items.yml"))
        }
    }
}