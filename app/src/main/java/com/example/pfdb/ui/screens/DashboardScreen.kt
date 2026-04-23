package com.example.pfdb.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pfdb.ui.FinanceViewModel
import com.example.pfdb.ui.components.DonutChart
import com.example.pfdb.ui.components.PieChartData
import com.example.pfdb.ui.theme.*

@Composable
fun DashboardScreen(viewModel: FinanceViewModel) {
    val familyMembers by viewModel.familyMembers.collectAsState()
    val selectedId by viewModel.selectedFamilyMemberId.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()
    
    val netWorth by viewModel.netWorth.collectAsState()
    val absReturn by viewModel.absoluteReturn.collectAsState()

    val greetingName = familyMembers.firstOrNull()?.name ?: "User"
    val showListState = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBase)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        // App Header
        AppHeader(greetingName)

        Spacer(modifier = Modifier.height(24.dp))

        // Net Worth Card (Glassmorphism)
        PremiumNetWorthCard(netWorth, absReturn, currency)

        Spacer(modifier = Modifier.height(32.dp))

        // Family Overview Header
        SectionHeader("Family Overview")

        Spacer(modifier = Modifier.height(16.dp))

        // Family Scroll Container
        FamilyScrollRow(familyMembers, selectedId, viewModel, currency)

        Spacer(modifier = Modifier.height(24.dp))

        // Analytics Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Asset Allocation", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            AnalyticsToggle(showList = showListState.value, onToggle = { showListState.value = it })
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AnalyticsSection(viewModel, currency, showListState.value)
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun AnalyticsToggle(showList: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BgSurface)
            .padding(4.dp)
    ) {
        IconButton(
            onClick = { onToggle(false) },
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (!showList) AccentBlue else Color.Transparent)
        ) {
            Icon(Icons.Default.PieChart, contentDescription = null, tint = if (!showList) Color.White else TextSecondary, modifier = Modifier.size(20.dp))
        }
        IconButton(
            onClick = { onToggle(true) },
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (showList) AccentBlue else Color.Transparent)
        ) {
            Icon(Icons.AutoMirrored.Filled.FormatListBulleted, contentDescription = null, tint = if (showList) Color.White else TextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun AppHeader(userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = com.example.pfdb.R.drawable.ic_app_logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("Good Morning,", fontSize = 13.sp, color = TextSecondary)
            Text(userName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
fun PremiumNetWorthCard(netWorth: Double, monthlyChange: Double, currency: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(BgSurfaceGlass)
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "TOTAL NET WORTH",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "$currency ${String.format("%,.2f", netWorth)}",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    brush = Brush.horizontalGradient(listOf(Color.White, Color(0xFFCBD5E1)))
                )
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            HorizontalDivider(color = BorderColor, thickness = 1.dp)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatColumn("Monthly Change", monthlyChange, currency)
                StatColumn("Yearly Change", monthlyChange * 12, currency) // Mocked
            }
        }
    }
}

@Composable
fun StatColumn(label: String, value: Double, currency: String) {
    Column {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            val isPos = value >= 0
            Icon(
                imageVector = if (isPos) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                contentDescription = null,
                tint = if (isPos) Success else Danger,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "${if (isPos) "+" else ""}$currency ${String.format("%,.2f", Math.abs(value))}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isPos) Success else Danger
            )
        }
    }
}

@Composable
fun FamilyScrollRow(
    familyMembers: List<com.example.pfdb.data.FamilyMember>,
    selectedId: Int?,
    viewModel: FinanceViewModel,
    currency: String
) {
    val allAssets by viewModel.allAssets.collectAsState()
    val allLiabilities by viewModel.allLiabilities.collectAsState()

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // "All Family" Card
        val totalNW = allAssets.sumOf { it.marketValue } - allLiabilities.sumOf { it.amount }
        FamilyCard(
            name = "All Family",
            isSelected = selectedId == null,
            onClick = { viewModel.selectFamilyMember(null) },
            icon = Icons.Default.Person,
            amount = totalNW,
            currency = currency
        )
        
        familyMembers.forEach { member ->
            val memberNW = allAssets.filter { it.familyMemberId == member.id }.sumOf { it.marketValue } -
                           allLiabilities.filter { it.familyMemberId == member.id }.sumOf { it.amount }
            FamilyCard(
                name = member.name,
                isSelected = selectedId == member.id,
                onClick = { viewModel.selectFamilyMember(member.id) },
                icon = Icons.Default.Person,
                amount = memberNW,
                currency = currency
            )
        }
    }
}

@Composable
fun FamilyCard(name: String, isSelected: Boolean, onClick: () -> Unit, icon: ImageVector, amount: Double, currency: String) {
    Box(
        modifier = Modifier
            .width(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) AccentBlue.copy(alpha = 0.15f) else BgSurface)
            .border(
                width = 1.dp,
                color = if (isSelected) AccentBlue else GlassBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(name, fontSize = 13.sp, color = TextSecondary, maxLines = 1)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("$currency ${String.format("%,.2f", amount)}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
fun AnalyticsSection(viewModel: FinanceViewModel, currency: String, showList: Boolean) {
    val assets by viewModel.filteredAssets.collectAsState()
    val liabilities by viewModel.filteredLiabilities.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(BgSurfaceGlass)
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column {
            if (assets.isEmpty() && liabilities.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("No data to display", color = TextSecondary)
                }
            } else {
                val totalAssets = assets.sumOf { it.marketValue }
                val totalLiabilities = liabilities.sumOf { it.amount }
                val totalNetWorth = totalAssets - totalLiabilities
                
                val groupedAssets = assets.groupBy { it.type }
                val assetChartData = groupedAssets.map { (type, items) ->
                    val catTotal = items.sumOf { it.marketValue }
                    val color = when (type) {
                        "Bank Accounts" -> Color(0xFF60A5FA)
                        "Stock Broker" -> Color(0xFFA78BFA)
                        "Mutual Funds" -> Color(0xFF34D399)
                        "Movable Assets" -> Color(0xFFFBBF24)
                        else -> Color(0xFFF87171)
                    }
                    PieChartData(type, catTotal, color)
                }.sortedByDescending { it.value }

                if (!showList) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                            DonutChart(assetChartData)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Net Worth", fontSize = 12.sp, color = TextSecondary)
                                Text("$currency ${String.format("%,.0f", totalNetWorth)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        // Mini Legend
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            maxItemsInEachRow = 3
                        ) {
                            assetChartData.forEach { data ->
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 6.dp)) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(data.color))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(data.name, fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                            if (totalLiabilities > 0) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 6.dp)) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Danger))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Liabilities", fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                } else {
                    // Assets List
                    if (assets.isNotEmpty()) {
                        Text("ASSETS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentBlue, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        assetChartData.forEach { data ->
                            val progress = (data.value / totalAssets).toFloat()
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(data.name, fontSize = 13.sp, color = TextSecondary)
                                    Text("$currency ${String.format("%,.0f", data.value)} (${String.format("%.1f", progress * 100)}%)", fontSize = 13.sp, color = TextPrimary)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.05f))) {
                                    Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().clip(CircleShape).background(data.color))
                                }
                            }
                        }
                    }
                    
                    // Liabilities List
                    if (liabilities.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("LIABILITIES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Danger, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        liabilities.forEach { liability ->
                            val progress = (liability.amount / totalLiabilities).toFloat()
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(liability.name, fontSize = 13.sp, color = TextSecondary)
                                    Text("$currency ${String.format("%,.0f", liability.amount)}", fontSize = 13.sp, color = TextPrimary)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.05f))) {
                                    Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().clip(CircleShape).background(Danger))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
