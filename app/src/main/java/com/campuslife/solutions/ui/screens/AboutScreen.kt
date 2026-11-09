package com.campuslife.solutions.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campuslife.solutions.R
import com.campuslife.solutions.ui.theme.*

data class Developer(val name: String, val role: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen() {
    val developers = listOf(
        Developer(stringResource(R.string.dev_pm), "Project Manager"),
        Developer(stringResource(R.string.dev_lead), "Lead Developer"),
        Developer(stringResource(R.string.dev_ui), "UI/UX Designer"),
        Developer(stringResource(R.string.dev_db), "Database Developer"),
        Developer(stringResource(R.string.dev_qa), "Quality Assurance"),
        Developer(stringResource(R.string.dev_doc), "Documentation Specialist")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About", color = White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Navy)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppBackground)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.cls_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Campus Life Solutions",
                style = MaterialTheme.typography.headlineMedium,
                color = Navy
            )
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.labelMedium,
                color = Navy
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "An all-in-one assignment and deadline reminder app designed to help students stay organized and ahead of their schedules.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = PrimaryText
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Meet the Developers",
                style = MaterialTheme.typography.titleLarge,
                color = Navy,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            developers.chunked(2).forEach { pair ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    pair.forEach { dev ->
                        DeveloperCard(developer = dev, modifier = Modifier.weight(1f))
                    }
                    if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DeveloperCard(developer: Developer, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Periwinkle),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = developer.name.firstOrNull()?.toString() ?: "",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = developer.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Navy,
                textAlign = TextAlign.Center
            )
            Text(
                text = developer.role,
                style = MaterialTheme.typography.labelSmall,
                color = Navy,
                textAlign = TextAlign.Center
            )
        }
    }
}
