package me.szu.kurtkong.lambdaFunc

import org.bukkit.Location

fun interface SignProcessor{
    fun apply(loc:Location,txt3:String,txt4:String)
}