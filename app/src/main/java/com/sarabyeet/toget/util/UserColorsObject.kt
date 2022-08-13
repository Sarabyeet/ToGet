package com.sarabyeet.toget.util

import android.content.Context

object UserColorsObject {
    lateinit var userData: UserColors

    fun init(context: Context){
        userData = UserColors(context)
    }
}