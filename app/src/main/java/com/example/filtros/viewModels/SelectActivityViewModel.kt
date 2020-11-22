package com.example.filtros.viewModels

import android.R.attr
import android.R.attr.x
import android.R.attr.y
import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.filtros.filterView.Filter
import com.example.filtros.models.PixelModel


class SelectActivityViewModel : ViewModel() {

    fun applyFilter(bitmap: Bitmap, filter: Filter, value: Int = 0): Bitmap {
        var pixelModel: PixelModel
        val bitmapCopy = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        for (i in 0 until bitmap.width) {
            for (j in 0 until bitmap.height) {
                pixelModel = PixelModel.fromPixel(bitmap.getPixel(i, j))
                when (filter) {
                    Filter.NEGATIVO -> {
                        bitmapCopy.setPixel(
                            i, j,
                            Color.argb(
                                pixelModel.alpha,
                                255 - pixelModel.red,
                                255 - pixelModel.green,
                                255 - pixelModel.blue
                            )
                        )

                    }
                    Filter.GRISES -> {
                        pixelModel.blue =
                            (pixelModel.red.toDouble() * 0.299).toInt() + (pixelModel.green.toDouble() * 0.587).toInt() + (pixelModel.blue.toDouble() * 0.114).toInt()
                        pixelModel.red =
                            (pixelModel.red.toDouble() * 0.299).toInt() + (pixelModel.green.toDouble() * 0.587).toInt() + (pixelModel.blue.toDouble() * 0.114).toInt()
                        pixelModel.green =
                            (pixelModel.red.toDouble() * 0.299).toInt() + (pixelModel.green.toDouble() * 0.587).toInt() + (pixelModel.blue.toDouble() * 0.114).toInt()
                        bitmapCopy.setPixel(
                            i, j,
                            Color.argb(
                                pixelModel.alpha,
                                pixelModel.red,
                                pixelModel.green,
                                pixelModel.blue
                            )
                        )
                    }
                    Filter.BRILLO -> {
                        pixelModel.green += value
                        pixelModel.blue += value
                        pixelModel.red += value
                        bitmapCopy.setPixel(
                            i, j,
                            Color.argb(
                                pixelModel.alpha,
                                checkRange(pixelModel.red),
                                checkRange(pixelModel.green),
                                checkRange(pixelModel.blue),
                            )
                        )
                    }
                    Filter.CONTRASTE -> {
                        var contrast = (100f + value.toFloat()) / 100f
                        contrast *= contrast
                        bitmapCopy.setPixel(
                            i, j,
                            Color.argb(
                                pixelModel.alpha,
                                applyContrastToColor(pixelModel.red, contrast),
                                applyContrastToColor(pixelModel.green, contrast),
                                applyContrastToColor(pixelModel.blue, contrast),
                            )
                        )
                    }
                    Filter.ROJO -> {
                        bitmapCopy.setPixel(
                            i, j,
                            Color.argb(
                                pixelModel.alpha,
                                pixelModel.red,
                                0,
                                0,
                            )
                        )
                    }
                    Filter.VERDE -> {
                        bitmapCopy.setPixel(
                            i, j,
                            Color.argb(
                                pixelModel.alpha,
                                0,
                                pixelModel.green,
                                0,
                            )
                        )
                    }
                    Filter.AZUL -> {
                        bitmapCopy.setPixel(
                            i, j,
                            Color.argb(
                                pixelModel.alpha,
                                0,
                                0,
                                pixelModel.blue,
                            )
                        )
                    }
                    else -> {
                    }
                }
            }
        }
        return bitmapCopy
    }

    private fun applyContrastToColor(value: Int, contrast: Float): Int {
        var newColor: Float = value.toFloat() / 255f
        newColor -= 0.5f
        newColor *= contrast
        newColor += 0.5f
        newColor *= 255
        return checkRange(newColor.toInt())
    }

    private fun checkRange(value: Int): Int {
        return when {
            value > 255 -> {
                255
            }
            value < 0 -> {
                0
            }
            else -> {
                value
            }
        }
    }
}