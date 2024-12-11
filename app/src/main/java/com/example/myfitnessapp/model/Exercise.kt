package com.example.myfitnessapp.model

data class Exercise(
    var name: String = "",
    var series: MutableList<Series> = mutableListOf()
)

data class Series(
    var repetitions: Int = 0,
    var weight: Float = 0f
)