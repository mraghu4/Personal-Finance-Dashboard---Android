package com.example.pfdb.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pfdb.ui.FinanceViewModel
import com.example.pfdb.ui.theme.*

@Composable
fun TimelineScreen(viewModel: FinanceViewModel) {
    val familyMembers by viewModel.familyMembers.collectAsState()
    val selectedId by viewModel.selectedFamilyMemberId.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()
    val allAssets by viewModel.allAssets.collectAsState()
    val allLiabilities by viewModel.allLiabilities.collectAsState()

    // Calculate current Net Worth for history view
    val currentNW = if (selectedId == null) {
        allAssets.sumOf { it.marketValue } - allLiabilities.sumOf { it.amount }
    } else {
        allAssets.filter { it.familyMemberId == selectedId }.sumOf { it.marketValue } -
        allLiabilities.filter { it.familyMemberId == selectedId }.sumOf { it.amount }
    }

    // Mock data for trends
    val historyData = listOf(
        TrendPoint("Jan", currentNW * 0.8),
        TrendPoint("Feb", currentNW * 0.85),
        TrendPoint("Mar", currentNW * 0.95),
        TrendPoint("Apr", currentNW * 0.92),
        TrendPoint("May", currentNW * 0.98),
        TrendPoint("Jun", currentNW) // Current
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBase)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Wealth Timeline", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        // Family Selector (Same as Dashboard)
        FamilyScrollRow(familyMembers, selectedId, viewModel, currency)
        
        Spacer(modifier = Modifier.height(24.dp))

        // Net Worth Trend Graph
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(BgSurfaceGlass)
                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column {
                val title = if (selectedId == null) "Household History" else {
                    val name = familyMembers.find { it.id == selectedId }?.name ?: "Member"
                    "$name's History"
                }
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Growth over the last 6 months", fontSize = 13.sp, color = TextSecondary)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                TrendChart(historyData)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Labels for X axis
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    historyData.forEach { point ->
                        Text(point.label, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Milestones
        Text("Recent Milestones", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(16.dp))

        MilestoneItem("Net Worth Peak", "You reached a new all-time high of $currency ${String.format("%,.0f", currentNW)}", "Jun 2024")
        MilestoneItem("Investment Milestone", "Portfolio grew by 8.5% this quarter", "May 2024")
        MilestoneItem("Debt Reduction", "Liability decreased by 5% in April", "Apr 2024")

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TrendChart(data: List<TrendPoint>) {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
    ) {
        val maxVal = data.maxOf { it.value }.toFloat() * 1.1f
        val minVal = data.minOf { it.value }.toFloat() * 0.9f
        val range = maxVal - minVal
        
        val width = size.width
        val height = size.height
        val stepX = width / (data.size - 1)
        
        val points = data.mapIndexed { index, point ->
            val x = index * stepX
            val y = height - ((point.value.toFloat() - minVal) / range * height)
            androidx.compose.ui.geometry.Offset(x, y)
        }
        
        // Draw Fill
        val fillPath = Path().apply {
            moveTo(points.first().x, height)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(points.last().x, height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(AccentBlue.copy(alpha = 0.3f), Color.Transparent)
            )
        )
        
        // Draw Line
        val strokePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawPath(
            path = strokePath,
            color = AccentBlue,
            style = Stroke(width = 3.dp.toPx())
        )
        
        // Draw points
        points.forEach { point ->
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = point
            )
            drawCircle(
                color = AccentBlue,
                radius = 2.dp.toPx(),
                center = point
            )
        }
    }
}

@Composable
fun MilestoneItem(title: String, description: String, date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BgSurface)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AccentBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(description, fontSize = 13.sp, color = TextSecondary)
            }
            Text(date, fontSize = 12.sp, color = TextSecondary)
        }
    }
}

data class TrendPoint(val label: String, val value: Double)
