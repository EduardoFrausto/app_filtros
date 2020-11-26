package com.example.filtros.viewModels

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.filtros.filterView.Filter
import com.example.filtros.models.PixelModel
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random


class SelectActivityViewModel : ViewModel() {

    private val redGamma = IntArray(256)
    private val greenGamma = IntArray(256)
    private val blueGamma = IntArray(256)

    fun applyFilter(bitmap: Bitmap, filter: Filter, value: Float = 0f): Bitmap {
        var pixelModel: PixelModel
        //val bitmapCopy = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true)
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
                        pixelModel.green += value.toInt()
                        pixelModel.blue += value.toInt()
                        pixelModel.red += value.toInt()
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
                        var contrast = (100f + value) / 100f
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
                    Filter.FLIP -> {
                        bitmapCopy.setPixel(
                            bitmap.width - (i + 1), j,
                            Color.argb(
                                pixelModel.alpha,
                                pixelModel.red,
                                pixelModel.green,
                                pixelModel.blue,
                            )
                        )
                    }
                    Filter.RANDOM_JITTER -> {
                        val offset = 2
                        val rnd = Random.nextInt(-offset, offset)
                        bitmapCopy.setPixel(
                            checkXBound(rnd, i, bitmapCopy), checkYBound(rnd, j, bitmapCopy),
                            Color.argb(
                                pixelModel.alpha,
                                pixelModel.red,
                                pixelModel.green,
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

    fun applyConvultionFilter(bitmap: Bitmap, filter: Filter): Bitmap {
        return when (filter) {
            Filter.SMOOTHING -> {
                val convFilter = intArrayOf(1, 1, 1, 1, 1, 1, 1, 1, 1)
                calculateMatrix(convFilter, bitmap)
            }
            Filter.GAUSSIAN_BLUR -> {
                val convFilter = intArrayOf(1, 2, 1, 2, 4, 2, 1, 2, 1)
                calculateMatrix(convFilter, bitmap, 16)
            }
            Filter.SHARPEN -> {
                val convFilter = intArrayOf(0, -2, 0, -2, 11, -2, 0, -2, 0)
                calculateMatrix(convFilter, bitmap)
            }
            Filter.MEAN_REMOVAL -> {
                val convFilter = intArrayOf(-1, -1, -1, -1, 9, -1, -1, -1, -1)
                calculateMatrix(convFilter, bitmap)
            }
            Filter.EMBOSSING -> {
                val convFilter = intArrayOf(-1, 0, -1, 0, 4, 0, -1, 0, -1)
                calculateMatrix(convFilter, bitmap, 1, 127)
            }
            Filter.EDGE_DETECTION -> {
                val convFilter = intArrayOf(1, 1, 1, 0, 0, 0, -1, -1, -1)
                calculateMatrix(convFilter, bitmap, 1, 127)
            }
            Filter.SOBELL -> {
                val vertical = intArrayOf(1, 2, 1, 0, 0, 0, -1, -2, -1)
                val verticalMtx = calculateMatrix(vertical, bitmap, 1)
                val horizontal = intArrayOf(1, 0, -1, 2, 0, -2, 1, 0, -1)
                val horizontalMtx = calculateMatrix(horizontal, bitmap, 1)
                val bitmapCopy = horizontalMtx.copy(Bitmap.Config.ARGB_8888, true)
                for (x in 0 until bitmapCopy.width) {
                    for (y in 0 until bitmapCopy.height) {
                        val hPixelModel = PixelModel.fromPixel(horizontalMtx.getPixel(x, y))
                        val vPixelModel = PixelModel.fromPixel(verticalMtx.getPixel(x, y))
                        bitmapCopy.setPixel(
                            x, y,
                            Color.argb(
                                hPixelModel.alpha,
                                checkRange(
                                    sqrt(
                                        hPixelModel.red.toFloat().pow(2) + vPixelModel.red.toFloat()
                                            .pow(2)
                                    ).toInt()
                                ),
                                checkRange(
                                    sqrt(
                                        hPixelModel.green.toFloat()
                                            .pow(2) + vPixelModel.green.toFloat()
                                            .pow(2)
                                    ).toInt()
                                ),
                                checkRange(
                                    sqrt(
                                        hPixelModel.blue.toFloat()
                                            .pow(2) + vPixelModel.blue.toFloat()
                                            .pow(2)
                                    ).toInt()
                                )
                            )
                        )
                    }
                }
                bitmapCopy
            }
            else -> {
                bitmap
            }
        }
    }

    private fun applyContrastToColor(value: Int, contrast: Float): Int {
        var newColor: Float = value.toFloat() / 255f
        newColor -= 0.5f
        newColor *= contrast
        newColor += 0.5f
        newColor *= 255
        return checkRange(newColor.toInt())
    }

    private fun setGammaChannels(red: Double, green: Double, blue: Double) {
        val redFixed = if (red != 0.0) red else 1.0
        val greenFixed = if (green != 0.0) green else 1.0
        val blueFixed = if (blue != 0.0) blue else 1.0
        for (i in 0 until 256) {
            redGamma[i] =
                255.coerceAtMost(((255.0 * (i / 255.0).pow(1.0 / redFixed)) + 0.5).toInt())
            greenGamma[i] =
                255.coerceAtMost(((255.0 * (i / 255.0).pow(1.0 / greenFixed)) + 0.5).toInt())
            blueGamma[i] =
                255.coerceAtMost(((255.0 * (i / 255.0).pow(1.0 / blueFixed)) + 0.5).toInt())
        }
    }

    fun applyGamma(bitmap: Bitmap, red: Float, green: Float, blue: Float): Bitmap {
        var pixelModel: PixelModel
        val bitmapCopy = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        setGammaChannels(red.toDouble(), green.toDouble(), blue.toDouble())
        for (i in 0 until bitmap.width) {
            for (j in 0 until bitmap.height) {
                pixelModel = PixelModel.fromPixel(bitmap.getPixel(i, j))
                bitmapCopy.setPixel(
                    i, j,
                    Color.argb(
                        pixelModel.alpha,
                        redGamma[pixelModel.red],
                        greenGamma[pixelModel.green],
                        blueGamma[pixelModel.blue],
                    )
                )
            }
        }
        return bitmapCopy
    }

    private fun checkRange(value: Int): Int {
        return when {
            value > 255 -> 255
            value < 0 -> 0
            else -> value
        }
    }

    private fun calculateMatrix(
        convFilter: IntArray,
        bitmap: Bitmap,
        factor: Int = convFilter.sum(),
        offset: Int = 0
    ): Bitmap {
        val bitmapCopy: Bitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        var pixel: Int
        var red: Int
        var green: Int
        var blue: Int
        var contador: Int
        var weight: Int
        for (i in 0 until bitmap.width - 1) {
            for (j in 0 until bitmap.height - 1) {
                blue = 0
                green = 0
                red = 0
                contador = 0
                for (x in i - 1..i + 1) {
                    for (y in j - 1..j + 1) {
                        pixel =
                            when {
                                x == -1 && (y == -1 || y == bitmap.height) || x == bitmap.width && (y == -1 || y == bitmap.height) -> bitmap.getPixel(
                                    i,
                                    j
                                )
                                x == -1 || x == bitmap.width -> bitmap.getPixel(i, y)
                                y == -1 || y == bitmap.height -> bitmap.getPixel(x, j)
                                else -> bitmap.getPixel(x, y)
                            }
                        weight = convFilter[contador]
                        red += Color.red(pixel) * weight
                        blue += Color.blue(pixel) * weight
                        green += Color.green(pixel) * weight
                        contador++
                    }
                }
                when (factor) {
                    0 -> {
                        red += offset
                        blue += offset
                        green += offset
                    }
                    else -> {
                        red = (red / factor) + offset
                        blue = (blue / factor) + offset
                        green = (green / factor) + offset
                    }
                }
                bitmapCopy.setPixel(
                    i,
                    j,
                    Color.rgb(
                        checkRange(red),
                        checkRange(green),
                        checkRange(blue)
                    )
                )
            }
        }
        return bitmapCopy
    }

    private fun checkXBound(rnd: Int, x: Int, bitmap: Bitmap): Int {
        return when {
            x + rnd >= bitmap.width -> bitmap.width - 1
            x + rnd < 0 -> 0
            else -> x + rnd
        }
    }

    private fun checkYBound(rnd: Int, y: Int, bitmap: Bitmap): Int {
        return when {
            y + rnd >= bitmap.height -> bitmap.height - 1
            y + rnd < 0 -> 0
            else -> y + rnd
        }
    }
}