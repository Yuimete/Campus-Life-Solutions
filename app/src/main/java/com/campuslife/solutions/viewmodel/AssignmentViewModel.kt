package com.campuslife.solutions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campuslife.solutions.data.AssignmentRepository
import com.campuslife.solutions.data.local.Assignment
import com.campuslife.solutions.data.local.Priority
import com.campuslife.solutions.worker.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val repository: AssignmentRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow(AssignmentFilter.ALL)
    val filter = _filter.asStateFlow()

    val assignments = combine(
        repository.getAllAssignments(),
        searchQuery,
        filter
    ) { list, query, filter ->
        list.filter {
            it.title.contains(query, ignoreCase = true) || it.subject.contains(query, ignoreCase = true)
        }.filter {
            when (filter) {
                AssignmentFilter.ALL -> true
                AssignmentFilter.TODAY -> isToday(it.dueDateTime)
                AssignmentFilter.THIS_WEEK -> isThisWeek(it.dueDateTime)
                AssignmentFilter.OVERDUE -> !it.isCompleted && it.dueDateTime < System.currentTimeMillis()
                AssignmentFilter.COMPLETED -> it.isCompleted
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: AssignmentFilter) {
        _filter.value = filter
    }

    fun toggleCompletion(assignment: Assignment) {
        viewModelScope.launch {
            repository.updateAssignment(assignment.copy(isCompleted = !assignment.isCompleted))
        }
    }

    fun saveAssignment(
        id: Int = 0,
        title: String,
        subject: String,
        description: String,
        dueDateTime: Long,
        priority: Priority,
        reminderOffset: Long
    ) {
        viewModelScope.launch {
            val assignment = Assignment(
                id = id,
                title = title,
                subject = subject,
                description = description,
                dueDateTime = dueDateTime,
                priority = priority,
                reminderOffset = reminderOffset
            )
            if (id == 0) {
                val newId = repository.insertAssignment(assignment)
                alarmScheduler.schedule(assignment.copy(id = newId.toInt()))
            } else {
                repository.updateAssignment(assignment)
                alarmScheduler.schedule(assignment)
            }
        }
    }

    fun deleteAssignment(assignment: Assignment) {
        viewModelScope.launch {
            repository.deleteAssignment(assignment)
            alarmScheduler.cancel(assignment.id)
        }
    }

    private fun isToday(timestamp: Long): Boolean {
        val now = java.util.Calendar.getInstance()
        val target = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
        return now.get(java.util.Calendar.YEAR) == target.get(java.util.Calendar.YEAR) &&
                now.get(java.util.Calendar.DAY_OF_YEAR) == target.get(java.util.Calendar.DAY_OF_YEAR)
    }

    private fun isThisWeek(timestamp: Long): Boolean {
        val now = java.util.Calendar.getInstance()
        val target = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
        return now.get(java.util.Calendar.YEAR) == target.get(java.util.Calendar.YEAR) &&
                now.get(java.util.Calendar.WEEK_OF_YEAR) == target.get(java.util.Calendar.WEEK_OF_YEAR)
    }
}

enum class AssignmentFilter {
    ALL, TODAY, THIS_WEEK, OVERDUE, COMPLETED
}
