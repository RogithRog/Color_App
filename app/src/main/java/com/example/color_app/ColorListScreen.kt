package com.example.color_app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.color_app.roomdb.ColorItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ColorListScreen(viewModel: ColorViewModel = viewModel()) {
    val colors by viewModel.colors.collectAsState()
    val unsyncedCount by viewModel.unsyncedCount.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val syncError by viewModel.syncError.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                title = "Color App",
                unsyncedCount = unsyncedCount,
                onSyncClick = { viewModel.syncColors() },
                isSyncing = isSyncing
            )
        },
        floatingActionButton = {
            AddColorFAB(onClick = { viewModel.addNewColor() })
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
         Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (syncError != null) {
                    Text(
                        text = syncError!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(colors) { colorItem ->
                        ColorCard(colorItem)
                    }
                }
            }
        }
    }
}

@Composable
fun AddColorFAB(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE6E0FF), // Light purple background color
            contentColor = Color(0xFF4C408B)   // Dark purple text/icon color
        ),
        modifier = Modifier.height(40.dp), // Adjust height to match the image
        contentPadding = PaddingValues(horizontal = 16.dp) // Adjust padding for text and icon
    ) {
        Text(
            text = "Add Color",
            color = Color(0xFF4C408B) // Dark purple color for the text
        )
        Spacer(modifier = Modifier.width(8.dp)) // Space between text and icon
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Color",
            tint = Color(0xFF4C408B) // Dark purple icon color
        )
    }
}

@Composable
fun ColorCard(colorItem: ColorItem) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(android.graphics.Color.parseColor(colorItem.color))
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = colorItem.color,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Created at ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(colorItem.createdAt))}",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, unsyncedCount: Int, onSyncClick: () -> Unit, isSyncing: Boolean) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        actions = {
            SyncButton(
                unsyncedCount = unsyncedCount,
                onSyncClick = onSyncClick,
                isSyncing = isSyncing
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun SyncButton(unsyncedCount: Int, onSyncClick: () -> Unit, isSyncing: Boolean) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier
            .padding(end = 8.dp)
            .clickable(onClick = onSyncClick, enabled = !isSyncing)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        tonalElevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = unsyncedCount.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondary
            )
            if (isSyncing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Sync,
                    contentDescription = "Sync",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}