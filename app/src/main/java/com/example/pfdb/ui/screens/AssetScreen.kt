package com.example.pfdb.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pfdb.data.Asset
import com.example.pfdb.data.Liability
import com.example.pfdb.ui.FinanceViewModel
import com.example.pfdb.ui.theme.*

@Composable
fun AssetScreen(viewModel: FinanceViewModel) {
    val assets by viewModel.allAssets.collectAsState()
    val liabilities by viewModel.allLiabilities.collectAsState()
    val totalAssets by viewModel.totalMarketValue.collectAsState()
    val totalLiabilities by viewModel.totalLiabilities.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()
    val familyMembers by viewModel.familyMembers.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var editingAsset by remember { mutableStateOf<Asset?>(null) }
    var editingLiability by remember { mutableStateOf<Liability?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBase)
            .padding(horizontal = 24.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("My Portfolio", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add")
            }
        }

        // Portfolio Summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryItem("Total Assets", totalAssets, currency, modifier = Modifier.weight(1f))
            SummaryItem("Total Liabilities", totalLiabilities, currency, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Portfolio List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Assets Section
            val categories = listOf("Bank Accounts", "Stock Broker", "Mutual Funds", "Movable Assets", "Immovable Assets")
            categories.forEach { category ->
                val categoryAssets = assets.filter { it.type == category }
                if (categoryAssets.isNotEmpty()) {
                    item { CategoryHeader(category, categoryAssets.sumOf { it.marketValue }, currency) }
                    items(categoryAssets) { asset ->
                        AssetListItem(
                            asset = asset,
                            currency = currency,
                            onClick = { editingAsset = asset },
                            onDelete = { viewModel.deleteAsset(asset) }
                        )
                    }
                }
            }

            // Liabilities Section
            if (liabilities.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    CategoryHeader("Liabilities", liabilities.sumOf { it.amount }, currency, isLiability = true)
                }
                items(liabilities) { liability ->
                    LiabilityListItem(
                        liability = liability,
                        currency = currency,
                        onClick = { editingLiability = liability },
                        onDelete = { viewModel.deleteLiability(liability) }
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // Dialogs
    if (showAddDialog) {
        AddEntryDialog(
            familyMembers = familyMembers,
            onDismiss = { showAddDialog = false },
            onAddAsset = { viewModel.addAsset(it) },
            onAddLiability = { viewModel.addLiability(it) }
        )
    }

    editingAsset?.let { asset ->
        EditAssetDialog(
            asset = asset,
            familyMembers = familyMembers,
            onDismiss = { editingAsset = null },
            onConfirm = { viewModel.updateAsset(it) }
        )
    }

    editingLiability?.let { liability ->
        EditLiabilityDialog(
            liability = liability,
            familyMembers = familyMembers,
            onDismiss = { editingLiability = null },
            onConfirm = { viewModel.updateLiability(it) }
        )
    }
}

@Composable
fun SummaryItem(label: String, value: Double, currency: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BgSurface)
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(label, fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "$currency ${String.format("%,.2f", value)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun CategoryHeader(title: String, total: Double, currency: String, isLiability: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val (icon, color) = when {
                isLiability -> Icons.Default.CreditCard to Danger
                title == "Bank Accounts" -> Icons.Default.AccountBalance to Color(0xFF60A5FA)
                title == "Stock Broker" -> Icons.AutoMirrored.Filled.TrendingUp to Color(0xFFA78BFA)
                title == "Mutual Funds" -> Icons.Default.PieChart to Color(0xFF34D399)
                title == "Movable Assets" -> Icons.Default.Watch to Color(0xFFFBBF24)
                else -> Icons.Default.Home to Color(0xFFF87171)
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }
        Text("$currency ${String.format("%,.2f", total)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (isLiability) Danger else TextPrimary)
    }
}

@Composable
fun AssetListItem(asset: Asset, currency: String, onClick: () -> Unit, onDelete: () -> Unit) {
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(asset.name, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                Text("Asset • ${asset.institution}", fontSize = 12.sp, color = TextSecondary)
                if (asset.notes.isNotEmpty()) {
                    Text(asset.notes, fontSize = 12.sp, color = TextSecondary, style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$currency ${String.format("%,.2f", asset.marketValue)}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Success)
                
                val diff = asset.marketValue - asset.investedAmount
                if (asset.investedAmount > 0) {
                    val pct = (diff / asset.investedAmount) * 100
                    val isPos = diff >= 0
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isPos) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = if (isPos) Success else Danger,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            "${if (isPos) "+" else ""}$currency ${String.format("%,.2f", Math.abs(diff))} (${String.format("%.1f", pct)}%)",
                            fontSize = 11.sp,
                            color = if (isPos) Success else Danger
                        )
                    }
                }
                
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Danger, modifier = Modifier.size(16.dp))
                }
            }
        }
        HorizontalDivider(color = BorderColor.copy(alpha = 0.2f), thickness = 1.dp)
    }
}

@Composable
fun LiabilityListItem(liability: Liability, currency: String, onClick: () -> Unit, onDelete: () -> Unit) {
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(liability.name, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                Text("Liability", fontSize = 12.sp, color = TextSecondary)
                if (liability.notes.isNotEmpty()) {
                    Text(liability.notes, fontSize = 12.sp, color = TextSecondary, style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$currency ${String.format("%,.2f", liability.amount)}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Danger)
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Danger.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                }
            }
        }
        HorizontalDivider(color = BorderColor.copy(alpha = 0.2f), thickness = 1.dp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryDialog(
    familyMembers: List<com.example.pfdb.data.FamilyMember>,
    onDismiss: () -> Unit,
    onAddAsset: (Asset) -> Unit,
    onAddLiability: (Liability) -> Unit
) {
    var entryType by remember { mutableStateOf("Asset") } // "Asset" or "Liability"
    
    // Shared fields
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") } // marketValue for asset, amount for liability
    var selectedMemberId by remember { mutableStateOf(familyMembers.firstOrNull()?.id ?: 0) }
    var notes by remember { mutableStateOf("") }
    
    // Asset specific
    var institution by remember { mutableStateOf("") }
    var invested by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Bank Accounts") }

    val categories = listOf("Bank Accounts", "Stock Broker", "Mutual Funds", "Movable Assets", "Immovable Assets")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Entry", color = TextPrimary) },
        containerColor = BgSurface,
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // Entry Type Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BgBase)
                        .padding(4.dp)
                ) {
                    listOf("Asset", "Liability").forEach { type ->
                        val isSelected = entryType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AccentBlue else Color.Transparent)
                                .clickable { entryType = type },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(type, color = if (isSelected) Color.White else TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Owner
                Text("Owner (Family Member)", fontSize = 12.sp, color = TextSecondary)
                MemberDropdown(familyMembers, selectedMemberId) { selectedMemberId = it }

                if (entryType == "Asset") {
                    // Category
                    Text("Category", fontSize = 12.sp, color = TextSecondary)
                    CategoryDropdown(categories, category) { category = it }

                    OutlinedTextField(
                        value = institution,
                        onValueChange = { institution = it },
                        label = { Text("Institution / AMC") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue, unfocusedBorderColor = BorderColor, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name / Description") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue, unfocusedBorderColor = BorderColor, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (entryType == "Asset") {
                        OutlinedTextField(
                            value = invested,
                            onValueChange = { invested = it },
                            label = { Text("Invested") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue, unfocusedBorderColor = BorderColor, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                    }
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text(if (entryType == "Asset") "Market Value" else "Amount") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue, unfocusedBorderColor = BorderColor, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue, unfocusedBorderColor = BorderColor, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (entryType == "Asset") {
                        onAddAsset(Asset(name = name, familyMemberId = selectedMemberId, type = category, investedAmount = invested.toDoubleOrNull() ?: 0.0, marketValue = amount.toDoubleOrNull() ?: 0.0, institution = institution, notes = notes))
                    } else {
                        onAddLiability(Liability(name = name, familyMemberId = selectedMemberId, amount = amount.toDoubleOrNull() ?: 0.0, notes = notes))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) { Text("Save Entry") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAssetDialog(
    asset: Asset,
    familyMembers: List<com.example.pfdb.data.FamilyMember>,
    onDismiss: () -> Unit,
    onConfirm: (Asset) -> Unit
) {
    var name by remember { mutableStateOf(asset.name) }
    var institution by remember { mutableStateOf(asset.institution) }
    var invested by remember { mutableStateOf(asset.investedAmount.toString()) }
    var market by remember { mutableStateOf(asset.marketValue.toString()) }
    var category by remember { mutableStateOf(asset.type) }
    var selectedMemberId by remember { mutableStateOf(asset.familyMemberId) }
    var notes by remember { mutableStateOf(asset.notes) }

    val categories = listOf("Bank Accounts", "Stock Broker", "Mutual Funds", "Movable Assets", "Immovable Assets")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Asset", color = TextPrimary) },
        containerColor = BgSurface,
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text("Owner", fontSize = 12.sp, color = TextSecondary)
                MemberDropdown(familyMembers, selectedMemberId) { selectedMemberId = it }
                
                Text("Category", fontSize = 12.sp, color = TextSecondary)
                CategoryDropdown(categories, category) { category = it }

                OutlinedTextField(value = institution, onValueChange = { institution = it }, label = { Text("Institution") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue, unfocusedBorderColor = BorderColor, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue, unfocusedBorderColor = BorderColor, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = invested, onValueChange = { invested = it }, label = { Text("Invested") }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue, unfocusedBorderColor = BorderColor, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                    OutlinedTextField(value = market, onValueChange = { market = it }, label = { Text("Market Value") }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue, unfocusedBorderColor = BorderColor, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                }
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue, unfocusedBorderColor = BorderColor, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(asset.copy(name = name, institution = institution, investedAmount = invested.toDoubleOrNull() ?: 0.0, marketValue = market.toDoubleOrNull() ?: 0.0, type = category, familyMemberId = selectedMemberId, notes = notes)) }, colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)) { Text("Update") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@Composable
fun EditLiabilityDialog(
    liability: Liability,
    familyMembers: List<com.example.pfdb.data.FamilyMember>,
    onDismiss: () -> Unit,
    onConfirm: (Liability) -> Unit
) {
    var name by remember { mutableStateOf(liability.name) }
    var amount by remember { mutableStateOf(liability.amount.toString()) }
    var selectedMemberId by remember { mutableStateOf(liability.familyMemberId) }
    var notes by remember { mutableStateOf(liability.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Liability", color = TextPrimary) },
        containerColor = BgSurface,
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text("Owner", fontSize = 12.sp, color = TextSecondary)
                MemberDropdown(familyMembers, selectedMemberId) { selectedMemberId = it }
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue, unfocusedBorderColor = BorderColor, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue, unfocusedBorderColor = BorderColor, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue, unfocusedBorderColor = BorderColor, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(liability.copy(name = name, amount = amount.toDoubleOrNull() ?: 0.0, familyMemberId = selectedMemberId, notes = notes)) }, colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)) { Text("Update") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@Composable
fun MemberDropdown(familyMembers: List<com.example.pfdb.data.FamilyMember>, selectedId: Int, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Surface(modifier = Modifier.fillMaxWidth().clickable { expanded = true }, color = BgBase, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, BorderColor)) {
            Text(text = familyMembers.find { it.id == selectedId }?.name ?: "Select Member", modifier = Modifier.padding(12.dp), color = TextPrimary)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(BgSurface)) {
            familyMembers.forEach { member ->
                DropdownMenuItem(text = { Text(member.name, color = TextPrimary) }, onClick = { onSelect(member.id); expanded = false })
            }
        }
    }
}

@Composable
fun CategoryDropdown(categories: List<String>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Surface(modifier = Modifier.fillMaxWidth().clickable { expanded = true }, color = BgBase, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, BorderColor)) {
            Text(text = selected, modifier = Modifier.padding(12.dp), color = TextPrimary)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(BgSurface)) {
            categories.forEach { cat ->
                DropdownMenuItem(text = { Text(cat, color = TextPrimary) }, onClick = { onSelect(cat); expanded = false })
            }
        }
    }
}
