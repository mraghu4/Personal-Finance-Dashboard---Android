package com.example.pfdb.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pfdb.ui.FinanceViewModel
import com.example.pfdb.ui.theme.*
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: FinanceViewModel) {
    val currency by viewModel.selectedCurrency.collectAsState()
    val familyMembers by viewModel.familyMembers.collectAsState()
    var newMemberName by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    var showResetDialog by remember { mutableStateOf(false) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                try {
                    context.contentResolver.openInputStream(it)?.use { inputStream ->
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val json = reader.readText()
                        viewModel.importData(json)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBase)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        SectionHeader("Settings & Family")
        
        Spacer(modifier = Modifier.height(24.dp))

        // Currency Card
        SettingsPremiumCard(
            title = "Display Currency",
            icon = Icons.Default.CurrencyExchange,
            description = "Select the currency symbol used across the dashboard."
        ) {
            val currencies = listOf("$", "€", "£", "₹", "¥")
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 12.dp)
            ) {
                currencies.forEach { symbol ->
                    FilterChip(
                        selected = currency == symbol,
                        onClick = { viewModel.setCurrency(symbol) },
                        label = { Text(symbol) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentBlue,
                            selectedLabelColor = Color.White,
                            containerColor = BgSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = currency == symbol,
                            borderColor = GlassBorder,
                            selectedBorderColor = AccentBlue
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Family Members Card
        SettingsPremiumCard(
            title = "Family Members",
            icon = Icons.Default.Groups,
            description = "Manage family members to track individual net worths."
        ) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                familyMembers.forEach { member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(member.name, color = TextPrimary, fontSize = 14.sp)
                        }
                        IconButton(onClick = { viewModel.deleteFamilyMember(member) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Danger, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                        OutlinedTextField(
                            value = newMemberName,
                            onValueChange = { newMemberName = it },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = BgSurface,
                                focusedContainerColor = BgSurface,
                                unfocusedBorderColor = BorderColor,
                                focusedBorderColor = AccentBlue,
                                unfocusedTextColor = TextPrimary,
                                focusedTextColor = TextPrimary,
                                unfocusedPlaceholderColor = TextSecondary,
                                focusedPlaceholderColor = TextSecondary
                            ),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newMemberName.isNotBlank()) {
                                viewModel.addFamilyMember(newMemberName)
                                newMemberName = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentBlue)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Backup Card
        SettingsPremiumCard(
            title = "Backup & Restore",
            icon = Icons.Default.Save,
            description = "Export your data to a secure file. You can restore from there anytime!"
        ) {
            Row(modifier = Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { 
                        try {
                            val json = viewModel.exportData()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, json)
                                type = "application/json"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Backup Financial Data")
                            context.startActivity(shareIntent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Backup", fontSize = 14.sp)
                }
                OutlinedButton(
                    onClick = { 
                        importLauncher.launch(arrayOf("application/json", "text/plain", "*/*")) 
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Restore", fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Danger Zone
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BgSurface)
                .border(1.dp, Danger.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Danger, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Danger Zone", color = Danger, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Button(
                    onClick = { showResetDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Danger.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Danger)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset All Data", color = Danger)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset All Data?") },
            text = { Text("This will permanently delete all family members, assets, and liabilities. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Danger)
                ) {
                    Text("Reset Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = BgSurface,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }
}

@Composable
fun SettingsPremiumCard(
    title: String,
    icon: ImageVector,
    description: String,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(BgSurfaceGlass)
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Text(description, color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(vertical = 8.dp))
            content()
        }
    }
}
