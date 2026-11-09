package com.campuslife.solutions.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.campuslife.solutions.R
import com.campuslife.solutions.data.local.Assignment
import com.campuslife.solutions.data.local.Priority
import com.campuslife.solutions.ui.theme.*
import com.campuslife.solutions.viewmodel.AssignmentFilter
import com.campuslife.solutions.viewmodel.AssignmentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddAssignment: () -> Unit,
    onAssignmentClick: (Int) -> Unit,
    viewModel: AssignmentViewModel = hiltViewModel()
) {
    val assignments by viewModel.assignments.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val currentFilter by viewModel.filter.collectAsState()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Navy)) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.cls_logo),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Campus Life Solutions",
                                color = White,
                                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Serif)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Navy,
                        titleContentColor = White
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(White)
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search assignments...", color = SecondaryText) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SecondaryText) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAssignment,
                containerColor = RoyalBlue,
                contentColor = White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Assignment")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppBackground),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Good day, Student",
                    style = MaterialTheme.typography.titleLarge,
                    color = Navy
                )
            }

            item {
                SummaryCards(assignments)
            }

            item {
                FilterChips(currentFilter) { viewModel.setFilter(it) }
            }

            items(assignments) { assignment ->
                AssignmentCard(assignment = assignment, onClick = { onAssignmentClick(assignment.id) })
            }
            
            if (assignments.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No assignments found", color = SecondaryText)
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCards(assignments: List<Assignment>) {
    val pending = assignments.count { !it.isCompleted }
    val dueToday = assignments.count { !it.isCompleted && isToday(it.dueDateTime) }
    val overdue = assignments.count { !it.isCompleted && it.dueDateTime < System.currentTimeMillis() }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryCard(title = "Pending", count = pending, modifier = Modifier.weight(1f), color = RoyalBlue)
        SummaryCard(title = "Due Today", count = dueToday, modifier = Modifier.weight(1f), color = Amber)
        SummaryCard(title = "Overdue", count = overdue, modifier = Modifier.weight(1f), color = SoftRed)
    }
}

@Composable
fun SummaryCard(title: String, count: Int, modifier: Modifier, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = SecondaryText)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(selectedFilter: AssignmentFilter, onFilterSelected: (AssignmentFilter) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(AssignmentFilter.entries) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter.name.lowercase().replaceFirstChar { it.uppercase() }) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RoyalBlue,
                    selectedLabelColor = White,
                    containerColor = Periwinkle.copy(alpha = 0.3f),
                    labelColor = Navy
                ),
                border = null
            )
        }
    }
}

@Composable
fun AssignmentCard(assignment: Assignment, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
    val isOverdue = !assignment.isCompleted && assignment.dueDateTime < System.currentTimeMillis()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (assignment.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                    ),
                    color = if (isOverdue) SoftRed else Navy
                )
                PriorityTag(assignment.priority)
            }
            Text(text = assignment.subject, style = MaterialTheme.typography.bodyMedium, color = SecondaryText)
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = White,
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isOverdue) SoftRed else RoyalBlue)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_my_calendar),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (isOverdue) SoftRed else RoyalBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = dateFormat.format(Date(assignment.dueDateTime)),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isOverdue) SoftRed else RoyalBlue
                    )
                }
            }
            if (assignment.isCompleted) {
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftGreen,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun PriorityTag(priority: Priority) {
    val color = when (priority) {
        Priority.LOW -> SteelBlue
        Priority.MEDIUM -> Amber
        Priority.HIGH -> SoftRed
    }
    Surface(
        color = White,
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = priority.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

private fun isToday(timestamp: Long): Boolean {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply { timeInMillis = timestamp }
    return now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
}
