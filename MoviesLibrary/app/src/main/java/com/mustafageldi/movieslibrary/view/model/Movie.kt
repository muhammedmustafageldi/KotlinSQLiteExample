package com.mustafageldi.movieslibrary.view.model

class Movie(id : Int, movieName : String, directoryName : String, rating : String, image : ByteArray) : java.io.Serializable {

    var id : Int? = id
    var movieName : String? = movieName
    var directoryName : String? = directoryName
    var rating : String? = rating
    var image : ByteArray? = image
}