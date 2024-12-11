package com.example.myfitnessapp.model

data class User(
    val id: String?,
    val userId: String?,
    val displayName: String?,
    val avatarUrl: String?,
    val quote: String?,// cita
    val profession: String?
) {
    fun toMap(): MutableMap<String, String?> { // funcion que obtiene los pares
        return mutableMapOf(
            "user_Id" to this.userId,
            "display_name" to this.displayName,
            "profession" to this.profession,
            "quote" to this.quote,
            "avatar_url" to this.avatarUrl
        )
    }
}