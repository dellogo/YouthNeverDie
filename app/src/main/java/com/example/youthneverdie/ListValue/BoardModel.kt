package com.example.youthneverdie.ListValue

data class BoardModel (
    val title: String = "",
    val content : String = "",
    val uid : String = "",
    val time : String = "",
    val position : String = "",
    var starCount: Int = 0,
    var stars: MutableMap<String, Boolean> = HashMap(),

)