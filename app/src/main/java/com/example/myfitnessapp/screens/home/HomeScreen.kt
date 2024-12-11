package com.example.myfitnessapp.screens.home

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfitnessapp.model.Exercise
import com.example.myfitnessapp.model.Routine
import com.example.myfitnessapp.model.Series
import com.example.myfitnessapp.navigation.Screens
import com.example.myfitnessapp.repository.RoutineRepository
import com.example.myfitnessapp.screens.login.LoginScreenViewModel
import java.util.UUID

@Composable
fun Home(navController: NavController, viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    var routines by remember { mutableStateOf(mutableListOf<Routine>()) }
    var selectedRoutine by remember { mutableStateOf<Routine?>(null) }

    LaunchedEffect(Unit) {
        RoutineRepository.getRoutinesFromFirebase(
            onResult = { routinesList ->
                routines = routinesList.toMutableList()
            },
            onFailure = { exception ->
                Log.e("Firebase", "Error cargando rutinas", exception)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = {
                val newRoutine = Routine(id = UUID.randomUUID().toString(), name = "Rutina ${routines.size + 1}")
                RoutineRepository.saveRoutineToFirebase(
                    newRoutine,
                    onSuccess = {
                        routines.add(newRoutine)
                        selectedRoutine = newRoutine
                    },
                    onFailure = { exception ->
                        Log.e("Firebase", "Error guardando nueva rutina", exception)
                    }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Nueva Rutina", color = MaterialTheme.colorScheme.onPrimary)
        }

        // Botón para ver todas las rutinas
        Button(
            onClick = { navController.navigate(Screens.RoutineScreen.name) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Ver Todas las Rutinas", color = MaterialTheme.colorScheme.onSecondary)
        }

        Text(
            text = "MIS RUTINAS",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        routines.forEach { routine ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surfaceTint, shape = RoundedCornerShape(8.dp))
                    .clickable { selectedRoutine = routine }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(routine.name, style = MaterialTheme.typography.bodyLarge)
                IconButton(
                    onClick = {
                        RoutineRepository.deleteRoutineFromFirebase(
                            routine.id,
                            onSuccess = {
                                routines = routines.filterNot { it == routine }.toMutableList()
                                if (selectedRoutine == routine) selectedRoutine = null
                            },
                            onFailure = { exception ->
                                Log.e("Firebase", "Error eliminando rutina", exception)
                            }
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar rutina",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        selectedRoutine?.let { routine ->
            RoutineEditor(
                routine = routine,
                onRoutineUpdated = { updatedRoutine ->
                    routines = routines.map { if (it.id == updatedRoutine.id) updatedRoutine else it }.toMutableList()
                }
            )

            Button(
                onClick = {
                    if (routine.id.isEmpty()) {
                        RoutineRepository.saveRoutineToFirebase(
                            routine,
                            onSuccess = { routines.add(routine) },
                            onFailure = { exception ->
                                Log.e("Firebase", "Error guardando rutina", exception)
                            }
                        )
                    } else {
                        RoutineRepository.updateRoutineInFirebase(
                            routine,
                            onSuccess = {
                                Log.d("Firebase", "Rutina actualizada correctamente")
                            },
                            onFailure = { exception ->
                                Log.e("Firebase", "Error actualizando rutina", exception)
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Guardar Rutina en Firebase", color = MaterialTheme.colorScheme.onPrimary)
            }
        }

        Button(
            onClick = {
                viewModel.signOut()
                navController.navigate("LoginScreen") {
                    popUpTo(0)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cerrar Sesión", color = MaterialTheme.colorScheme.onError)
        }
    }
}




@Composable
fun RoutineEditor(
    routine: Routine,
    onRoutineUpdated: (Routine) -> Unit
) {
    // Usamos un estado local para el nombre de la rutina
    var routineName by remember { mutableStateOf(routine.name) }

    LaunchedEffect(routine) {
        routineName = routine.name // Actualiza el estado cuando se selecciona una nueva rutina
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        OutlinedTextField(
            value = routineName,
            onValueChange = {
                routineName = it
                routine.name = it
                onRoutineUpdated(routine)
            },
            label = { Text("Nombre de la Rutina") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                routine.exercises.add(Exercise(name = "Nuevo Ejercicio"))
                onRoutineUpdated(routine)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir Ejercicio")
        }

        Spacer(modifier = Modifier.height(16.dp))

        routine.exercises.forEachIndexed { index, exercise ->
            ExerciseEditor(
                exercise = exercise,
                onExerciseUpdated = { updatedExercise ->

                    routine.exercises[index] = updatedExercise
                    onRoutineUpdated(routine)
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}




@SuppressLint("RememberReturnType")
@Composable
fun ExerciseEditor(
    exercise: Exercise,
    onExerciseUpdated: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {

    var exerciseName by remember { mutableStateOf(exercise.name) }

    val seriesStates = remember { mutableStateListOf<MutableState<Series>>() }

    // Inicializar los estados de las series si es la primera vez
    LaunchedEffect(exercise.series) {

        seriesStates.clear()
        exercise.series.forEach { series ->

            seriesStates.add(mutableStateOf(series))
        }
    }

    LaunchedEffect(exercise) {
        exerciseName = exercise.name
    }

    Column(modifier = modifier.fillMaxWidth()) {

        OutlinedTextField(
            value = exerciseName,
            onValueChange = {
                exerciseName = it
                exercise.name = it
                onExerciseUpdated(exercise)
            },
            label = { Text("Nombre del Ejercicio") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para añadir series
        Button(
            onClick = {
                val newSeries = Series() // Crear una nueva serie
                exercise.series.add(newSeries)
                seriesStates.add(mutableStateOf(newSeries))
                onExerciseUpdated(exercise)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir Serie")
        }

        Spacer(modifier = Modifier.height(16.dp))


        seriesStates.forEachIndexed { index, seriesState ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Campo para repeticiones con estado local
                var repetitions by remember { mutableStateOf(seriesState.value.repetitions) }
                OutlinedTextField(
                    value = repetitions.toString(),
                    onValueChange = { value ->
                        repetitions = value.toIntOrNull() ?: 0
                        seriesState.value.repetitions = repetitions
                        exercise.series[index] = seriesState.value
                        onExerciseUpdated(exercise)
                    },
                    label = { Text("Repeticiones") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Campo para peso con estado local
                var weight by remember { mutableStateOf(seriesState.value.weight) }
                OutlinedTextField(
                    value = weight.toString(),
                    onValueChange = { value ->
                        weight = value.toFloatOrNull() ?: 0f // Actualiza el valor de peso
                        seriesState.value.weight = weight // Modifica la propiedad de la serie
                        exercise.series[index] = seriesState.value // Actualiza la serie original
                        onExerciseUpdated(exercise) // Notificar al editor que el ejercicio ha sido actualizado
                    },
                    label = { Text("Peso (kg)") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        exercise.series.removeAt(index)
                        seriesStates.removeAt(index)
                        onExerciseUpdated(exercise) // Notificar que el ejercicio ha sido actualizado
                    }
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar serie")
                }

            }
        }
    }
}









