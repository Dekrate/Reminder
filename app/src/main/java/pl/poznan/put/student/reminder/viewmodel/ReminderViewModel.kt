package pl.poznan.put.student.reminder.viewmodel

import pl.poznan.put.student.reminder.database.entity.ReminderEntity
import pl.poznan.put.student.reminder.list.ReminderDto
import pl.poznan.put.student.reminder.repository.ReminderRepository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(State.DEFAULT)
    val uiState: Flow<State> = _uiState
    private val eventChannel = Channel<Event>(Channel.CONFLATED)

    init {
        viewModelScope.launch {
//            reminderRepository.initializeDatabaseIfNeeded()
            reminderRepository.getReminders().collect { reminder ->
                val mappedTrails = reminder.map { trail ->
                    ReminderDto(
                        id = trail.id,
                        title = trail.title,
                        datetime = trail.datetime,
                        isDone = trail.isDone
                    )
                }
                _uiState.update { state ->
                    state.copy(reminderDtos = mappedTrails)
                }
            }
        }
    }



    fun getReminderById(id: Int) {
        viewModelScope.launch {
            val selectedTrail = reminderRepository.getReminder(id)
            _uiState.update { state ->
                state.copy(selectedReminder = selectedTrail)
            }
        }
    }

    data class State(
        val reminderDtos: List<ReminderDto>,
        val selectedReminder: ReminderEntity?,
    ) {
        companion object {
            val DEFAULT = State(
                reminderDtos = emptyList(),
                selectedReminder = null,
            )
        }
    }

    fun updateReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            reminderRepository.updateReminder(reminder)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            reminderRepository.deleteAll()
        }
    }

    fun deleteById(id: Int) {
        viewModelScope.launch {
            reminderRepository.deleteById(id)
        }
    }

    fun onNameChange(it: String) {
        viewModelScope.launch {
            val selectedReminder = _uiState.value.selectedReminder
            selectedReminder?.let {
                val updatedTrail = it.copy(title = it.title)
                _uiState.update { state ->
                    state.copy(selectedReminder = updatedTrail)
                }
            }
        }
    }

    sealed class Event {

    }
}
