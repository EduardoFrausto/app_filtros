package com.example.filtros.activities

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.filtros.databinding.ActivitySelectFilterBinding
import com.example.filtros.filterView.Filter
import com.example.filtros.filterView.Filter.*
import com.example.filtros.filterView.FiltersView
import com.example.filtros.viewModels.SelectActivityViewModel
import kotlinx.coroutines.*

class SelectFilterActivity : AppCompatActivity() {

    private lateinit var _bitmapOriginal: Bitmap
    private lateinit var binding: ActivitySelectFilterBinding
    private val viewModel: SelectActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        _bitmapOriginal = intent.getParcelableExtra("bitmap")!!
        binding.imageView2.setImageBitmap(_bitmapOriginal)
        binding.filters.listener = object : FiltersView.FilterViewListener {
            override fun onClick(filter: Filter) {
                lifecycleScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {
                        binding.filters.disableAll()
                    }
                    val bitmapCopy: Bitmap
                    when (filter) {
                        NORMAL -> {
                            withContext(Dispatchers.Main) {
                                binding.imageView2.setImageBitmap(_bitmapOriginal)
                            }
                        }
                        NEGATIVO, GRISES, ROJO, VERDE, AZUL, RANDOM_JITTER,FLIP -> {
                            bitmapCopy =
                                viewModel.applyFilter(_bitmapOriginal, filter)
                            withContext(Dispatchers.Main) {
                                binding.imageView2.setImageBitmap(bitmapCopy)
                            }
                        }
                        BRILLO, CONTRASTE -> {
                            bitmapCopy =
                                viewModel.applyFilter(_bitmapOriginal, filter)
                            withContext(Dispatchers.Main) {
                                binding.imageView2.setImageBitmap(bitmapCopy)
                            }
                        }
                        GAMMA -> {
                            bitmapCopy =
                                viewModel.applyGamma(_bitmapOriginal, 0.6f, 0.6f, 0.6f)
                            withContext(Dispatchers.Main) {
                                binding.imageView2.setImageBitmap(bitmapCopy)
                            }
                        }
                        SMOOTHING, GAUSSIAN_BLUR, SHARPEN, MEAN_REMOVAL, EDGE_DETECTION, EMBOSSING,SOBELL -> {
                            bitmapCopy =
                                viewModel.applyConvultionFilter(_bitmapOriginal, filter)
                            withContext(Dispatchers.Main) {
                                binding.imageView2.setImageBitmap(bitmapCopy)
                            }
                        }
                        else -> {
                        }
                    }
                    withContext(Dispatchers.Main) {
                        binding.filters.enableAll()
                    }
                }
            }

            override fun onStop(filter: Filter, value: Float) {
                lifecycleScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {
                        binding.filters.disableAll()
                    }
                    val bitmapCopy: Bitmap
                    when (filter) {
                        NORMAL -> {
                            withContext(Dispatchers.Main) {
                                binding.imageView2.setImageBitmap(_bitmapOriginal)
                            }
                        }
                        NEGATIVO, GRISES -> {
                            bitmapCopy =
                                viewModel.applyFilter(_bitmapOriginal, filter)
                            withContext(Dispatchers.Main) {
                                binding.imageView2.setImageBitmap(bitmapCopy)
                            }
                        }
                        BRILLO, CONTRASTE -> {
                            bitmapCopy =
                                viewModel.applyFilter(_bitmapOriginal, filter, value)
                            withContext(Dispatchers.Main) {
                                binding.imageView2.setImageBitmap(bitmapCopy)
                            }
                        }
                        else -> {
                        }
                    }
                    withContext(Dispatchers.Main) {
                        binding.filters.enableAll()
                    }
                }
            }

            override fun gamma(red: Float, green: Float, blue: Float) {
                lifecycleScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {
                        binding.filters.disableAll()
                    }
                    val bitmapCopy: Bitmap = viewModel.applyGamma(_bitmapOriginal, red, green, blue)
                    withContext(Dispatchers.Main) {
                        binding.imageView2.setImageBitmap(bitmapCopy)
                        binding.filters.enableAll()
                    }
                }
            }
        }
    }
}