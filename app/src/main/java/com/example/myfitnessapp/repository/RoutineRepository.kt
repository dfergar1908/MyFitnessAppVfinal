package com.example.myfitnessapp.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import com.example.myfitnessapp.model.Exercise
import com.example.myfitnessapp.model.Routine
import com.example.myfitnessapp.model.Series
import com.google.firebase.firestore.FirebaseFirestore

object RoutineRepository {
    private val db = FirebaseFirestore.getInstance()

    // Guardar rutina en Firebase
    fun saveRoutineToFirebase(
        routine: Routine,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val routineData = hashMapOf(
            "id" to routine.id,
            "name" to routine.name,
            "exercises" to routine.exercises.map { exercise ->
                hashMapOf(
                    "name" to exercise.name,
                    "series" to exercise.series.map { series ->
                        hashMapOf(
                            "repetitions" to series.repetitions,
                            "weight" to series.weight
                        )
                    }
                )
            }
        )

        db.collection("routines").document(routine.id)
            .set(routineData)
            .addOnSuccessListener {
                onSuccess() // Llamamos al éxito al guardar correctamente
            }
            .addOnFailureListener { exception ->
                onFailure(exception) // En caso de error
            }
    }

    // Leer rutinas desde Firebase
    fun getRoutinesFromFirebase(
        onResult: (List<Routine>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("routines").get()
            .addOnSuccessListener { snapshot ->
                val routines = snapshot.documents.mapNotNull { document ->
                    try {
                        val id = document.getString("id") ?: return@mapNotNull null
                        val name = document.getString("name") ?: "Sin Nombre"

                        val exercises = (document.get("exercises") as? List<Map<String, Any>>)?.map { exercise ->
                            Exercise(
                                name = exercise["name"] as? String ?: "Sin Nombre",
                                series = (exercise["series"] as? List<Map<String, Any>>)?.map { series ->
                                    Series(
                                        repetitions = (series["repetitions"] as? Long)?.toInt() ?: 0,
                                        weight = (series["weight"] as? Double)?.toFloat() ?: 0f
                                    )
                                }?.toMutableList() ?: mutableListOf()
                            )
                        }?.toMutableList() ?: mutableListOf()

                        Routine(id = id, name = name, exercises = exercises)
                    } catch (e: Exception) {
                        Log.e("Firebase", "Error parsing document: ${e.message}")
                        null
                    }
                }
                onResult(routines)
            }
            .addOnFailureListener { exception ->
                onFailure(exception) // Manejo de errores
            }
    }

    fun updateRoutineInFirebase(
        routine: Routine,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        saveRoutineToFirebase(routine, onSuccess, onFailure)
    }

    fun deleteRoutineFromFirebase(
        routineId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("routines").document(routineId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}



