package com.scrubele.scrubeleapp1.models

import com.google.gson.annotations.SerializedName


data class RobotModel(

        @SerializedName("url")
        var url: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("detection_algorithm")
        val detection_algorithm: String,
        @SerializedName("price")
        val size: String
)