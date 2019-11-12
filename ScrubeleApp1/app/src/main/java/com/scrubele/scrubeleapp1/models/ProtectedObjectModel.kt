package com.scrubele.scrubeleapp1.models

import com.google.gson.annotations.SerializedName

data class ProtectedObjectModel(

    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("size")
    val size: String,
    @SerializedName("robots")
    val robots: ArrayList<String>,
    @SerializedName("photo")
    val photo: String
)