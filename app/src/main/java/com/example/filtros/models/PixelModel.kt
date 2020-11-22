package com.example.filtros.models

import android.graphics.Color

data class PixelModel(var red: Int, var green: Int, var blue: Int, var alpha:Int){
    companion object{
        fun fromPixel(pixel:Int): PixelModel {
            return PixelModel( Color.red(pixel),Color.green(pixel), Color.blue(pixel),Color.alpha(pixel))
        }
    }
}