package com.mustafageldi.movieslibrary.view.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.mustafageldi.movieslibrary.databinding.ActivityMainBinding
import com.mustafageldi.movieslibrary.view.adapter.MovieAdapter
import com.mustafageldi.movieslibrary.view.model.Movie

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var movieList = ArrayList<Movie>()
    private lateinit var movieAdapter : MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, UploadActivity :: class.java)
            startActivity(intent)
        }

        getData()

        binding.recyclerView.layoutManager = GridLayoutManager(this,2)
        movieAdapter = MovieAdapter(movieList, this)
        binding.recyclerView.hasFixedSize()
        binding.recyclerView.adapter = movieAdapter
        
    }

    private fun getData(){
        try {
            val database = this@MainActivity.openOrCreateDatabase("Movies", MODE_PRIVATE,null)
            val cursor = database.rawQuery("SELECT * FROM movies",null)
            //Get column indexes
            val idIx = cursor.getColumnIndex("id")
            val nameIx = cursor.getColumnIndex("movieName")
            val ratingIx = cursor.getColumnIndex("rating")
            val directoryNameIx = cursor.getColumnIndex("directoryName")
            val imageIx = cursor.getColumnIndex("image")

            while (cursor.moveToNext()){
                val id = cursor.getInt(idIx)
                val name = cursor.getString(nameIx)
                val rating = cursor.getString(ratingIx)
                val directory = cursor.getString(directoryNameIx)
                val image = cursor.getBlob(imageIx)

                val movie = Movie(id,name,directory,rating,image)
                movieList.add(movie)
            }

            cursor.close()

        }catch (e : Exception){
            e.printStackTrace()
        }
    }


}