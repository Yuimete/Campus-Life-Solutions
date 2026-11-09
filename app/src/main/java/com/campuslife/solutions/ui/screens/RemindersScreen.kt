package com.campuslife.solutions.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.campuslife.solutions.data.local.Assignment
import com.campuslife.solutions.ui.theme.*
import com.campuslife.solutions.viewmodel.AssignmentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    viewModel: AssignmentViewModel = hiltViewModel()
) {
    val assignments by viewModel.assignments.collectAsState()
    val upcomingAssignments = assignments.filter { !it.isCompleted && it.dueDateTime > System.currentTimeMillis() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reminders", color = White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Navy)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppBackground),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Active Reminders",
                    style = MaterialTheme.typography.titleMedium,
                    color = Navy,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(upcomingAssignments) { assignment ->
                ReminderItem(assignment)
            }

            if (upcomingAssignments.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No active reminders", color = Navy)
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderItem(assignment: Assignment) {
    val reminderTime = assignment.dueDateTime - assignment.reminderOffset
    val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
                Text(
                    text = "Reminder set for: ${dateFormat.format(Date(reminderTime))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = BrightBlue
                )
            }
            Switch(
                checked = true,
                onCheckedChange = { },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = White,
                    checkedTrackColor = RoyalBlue
                )
            )
        }
    }
}
