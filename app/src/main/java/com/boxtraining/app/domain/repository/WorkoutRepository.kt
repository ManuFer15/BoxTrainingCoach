package com.boxtraining.app.domain.repository

import com.boxtraining.app.domain.model.Exercise
import com.boxtraining.app.domain.model.ExerciseCategory
import kotlinx.coroutines.flow.StateFlow

/**
 * Contrato del repositorio de ejercicios, combinaciones y rutinas personalizadas.
 */
interface WorkoutRepository {
    val exercisesFlow: StateFlow<List<Exercise>>
    val customSelectedExerciseIdsFlow: StateFlow<List<String>>

    fun getExercisesByCategory(category: ExerciseCategory): List<Exercise>
    fun getAllExercises(): List<Exercise>
    fun addCustomExercise(exercise: Exercise)
    fun toggleExerciseInCustomRoutine(exerciseId: String)
    fun clearCustomRoutine()
    fun moveCustomExercise(fromIndex: Int, toIndex: Int)
}
