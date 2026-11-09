package com.campuslife.solutions.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.campuslife.solutions.data.local.Assignment
import com.campuslife.solutions.data.local.Priority
import com.campuslife.solutions.ui.theme.*
import com.campuslife.solutions.viewmodel.AssignmentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentDetailScreen(
    assignmentId: Int,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    viewModel: AssignmentViewModel = hiltViewModel()
) {
    val assignments by viewModel.assignments.collectAsState()
    val assignment = remember(assignments, assignmentId) {
        assignments.find { it.id == assignmentId }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assignment Details", color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                actions = {
                    assignment?.let {
                        IconButton(onClick = { onEdit(it.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = White)
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Navy)
            )
        }
    ) { padding ->
        assignment?.let { item ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Navy
                    )
                    PriorityTag(item.priority)
                }

                Text(
                    text = item.subject,
                    style = MaterialTheme.typography.titleMedium,
                    color = BrightBlue
                )

                Surface(
                    color = White,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, RoyalBlue)
                ) {
                    InfoRow(
                        label = "Deadline",
                        value = SimpleDateFormat("EEEE, MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(Date(item.dueDateTime)),
                        valueColor = RoyalBlue,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Surface(
                    color = White,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (item.isCompleted) SoftGreen else Amber)
                ) {
                    InfoRow(
                        label = "Status",
                        value = if (item.isCompleted) "Completed" else "Pending",
                        valueColor = if (item.isCompleted) SoftGreen else Amber,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleSmall,
                    color = Navy
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = PrimaryText
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.toggleCompletion(item) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.isCompleted) SteelBlue else RoyalBlue
                    )
                ) {
                    Text(if (item.isCompleted) "Mark as Pending" else "Mark as Complete")
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Assignment not found", color = Navy)
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Assignment") },
            text = { Text("Are you sure you want to delete this assignment? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    assignment?.let { viewModel.deleteAssignment(it) }
                    showDeleteDialog = false
                    onBack()
                }) {
                    Text("Delete", color = SoftRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Navy)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = valueColor, fontWeight = FontWeight.Medium)
    }
}
