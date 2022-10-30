package com.mustafageldi.movieslibrary.view.view

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mustafageldi.movieslibrary.databinding.ActivityDetailsBinding
import com.mustafageldi.movieslibrary.view.model.Movie

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val intent = intent
        val selectedMovie = intent.getSerializableExtra("selectedMovie") as Movie

        binding.nameText.text = selectedMovie.movieName
        binding.ratingBar.rating = selectedMovie.rating!!.toFloat()
        binding.directorName.text = selectedMovie.directoryName

        val byteArray = selectedMovie.image
        //Convert byteArray to bitmap for imageView
        if (byteArray != null){
            val imageBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
            binding.imageView.setImageBitmap(imageBitmap)
        }

    }
}