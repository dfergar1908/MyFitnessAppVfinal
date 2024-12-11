package com.example.myfitnessapp.model

import java.util.UUID

data class Routine(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var exercises: MutableList<Exercise> = mutableListOf()
)