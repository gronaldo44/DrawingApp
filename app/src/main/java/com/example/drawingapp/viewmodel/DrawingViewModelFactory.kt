package com.example.drawingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.drawingapp.model.database.DrawingRepository

/**
 * Factory class for creating instances of [DrawingViewModel].
 * Provides a way to pass parameters to the ViewModel class.
 *
 * @param repository The repository to be used by the ViewModel.
 */
class DrawingViewModelFactory(private val repository: DrawingRepository,
                              private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    /**
     * Creates a new instance of the specified [ViewModel].
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A newly created instance of the ViewModel.
     * @throws IllegalArgumentException if the ViewModel class is unknown.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DrawingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DrawingViewModel(repository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}