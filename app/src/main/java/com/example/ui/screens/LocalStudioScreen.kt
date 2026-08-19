package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalStudioScreen(onGenerateClicked: (String, String, Boolean) -> Unit) {
    var modelName by remember { mutableStateOf("Valerie") }
    var vibeSetting by remember { mutableStateOf("Moody cyberpunk penthouse") }
    var isUnconstrainedActive by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aura Studio: Local Engine") },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = "Model Parameter Matrix", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = modelName,
                onValueChange = { modelName = it },
                label = { Text("Avatar Name") },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = vibeSetting,
                onValueChange = { vibeSetting = it },
                label = { Text("Atmospheric Vibe & Lighting") },
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Unconstrained / Fine-Art Mode",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Switch(
                    checked = isUnconstrainedActive,
                    onCheckedChange = { isUnconstrainedActive = it },
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onGenerateClicked(modelName, vibeSetting, isUnconstrainedActive) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Compile Local Asset via Swarm")
            }
        }
    }
}
