package com.mustafageldi.movieslibrary.view.adapter

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.mustafageldi.movieslibrary.R
import com.mustafageldi.movieslibrary.databinding.RecyclerRowBinding
import com.mustafageldi.movieslibrary.view.model.Movie
import com.mustafageldi.movieslibrary.view.view.DetailsActivity

class MovieAdapter(private val movieList : ArrayList<Movie>, private val context : Context) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    class ViewHolder(val binding : RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.recyclerMovieName.text = movieList[position].movieName
        holder.binding.ratingText.text = movieList[position].rating

        //Convert byteArray to bitmap for imageView -->>
        val imageBitmap = BitmapFactory.decodeByteArray(movieList[position].image,0,movieList[position].image!!.size)
        holder.binding.recyclerImageView.setImageBitmap(imageBitmap)

        holder.itemView.setOnClickListener{
            val selectedMovie = movieList[position]
            val intent = Intent(context,DetailsActivity :: class.java)
            intent.putExtra("selectedMovie",selectedMovie)
            context.startActivity(intent)
        }

        holder.binding.recyclerViewDeleteIcon.setOnClickListener{
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle("Delete Movie")
            alertDialog.setMessage("Are you sure you want to delete the movie " + movieList[position].movieName)
            alertDialog.setIcon(R.drawable.delete_icon)
            alertDialog.setPositiveButton("Yes"){ _, _ ->
                //Delete this movie -->>
                try {
                    val database = context.openOrCreateDatabase("Movies", MODE_PRIVATE,null)
                    database.execSQL("DELETE FROM movies WHERE id = ?", arrayOf(movieList[position].id.toString()))
                    movieList.removeAt(position)
                    notifyDataSetChanged()
                    Toast.makeText(context,"Movie is deleted.",Toast.LENGTH_LONG).show()
                }catch (e : Exception){
                    println(e.message)
                }

            }
            alertDialog.setNegativeButton("No"){ _, _ ->

            }
            alertDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

}