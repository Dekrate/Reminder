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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.poznan.put.student.reminder.database.entity.SettingsEntity
import pl.poznan.put.student.reminder.list.SettingsDto
import pl.poznan.put.student.reminder.repository.SettingsRepository
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val settingsRepository: SettingsRepository
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
                        date = trail.date,
                        time = trail.time,
                        isDone = trail.isDone
                    )
                }
                _uiState.update { state ->
                    state.copy(reminderDtos = mappedTrails)
                }
            }
            val mappedSettings = settingsRepository.getSettings().map { settings ->
                SettingsDto(
                    fingerprint = settings.fingerprint
                )
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
        val settingsDto: SettingsDto
    ) {
        companion object {
            val DEFAULT = State(
                reminderDtos = emptyList(),
                selectedReminder = null,
                settingsDto = SettingsDto(fingerprint = false)
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

    fun insertReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            reminderRepository.insertReminder(reminder)
        }
    }

    fun updateSettings(settingsEntity: SettingsEntity) {
        viewModelScope.launch {
            settingsRepository.updateSettings(settingsEntity)
        }
    }


    sealed class Event {

    }
}
