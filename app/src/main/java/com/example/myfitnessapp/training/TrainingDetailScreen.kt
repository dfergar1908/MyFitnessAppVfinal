package com.example.myfitnessapp.training

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfitnessapp.model.Exercise
import com.example.myfitnessapp.model.Routine
import com.example.myfitnessapp.model.Series
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingDetailsScreen(navController: NavController) {
    val context = LocalContext.current
    val timerState = remember { mutableStateOf(0L) }
    val isTimerRunning = remember { mutableStateOf(true) }
    val trainingNotes = remember { mutableStateOf("") }
    val currentRoutine = remember { mutableStateOf<Routine?>(null) }
    var newExerciseName by remember { mutableStateOf("") }
    val showEndTrainingDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        currentRoutine.value = Routine(
            name = "Rutina de Ejemplo",
            exercises = mutableListOf(
                Exercise(name = "Sentadilla", series = mutableListOf(Series(10, 50f))),
                Exercise(name = "Press Banca", series = mutableListOf(Series(8, 70f)))
            )
        )
    }

    LaunchedEffect(isTimerRunning.value) {
        if (isTimerRunning.value) {
            while (true) {
                delay(1000L)
                timerState.value += 1000L
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Entrenamiento en Curso") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Tiempo transcurrido: ${timerState.value / 1000}s",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                currentRoutine.value?.let { routine ->
                    items(routine.exercises) { exercise ->
                        var exerciseName by remember { mutableStateOf(exercise.name) }
                        val seriesStates = remember { mutableStateListOf<MutableState<Series>>() }

                        LaunchedEffect(exercise.series) {
                            seriesStates.clear()
                            exercise.series.forEach { series ->
                                seriesStates.add(mutableStateOf(series))
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                OutlinedTextField(
                                    value = exerciseName,
                                    onValueChange = { newName ->
                                        exerciseName = newName
                                        exercise.name = newName
                                        currentRoutine.value = routine // Propagar cambios
                                    },
                                    label = { Text("Nombre del Ejercicio") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Button(
                                    onClick = {
                                        val newSeries = Series()
                                        exercise.series.add(newSeries)
                                        seriesStates.add(mutableStateOf(newSeries))
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Añadir Serie")
                                }

                                seriesStates.forEachIndexed { index, seriesState ->
                                    val series = seriesState.value

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        var repetitions by remember { mutableStateOf(series.repetitions) }
                                        var weight by remember { mutableStateOf(series.weight) }

                                        OutlinedTextField(
                                            value = repetitions.toString(),
                                            onValueChange = {
                                                repetitions = it.toIntOrNull() ?: 0
                                                seriesState.value = series.copy(repetitions = repetitions)
                                                exercise.series[index] = seriesState.value
                                                currentRoutine.value = routine
                                            },
                                            label = { Text("Repeticiones") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        OutlinedTextField(
                                            value = weight.toString(),
                                            onValueChange = {
                                                weight = it.toFloatOrNull() ?: 0f
                                                seriesState.value = series.copy(weight = weight)
                                                exercise.series[index] = seriesState.value
                                                currentRoutine.value = routine
                                            },
                                            label = { Text("Peso (kg)") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(onClick = {
                                            exercise.series.removeAt(index)
                                            seriesStates.removeAt(index)
                                            currentRoutine.value = routine
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar Serie")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = trainingNotes.value,
                        onValueChange = { trainingNotes.value = it },
                        label = { Text("Notas del Entrenamiento") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Botón para finalizar el entrenamiento
                item {
                    Button(
                        onClick = { showEndTrainingDialog.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Finalizar Entrenamiento", color = MaterialTheme.colorScheme.onError)
                    }
                }
            }
        }
    )

    if (showEndTrainingDialog.value) {
        AlertDialog(
            onDismissRequest = { showEndTrainingDialog.value = false },
            title = { Text("Finalizar Entrenamiento") },
            text = { Text("¿Estás seguro de que deseas finalizar el entrenamiento?") },
            confirmButton = {
                Button(onClick = {
                    showEndTrainingDialog.value = false
                    currentRoutine.value?.let {
                        val training = hashMapOf(
                            "routineId" to it.id,
                            "name" to it.name,
                            "exercises" to it.exercises,
                            "notes" to trainingNotes.value,
                            "timestamp" to System.currentTimeMillis()
                        )
                        FirebaseFirestore.getInstance().collection("training").add(training)
                            .addOnSuccessListener {
                                navController.popBackStack()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error guardando: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                Button(onClick = { showEndTrainingDialog.value = false }) {
                    Text("No")
                }
            }
        )
    }
}













