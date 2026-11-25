package com.boxtraining.app.data.repository

import com.boxtraining.app.data.seed.BoxingCatalogSeed
import com.boxtraining.app.domain.model.Exercise
import com.boxtraining.app.domain.model.ExerciseCategory
import com.boxtraining.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Implementación del repositorio de entrenamientos.
 * Gestiona el catálogo completo de ejercicios/combinaciones y la rutina libre del usuario.
 */
class InMemoryWorkoutRepository : WorkoutRepository {

    private val _exercisesFlow = MutableStateFlow(BoxingCatalogSeed.defaultExercises())
    override val exercisesFlow: StateFlow<List<Exercise>> = _exercisesFlow.asStateFlow()

    private val _customSelectedExerciseIdsFlow = MutableStateFlow(
        listOf("combo_1", "combo_2", "lower_squats", "upper_pushups")
    )
    override val customSelectedExerciseIdsFlow: StateFlow<List<String>> =
        _customSelectedExerciseIdsFlow.asStateFlow()

    override fun getExercisesByCategory(category: ExerciseCategory): List<Exercise> {
        return _exercisesFlow.value.filter { it.category == category }
    }

    override fun getAllExercises(): List<Exercise> {
        return _exercisesFlow.value
    }

    override fun addCustomExercise(exercise: Exercise) {
        _exercisesFlow.update { current -> current + exercise }
    }

    override fun toggleExerciseInCustomRoutine(exerciseId: String) {
        _customSelectedExerciseIdsFlow.update { current ->
            if (current.contains(exerciseId)) {
                current - exerciseId
            } else {
                current + exerciseId
            }
        }
    }

    override fun clearCustomRoutine() {
        _customSelectedExerciseIdsFlow.value = emptyList()
    }

    override fun moveCustomExercise(fromIndex: Int, toIndex: Int) {
        _customSelectedExerciseIdsFlow.update { current ->
            if (fromIndex in current.indices && toIndex in current.indices) {
                val mutable = current.toMutableList()
                val item = mutable.removeAt(fromIndex)
                mutable.add(toIndex, item)
                mutable
            } else {
                current
            }
        }
    }
}
