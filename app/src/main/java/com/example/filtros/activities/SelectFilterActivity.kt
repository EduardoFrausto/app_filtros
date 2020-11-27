package com.example.filtros.activities

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.filtros.R
import com.example.filtros.databinding.ActivitySelectFilterBinding
import com.example.filtros.filterView.Filter
import com.example.filtros.filterView.Filter.*
import com.example.filtros.filterView.FiltersView
import com.example.filtros.viewModels.SelectActivityViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class SelectFilterActivity : AppCompatActivity() {

    private lateinit var _bitmapOriginal: Bitmap
    private lateinit var _bitmapSave: Bitmap
    private lateinit var binding: ActivitySelectFilterBinding
    private val viewModel: SelectActivityViewModel by viewModels()
    private val REQUEST_CODE = 1
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.save_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_file -> {
                checkPermission()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveFile() {
        lifecycleScope.launch(Dispatchers.IO) {
            val filename = "${Date()}.jpg"
            var outputStream: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                applicationContext.contentResolver.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    val imageURI: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    outputStream = imageURI?.let { resolver.openOutputStream(it) }
                }
            } else {
                val imgDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imgDir, filename)
                outputStream = FileOutputStream(image)
            }
            outputStream?.use {
                _bitmapSave.compress(Bitmap.CompressFormat.JPEG, 100, it)
                withContext(Dispatchers.Main) {
                    Snackbar.make(binding.layout, "Imagen guardada ", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE
            )
        } else {
            saveFile()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        _bitmapOriginal = intent.getParcelableExtra("bitmap")!!
        _bitmapSave = _bitmapOriginal
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
                        NEGATIVO, GRISES, ROJO, VERDE, AZUL, RANDOM_JITTER, FLIP -> {
                            bitmapCopy =
                                viewModel.applyFilter(_bitmapOriginal, filter)
                            _bitmapSave = bitmapCopy
                            withContext(Dispatchers.Main) {
                                binding.imageView2.setImageBitmap(bitmapCopy)
                            }
                        }
                        BRILLO, CONTRASTE -> {
                            bitmapCopy =
                                viewModel.applyFilter(_bitmapOriginal, filter)
                            _bitmapSave = bitmapCopy
                            withContext(Dispatchers.Main) {
                                binding.imageView2.setImageBitmap(bitmapCopy)
                            }
                        }
                        GAMMA -> {
                            bitmapCopy =
                                viewModel.applyGamma(_bitmapOriginal, 0.6f, 0.6f, 0.6f)
                            _bitmapSave = bitmapCopy
                            withContext(Dispatchers.Main) {
                                binding.imageView2.setImageBitmap(bitmapCopy)
                            }
                        }
                        SMOOTHING, GAUSSIAN_BLUR, SHARPEN, MEAN_REMOVAL, EDGE_DETECTION, EMBOSSING, SOBELL -> {
                            bitmapCopy =
                                viewModel.applyConvultionFilter(_bitmapOriginal, filter)
                            _bitmapSave = bitmapCopy
                            withContext(Dispatchers.Main) {
                                binding.imageView2.setImageBitmap(bitmapCopy)
                            }
                        }
                        DISTORTION -> {
                            bitmapCopy =
                                viewModel.applySphereFilter(_bitmapOriginal)
                            _bitmapSave = bitmapCopy
                            withContext(Dispatchers.Main) {
                                binding.imageView2.setImageBitmap(bitmapCopy)
                            }
                        }
                        PIXELATED -> {
                            bitmapCopy =
                                viewModel.applyWaveFilter(_bitmapOriginal)
                            _bitmapSave = bitmapCopy
                            withContext(Dispatchers.Main) {
                                binding.imageView2.setImageBitmap(bitmapCopy)
                            }
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
                            _bitmapSave = bitmapCopy
                            withContext(Dispatchers.Main) {
                                binding.imageView2.setImageBitmap(bitmapCopy)
                            }
                        }
                        BRILLO, CONTRASTE -> {
                            bitmapCopy =
                                viewModel.applyFilter(_bitmapOriginal, filter, value)
                            _bitmapSave = bitmapCopy
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                saveFile()
            }
        }
    }
}