package com.example.filtros.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.filtros.viewModels.MainActivityViewModel
import com.example.filtros.R
import com.example.filtros.databinding.ActivityMainBinding
import com.example.filtros.models.ImageSingleton
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var _bitmap: Bitmap? = null
    private val viewModel: MainActivityViewModel by viewModels()
    private var showAccept = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tomarFoto.setOnClickListener {
            dispatchTakePictureIntent()
        }

        binding.buscarGaleria.setOnClickListener {
            dispatchChooseFromGalleryIntent()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == viewModel.requestImageCapture && resultCode == RESULT_OK) {
            _bitmap = data?.extras?.get("data") as Bitmap
            showAccept = true
            invalidateOptionsMenu()
            binding.imageView.setImageBitmap(_bitmap)
        } else if (requestCode == viewModel.requestImageGallery && resultCode == RESULT_OK) {
            val selectedImageURI = data?.data
            val imageStream = selectedImageURI?.let { contentResolver.openInputStream(it) }
            _bitmap = BitmapFactory.decodeStream(imageStream)
            binding.imageView.setImageBitmap(_bitmap)
        } else {
            Snackbar.make(binding.layout, "No hay imagen", Snackbar.LENGTH_SHORT)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val acceptIcon = menu.findItem(R.id.accept)
        acceptIcon.isVisible = showAccept
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.accept -> {
            val intent = Intent(this, SelectFilterActivity::class.java)
            ImageSingleton.image = _bitmap!!
            startActivity(intent)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, viewModel.requestImageCapture)
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(binding.layout, e.toString(), Snackbar.LENGTH_SHORT)
        }
    }

    private fun dispatchChooseFromGalleryIntent() {
        val pickPhoto = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        try {
            startActivityForResult(pickPhoto, viewModel.requestImageGallery)
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(binding.layout, e.toString(), Snackbar.LENGTH_SHORT)
        }
    }
}