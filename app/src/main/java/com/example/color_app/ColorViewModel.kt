package com.example.color_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.color_app.roomdb.ColorItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

class ColorViewModel(private val repository: ColorRepository) : ViewModel() {
    private val _colors = MutableStateFlow<List<ColorItem>>(emptyList())
    val colors: StateFlow<List<ColorItem>> = _colors.asStateFlow()

    private val _unsyncedCount = MutableStateFlow(0)
    val unsyncedCount: StateFlow<Int> = _unsyncedCount.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncError = MutableStateFlow<String?>(null)
    val syncError: StateFlow<String?> = _syncError.asStateFlow()
    init {
        viewModelScope.launch {
            repository.getAllColors().collect { colorList ->
                _colors.value = colorList
            }
        }
        viewModelScope.launch {
            repository.getUnsyncedCount().collect { count ->
                _unsyncedCount.value = count
            }
        }
    }

    fun addNewColor() {
        viewModelScope.launch {
            val newColor = ColorItem(
                color = String.format("#%06X", Random.nextInt(0xFFFFFF + 1)),
                createdAt = System.currentTimeMillis(),
                isSynced = false
            )
            repository.insertColor(newColor)
        }
    }

    fun syncColors() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncError.value = null
            try {
                repository.syncColors()
                _unsyncedCount.value = repository.getUnsyncedCount().first()
            } catch (e: Exception) {
                _syncError.value = "Failed to sync: ${e.message}"
            } finally {
                _isSyncing.value = false
            }
        }
    }
}