package com.example.myfitnessapp.training

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfitnessapp.model.Routine
import com.example.myfitnessapp.repository.RoutineRepository
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

@Composable
fun TrainingScreen(navController: NavController) {
    val routinesState = remember { mutableStateOf<List<Routine>>(emptyList()) }
    val completedTrainingsState = remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    // Cargar rutinas desde Firebase
    LaunchedEffect(Unit) {
        RoutineRepository.getRoutinesFromFirebase(
            onResult = { routines -> routinesState.value = routines },
            onFailure = { exception -> Log.e("Firebase", "Error cargando rutinas: ", exception) }
        )
    }

    // Cargar entrenamientos completados desde Firebase
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance()
            .collection("training")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val trainings = querySnapshot.documents.map { it.data ?: emptyMap<String, Any>() }
                completedTrainingsState.value = trainings
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error cargando entrenamientos completados: ", exception)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Botón para volver
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Volver a las rutinas", color = MaterialTheme.colorScheme.onSecondary)
        }

        Text(
            text = "Selecciona Rutina para Entrenar",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // Lista de rutinas
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(routinesState.value) { routine ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Rutina: ${routine.name}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Button(
                            onClick = { navController.navigate("TrainingDetailScreen/${routine.id}") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Iniciar Entrenamiento", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }

        if (completedTrainingsState.value.isNotEmpty()) {
            Text(
                text = "Entrenamientos Completados",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Lista de entrenamientos completados
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(completedTrainingsState.value) { training ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Rutina: ${training["name"] as? String ?: "Desconocida"}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Fecha: ${Date(training["timestamp"] as? Long ?: 0).toString()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val notes = training["notes"] as? String ?: "Sin notas"
                        Text(
                            text = "Notas: $notes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}


