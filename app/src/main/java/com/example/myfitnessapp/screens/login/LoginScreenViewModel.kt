package com.example.myfitnessapp.screens.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfitnessapp.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)

    //cerrar sesión
    fun signOut() {
        try {
            auth.signOut()
            Log.d("MyLogin", "Usuario cerrado sesión exitosamente")
        } catch (ex: Exception) {
            Log.d("MyLogin", "Error al cerrar sesión: ${ex.message}")
        }
    }

    fun signInWithGoogleCredential(credential: AuthCredential, home: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("MyLogin", "Google logueado!!!!")
                            home()
                        } else {
                            Log.d("MyLogin", "signInWithGoogle: ${task.result.toString()}")
                        }
                    }
            } catch (ex: Exception) {
                Log.d("MyLogin", "Error al loguear con Google: ${ex.message}")
            }
        }



    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("MyLogin", "signInWithEmailAndPassword logueado!!!!")
                            home()
                        } else {
                            Log.d("MyLogin", "signInWithEmailAndPassword: ${task.result.toString()}")
                        }
                    }
            } catch (ex: Exception) {
                Log.d("MyLogin", "signInWithEmailAndPassword: ${ex.message}")
            }
        }

    fun createUserWithEmailAndPassword(email: String, password: String, home: () -> Unit) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val displayName = task.result.user?.email?.split("@")?.get(0)
                        createUser(displayName)
                        home()
                    } else {
                        Log.d("MyLogin", "createUserWithEmailAndPassword: ${task.result.toString()}")
                    }
                    _loading.value = false
                }
        }
    }

    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid
        val user = User(
            userId = userId.toString(),
            displayName = displayName.toString(),
            avatarUrl = "",
            quote = "lo dificil ya pasó",
            profession = "Android Dev",
            id = null
        ).toMap()

        FirebaseFirestore.getInstance()
            .collection("users")
            .add(user)
            .addOnSuccessListener {
                Log.d("MyLogin", "Creado ${it.id}")
            }
            .addOnFailureListener {
                Log.d("MyLogin", "Ocurrió Error: ${it}")
            }
    }
}
