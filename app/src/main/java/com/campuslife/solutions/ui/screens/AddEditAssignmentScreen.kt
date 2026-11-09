package com.campuslife.solutions.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.campuslife.solutions.data.local.Assignment
import com.campuslife.solutions.data.local.Priority
import com.campuslife.solutions.ui.theme.Navy
import com.campuslife.solutions.ui.theme.RoyalBlue
import com.campuslife.solutions.ui.theme.White
import com.campuslife.solutions.viewmodel.AssignmentViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.saveable.rememberSaveable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAssignmentScreen(
    assignmentId: Int? = null,
    onBack: () -> Unit,
    viewModel: AssignmentViewModel = hiltViewModel()
) {
    val assignments by viewModel.assignments.collectAsState()
    val existingAssignment = remember(assignmentId, assignments) {
        assignments.find { it.id == assignmentId }
    }
    
    var title by rememberSaveable(existingAssignment) { mutableStateOf(existingAssignment?.title ?: "") }
    var subject by rememberSaveable(existingAssignment) { mutableStateOf(existingAssignment?.subject ?: "") }
    var description by rememberSaveable(existingAssignment) { mutableStateOf(existingAssignment?.description ?: "") }
    var priority by rememberSaveable(existingAssignment) { mutableStateOf(existingAssignment?.priority ?: Priority.MEDIUM) }
    
    var dueDate by rememberSaveable(existingAssignment) { 
        mutableStateOf(
            existingAssignment?.dueDateTime ?: Calendar.getInstance().apply {
                add(Calendar.HOUR_OF_DAY, 1)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        )
    }
    var reminderOffset by rememberSaveable(existingAssignment) { mutableStateOf(existingAssignment?.reminderOffset ?: 0L) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dueDate)
    var showDatePicker by remember { mutableStateOf(false) }

    val calendar = remember(dueDate) { 
        Calendar.getInstance().apply { timeInMillis = dueDate } 
    }
    
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (assignmentId == null) "Add Assignment" else "Edit Assignment", color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Navy)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject/Course") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text("Priority", style = MaterialTheme.typography.titleMedium, color = White)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Priority.values().forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(p.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalBlue,
                            selectedLabelColor = White
                        )
                    )
                }
            }

            Text("Due Date & Time", style = MaterialTheme.typography.titleMedium, color = White)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) {
                    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    Text(sdf.format(Date(dueDate)))
                }

                Button(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) {
                    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    Text(sdf.format(Date(dueDate)))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (title.isNotBlank() && subject.isNotBlank()) {
                        viewModel.saveAssignment(
                            id = assignmentId ?: 0,
                            title = title,
                            subject = subject,
                            description = description,
                            dueDateTime = dueDate,
                            priority = priority,
                            reminderOffset = reminderOffset
                        )
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
            ) {
                Text("Save Assignment", modifier = Modifier.padding(8.dp))
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedMillis ->
                        val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                            timeInMillis = selectedMillis
                        }
                        val newCal = Calendar.getInstance().apply {
                            timeInMillis = dueDate
                            set(Calendar.YEAR, utcCal.get(Calendar.YEAR))
                            set(Calendar.MONTH, utcCal.get(Calendar.MONTH))
                            set(Calendar.DAY_OF_MONTH, utcCal.get(Calendar.DAY_OF_MONTH))
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        dueDate = newCal.timeInMillis
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE)
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val newCal = Calendar.getInstance().apply { 
                        timeInMillis = dueDate
                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(Calendar.MINUTE, timePickerState.minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    dueDate = newCal.timeInMillis
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}
