package com.mustafageldi.movieslibrary.view.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.mustafageldi.movieslibrary.databinding.ActivityUploadBinding
import java.io.ByteArrayOutputStream

class UploadActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUploadBinding
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var intentLauncher : ActivityResultLauncher<Intent>
    private var selectedBitmap : Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerLauncher()
    }


    fun selectImage(view : View){
        if (ContextCompat.checkSelfPermission(this@UploadActivity,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //No permission. Request it -->>
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@UploadActivity,Manifest.permission.READ_EXTERNAL_STORAGE)){
                //Require rationale -->
                Snackbar.make(view,"Permission Need For Gallery.",Snackbar.LENGTH_INDEFINITE).setAction("Give permission"){
                    //Request Permission -->>
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }else{
                //Request Permission -->>
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        }else{
            //Request granted. Go to gallery. -->>
            val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intentLauncher.launch(intentToGallery)
        }
    }

    fun save(view : View){
        val rating = binding.ratingBar.rating.toString()
        val movieName = binding.movieNameText.text.toString()
        val directoryName = binding.movieDirectoryText.text.toString()

        if(selectedBitmap != null){
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)
            //Convert bitmap to byteArray -->>
            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray = outputStream.toByteArray()

            //Create Database and Include data -->
            try {
                val database = this@UploadActivity.openOrCreateDatabase("Movies", MODE_PRIVATE,null)
                database.execSQL("CREATE TABLE IF NOT EXISTS movies (id INTEGER PRIMARY KEY, movieName VARCHAR, rating VARCHAR, directoryName VARCHAR, image BLOB)")

                val sqlString = "INSERT INTO movies (movieName, rating, directoryName, image) VALUES (?, ?, ?, ?)"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1,movieName)
                statement.bindString(2,rating)
                statement.bindString(3,directoryName)
                statement.bindBlob(4,byteArray)
                statement.execute()

                //Save transaction is success.
                Toast.makeText(this@UploadActivity,"Saved.",Toast.LENGTH_LONG).show()

            }catch (e : Exception){
                println(e.message)
            }

            val intent = Intent(this@UploadActivity, MainActivity :: class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    private fun makeSmallerBitmap(image : Bitmap, maximumSize : Int) : Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1){
            //Landscape
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        }else{
            //Portrait
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }

        return Bitmap.createScaledBitmap(image,width,height,true)
    }

    private fun registerLauncher(){
        intentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if (intentFromResult != null){
                    val imageData =intentFromResult.data
                    if (imageData != null){

                        try {
                            if (Build.VERSION.SDK_INT >=28){
                                //Convert uri to Bitmap for sdk level >= 28
                                val source = ImageDecoder.createSource(this@UploadActivity.contentResolver,imageData)
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.selectDescriptionText.visibility = View.GONE
                                binding.movieImage.setImageBitmap(selectedBitmap)
                            }else{
                                //Convert transaction for sdk level < 28
                                selectedBitmap = MediaStore.Images.Media.getBitmap(this@UploadActivity.contentResolver,imageData)
                                binding.movieImage.setImageBitmap(selectedBitmap)
                                binding.selectDescriptionText.visibility = View.GONE
                            }
                        }catch (e : Exception){
                            println(e.message)
                        }

                    }else{
                        Toast.makeText(this@UploadActivity,"An error occurred while uploading the photo.",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if (result){
                //Permission Granted.
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intentLauncher.launch(intentToGallery)
            }else{
                //Permission Denied
                Toast.makeText(this@UploadActivity,"Permission Needed!",Toast.LENGTH_LONG).show()
            }
        }

    }


}