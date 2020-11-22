package com.example.filtros.filterView

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import com.example.filtros.R
import com.example.filtros.filterView.Filter.*
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider

class FiltersView : ScrollView {

    private lateinit var normal: Button
    private lateinit var negativo: Button
    private lateinit var grises: Button
    private lateinit var brillo: Button
    private lateinit var contraste: Button
    private lateinit var gamma: Button
    private lateinit var verde: Button
    private lateinit var rojo: Button
    private lateinit var azul: Button
    private lateinit var smoothing: Button
    private lateinit var gaussianBlur: Button
    private lateinit var sharpen: Button
    private lateinit var meanRemoval: Button
    private lateinit var embossing: Button
    private lateinit var edgeDetection: Button
    private lateinit var slider: Slider

    var selected: Filter = NORMAL

    var listener: FilterViewListener? = null
        set(value) {
            field = value
            addListeners()
        }

    constructor(ctx: Context) : super(ctx) {
        iniciar()
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        iniciar()
    }

    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        ctx,
        attrs,
        defStyleAttr
    ) {
        iniciar()
    }

    private fun iniciar() {
        val layoutInflater = LayoutInflater.from(context)
        layoutInflater.inflate(R.layout.filter_view, this, true)
        normal = findViewById(R.id.buttonNormal)
        negativo = findViewById(R.id.buttonNegativo)
        grises = findViewById(R.id.buttonGrises)
        brillo = findViewById(R.id.buttonBrillo)
        contraste = findViewById(R.id.buttonContraste)
        gamma = findViewById(R.id.buttonGamma)
        verde = findViewById(R.id.buttonVerde)
        rojo = findViewById(R.id.buttonRojo)
        azul = findViewById(R.id.buttonAzul)
        smoothing = findViewById(R.id.buttonSmoothing)
        gaussianBlur = findViewById(R.id.buttonGaussianBlur)
        sharpen = findViewById(R.id.buttonSharpen)
        meanRemoval = findViewById(R.id.buttonMeanRemoval)
        embossing = findViewById(R.id.buttonEmbossing)
        edgeDetection = findViewById(R.id.buttonEdgeDetection)
        slider = findViewById(R.id.slider)
        addListeners()
    }

    private fun addListeners() {
        if (listener != null) {
            slider.value = 0f
            normal.setOnClickListener {
                slider.visibility = View.GONE
                listener!!.onClick(NORMAL)
                changeStyle(it as Button)
            }
            negativo.setOnClickListener {
                slider.visibility = View.GONE
                listener!!.onClick(NEGATIVO)
                changeStyle(it as Button)
            }
            grises.setOnClickListener {
                slider.visibility = View.GONE
                listener!!.onClick(GRISES)
                changeStyle(it as Button)
            }
            brillo.setOnClickListener {
                slider.visibility = View.VISIBLE
                listener!!.onClick(BRILLO)
                changeStyle(it as Button)
            }
            contraste.setOnClickListener {
                slider.visibility = View.VISIBLE
                listener!!.onClick(CONTRASTE)
                changeStyle(it as Button)
            }
            gamma.setOnClickListener {
                slider.visibility = View.VISIBLE
                listener!!.onClick(GAMMA)
                changeStyle(it as Button)
            }
            verde.setOnClickListener {
                slider.visibility = View.GONE
                listener!!.onClick(VERDE)
                changeStyle(it as Button)
            }
            rojo.setOnClickListener {
                slider.visibility = View.GONE
                listener!!.onClick(ROJO)
                changeStyle(it as Button)
            }
            azul.setOnClickListener {
                slider.visibility = View.GONE
                listener!!.onClick(AZUL)
                changeStyle(it as Button)
            }
            smoothing.setOnClickListener {
                slider.visibility = View.GONE
                listener!!.onClick(SMOOTHING)
                changeStyle(it as Button)
            }
            gaussianBlur.setOnClickListener {
                slider.visibility = View.GONE
                listener!!.onClick(GAUSSIAN_BLUR)
                changeStyle(it as Button)
            }
            sharpen.setOnClickListener {
                slider.visibility = View.GONE
                listener!!.onClick(SHARPEN)
                changeStyle(it as Button)
            }
            meanRemoval.setOnClickListener {
                slider.visibility = View.GONE
                listener!!.onClick(MEAN_REMOVAL)
                changeStyle(it as Button)
            }
            embossing.setOnClickListener {
                slider.visibility = View.GONE
                listener!!.onClick(EMBOSSING)
                changeStyle(it as Button)
            }
            edgeDetection.setOnClickListener {
                slider.visibility = View.GONE
                listener!!.onClick(EDGE_DETECTION)
                changeStyle(it as Button)
            }
            slider.setLabelFormatter {
                it.toInt().toString()
            }
            slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}

                override fun onStopTrackingTouch(slider: Slider) {
                    listener!!.onStop(selected, slider.value.toInt())
                }

            })
        }
    }

    fun disableAll() {
        normal.isEnabled = false
        negativo.isEnabled = false
        grises.isEnabled = false
        brillo.isEnabled = false
        contraste.isEnabled = false
        gamma.isEnabled = false
        verde.isEnabled = false
        rojo.isEnabled = false
        azul.isEnabled = false
        smoothing.isEnabled = false
        gaussianBlur.isEnabled = false
        sharpen.isEnabled = false
        meanRemoval.isEnabled = false
        embossing.isEnabled = false
        edgeDetection.isEnabled = false
        slider.isEnabled = false
    }

    fun enableAll() {
        normal.isEnabled = true
        negativo.isEnabled = true
        grises.isEnabled = true
        brillo.isEnabled = true
        contraste.isEnabled = true
        gamma.isEnabled = true
        verde.isEnabled = true
        rojo.isEnabled = true
        azul.isEnabled = true
        smoothing.isEnabled = true
        gaussianBlur.isEnabled = true
        sharpen.isEnabled = true
        meanRemoval.isEnabled = true
        embossing.isEnabled = true
        edgeDetection.isEnabled = true
        slider.isEnabled = true
    }

    private fun changeStyle(button: Button) {
        button.setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.design_default_color_primary
            )
        )
        button.setTextColor(Color.WHITE)
        when (selected) {
            NORMAL -> clearButtonStyle(normal)
            NEGATIVO -> clearButtonStyle(negativo)
            GRISES -> clearButtonStyle(grises)
            BRILLO -> clearButtonStyle(brillo)
            CONTRASTE -> clearButtonStyle(contraste)
            GAMMA -> clearButtonStyle(gamma)
            ROJO -> clearButtonStyle(rojo)
            VERDE -> clearButtonStyle(verde)
            AZUL -> clearButtonStyle(azul)
            SMOOTHING -> clearButtonStyle(smoothing)
            GAUSSIAN_BLUR -> clearButtonStyle(gaussianBlur)
            SHARPEN -> clearButtonStyle(sharpen)
            MEAN_REMOVAL -> clearButtonStyle(meanRemoval)
            EMBOSSING -> clearButtonStyle(embossing)
            EDGE_DETECTION -> clearButtonStyle(edgeDetection)
        }
        changeSelected(button)
    }

    private fun clearButtonStyle(button: Button) {
        button.setBackgroundColor(Color.WHITE)
        button.setTextColor(ContextCompat.getColor(context, R.color.design_default_color_primary))
    }

    private fun changeSelected(button: Button) {
        selected = when (button.text.toString()) {
            "Normal" -> NORMAL
            "Negativo" -> NEGATIVO
            "Escala de Grises" -> GRISES
            "Brillo" -> BRILLO
            "Contraste" -> CONTRASTE
            "Gamma" -> GAMMA
            "Rojo" -> ROJO
            "Verde" -> VERDE
            "Azul" -> AZUL
            "Smoothing" -> SMOOTHING
            "Gaussian Blur" -> GAUSSIAN_BLUR
            "Sharpen" -> SHARPEN
            "Mean Removal" -> MEAN_REMOVAL
            "Embossing" -> EMBOSSING
            "Edge Detection" -> EDGE_DETECTION
            else -> NORMAL
        }
    }

    interface FilterViewListener {
        fun onClick(filter: Filter)
        fun onStop(filter: Filter, value: Int)
    }
}