package com.example.pfdb.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pfdb.ui.FinanceViewModel
import com.example.pfdb.ui.components.DonutChart
import com.example.pfdb.ui.components.PieChartData
import com.example.pfdb.ui.theme.*

@Composable
fun AnalyticsScreen(viewModel: FinanceViewModel) {
    var showTable by remember { mutableStateOf(false) }
    val assets by viewModel.allAssets.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBase)
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Analytics", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(BgSurface)
                    .padding(4.dp)
            ) {
                IconButton(
                    onClick = { showTable = false },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!showTable) AccentBlue else Color.Transparent)
                ) {
                    Icon(Icons.Default.PieChart, contentDescription = "Graph View", tint = if (!showTable) Color.White else TextSecondary, modifier = Modifier.size(20.dp))
                }
                IconButton(
                    onClick = { showTable = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (showTable) AccentBlue else Color.Transparent)
                ) {
                    Icon(Icons.Default.TableChart, contentDescription = "Table View", tint = if (showTable) Color.White else TextSecondary, modifier = Modifier.size(20.dp))
                }
            }
        }

        if (showTable) {
            AssetTable(assets, currency)
        } else {
            AnalyticsGraphView(assets, currency)
        }
    }
}

@Composable
fun AnalyticsGraphView(assets: List<com.example.pfdb.data.Asset>, currency: String) {
    if (assets.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No data available", color = TextSecondary)
        }
        return
    }

    val total = assets.sumOf { it.marketValue }
    val grouped = assets.groupBy { it.type }
    
    val chartData = grouped.map { (type, items) ->
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // Pie Chart Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(BgSurfaceGlass)
                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Asset Allocation", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(24.dp))
                
                Box(contentAlignment = Alignment.Center) {
                    DonutChart(chartData)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total", fontSize = 12.sp, color = TextSecondary)
                        Text("$currency ${String.format("%,.0f", total)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Legend
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    maxItemsInEachRow = 3
                ) {
                    chartData.forEach { data ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(data.color))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(data.name, fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Breakdown List
        Text("Breakdown", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(16.dp))
        
        chartData.forEach { data ->
            val percentage = (data.value / total * 100).toFloat()
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(data.name, fontSize = 14.sp, color = TextPrimary)
                    Text("$currency ${String.format("%,.0f", data.value)} (${String.format("%.1f", percentage)}%)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.05f))) {
                    Box(modifier = Modifier.fillMaxWidth(percentage/100).fillMaxHeight().clip(CircleShape).background(data.color))
                }
            }
        }
    }
}

@Composable
fun AssetTable(assets: List<com.example.pfdb.data.Asset>, currency: String) {
    if (assets.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No assets to list", color = TextSecondary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(BgSurfaceGlass)
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text("Asset", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSecondary)
                Text("Value", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSecondary)
                Text("Return", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSecondary)
            }
            HorizontalDivider(color = BorderColor)
        }

        items(assets) { asset ->
            val returns = asset.marketValue - asset.investedAmount
            val pct = if (asset.investedAmount > 0) (returns / asset.investedAmount * 100) else 0.0
            
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(2f)) {
                    Text(asset.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    Text(asset.type, fontSize = 11.sp, color = TextSecondary)
                }
                Text("$currency ${String.format("%,.0f", asset.marketValue)}", modifier = Modifier.weight(1.5f), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Column(modifier = Modifier.weight(1.5f)) {
                    val color = if (returns >= 0) Success else Danger
                    Text("${if (returns >= 0) "+" else ""}${String.format("%.1f", pct)}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
                    Text("$currency ${String.format("%,.0f", Math.abs(returns))}", fontSize = 11.sp, color = color)
                }
            }
            HorizontalDivider(color = BorderColor.copy(alpha = 0.05f))
        }
    }
}
