package com.campuslife.solutions.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.campuslife.solutions.data.local.Assignment
import com.campuslife.solutions.ui.theme.*
import com.campuslife.solutions.viewmodel.AssignmentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onAssignmentClick: (Int) -> Unit,
    viewModel: AssignmentViewModel = hiltViewModel()
) {
    val assignments by viewModel.assignments.collectAsState()
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

    val assignmentsForSelectedDate = assignments.filter {
        val cal = Calendar.getInstance().apply { timeInMillis = it.dueDateTime }
        cal.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar", color = White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Navy)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppBackground)
        ) {
            CalendarView(
                selectedDate = selectedDate,
                assignments = assignments,
                onDateSelected = { selectedDate = it }
            )

            Text(
                text = "Assignments for ${SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(selectedDate.time)}",
                style = MaterialTheme.typography.titleMedium,
                color = Navy,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(assignmentsForSelectedDate) { assignment ->
                    AssignmentCard(assignment = assignment, onClick = { onAssignmentClick(assignment.id) })
                }

                if (assignmentsForSelectedDate.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No assignments due this day", color = SecondaryText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarView(
    selectedDate: Calendar,
    assignments: List<Assignment>,
    onDateSelected: (Calendar) -> Unit
) {
    val calendar = remember(selectedDate) {
        (selectedDate.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }
    
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    
    Card(
        modifier = Modifier.padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time),
                    style = MaterialTheme.typography.titleLarge,
                    color = Navy
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
            Row(modifier = Modifier.fillMaxWidth()) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = SecondaryText
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            for (i in 0 until 6) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (j in 0 until 7) {
                        val dayIndex = i * 7 + j
                        val day = dayIndex - firstDayOfWeek + 1
                        
                        if (day in 1..daysInMonth) {
                            val dayCal = (calendar.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, day) }
                            val isSelected = dayCal.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR) &&
                                    dayCal.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR)
                            
                            val hasAssignments = assignments.any {
                                val cal = Calendar.getInstance().apply { timeInMillis = it.dueDateTime }
                                cal.get(Calendar.YEAR) == dayCal.get(Calendar.YEAR) &&
                                        cal.get(Calendar.DAY_OF_YEAR) == dayCal.get(Calendar.DAY_OF_YEAR)
                            }
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Periwinkle else Color.Transparent)
                                    .clickable { onDateSelected(dayCal) },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = day.toString(),
                                        color = if (isSelected) Navy else PrimaryText,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (hasAssignments) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(BrightBlue)
                                        )
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
