package kz.taskbar.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kz.taskbar.app.TaskBarApplication
import kz.taskbar.app.diagnostics.TaskOperationTracker
import kz.taskbar.app.data.DailyQuote
import kz.taskbar.app.data.PreferencesRepository
import kz.taskbar.app.data.QuoteRepository
import kz.taskbar.app.data.TaskEntity
import kz.taskbar.app.data.TaskRepository
import kz.taskbar.app.domain.TaskDraft
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job

class TaskViewModel(
    private val repository: TaskRepository,
    private val preferences: PreferencesRepository,
    private val quotes: QuoteRepository,
) : ViewModel() {
    val tasks: StateFlow<List<TaskEntity>> = repository.tasks.stateIn(viewModelScope,
        SharingStarted.WhileSubscribed(5_000), emptyList())
    val isDark: StateFlow<Boolean> = preferences.isDarkTheme.stateIn(viewModelScope,
        SharingStarted.WhileSubscribed(5_000), true)

    private val _quote = MutableStateFlow(DailyQuote(
        "Большие результаты начинаются с маленьких действий.", "TaskBar", false))
    val quote: StateFlow<DailyQuote> = _quote

    init {
        tracked { repository.populateIfFirstLaunch() }
        refreshQuote()
    }

    private fun tracked(block: suspend () -> Unit): Job = viewModelScope.launch(
        start = CoroutineStart.UNDISPATCHED,
    ) {
        TaskOperationTracker.begin()
        try { block() } finally { TaskOperationTracker.end() }
    }

    fun save(draft: TaskDraft) = tracked { repository.save(draft) }
    fun toggle(task: TaskEntity) = tracked { repository.setCompleted(task, !task.completed) }
    fun delete(task: TaskEntity) = tracked { repository.delete(task) }
    fun clearCompleted() = tracked { repository.clearCompleted() }
    fun setDark(enabled: Boolean) = tracked { preferences.setDarkTheme(enabled) }
    fun refreshQuote() = tracked { _quote.value = quotes.getQuote() }

    class Factory(private val app: TaskBarApplication) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            TaskViewModel(app.repository, app.preferences, app.quoteRepository) as T
    }
}
