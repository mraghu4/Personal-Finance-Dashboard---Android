package com.example.pfdb.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class PieChartData(val name: String, val value: Double, val color: Color)

@Composable
fun DonutChart(data: List<PieChartData>, size: Dp = 160.dp, strokeWidth: Dp = 30.dp) {
    Canvas(modifier = Modifier.size(size)) {
        var startAngle = -90f
        val totalValue = data.sumOf { it.value }
        
        data.forEach { segment ->
            val sweepAngle = (segment.value / totalValue * 360f).toFloat()
            drawArc(
                color = segment.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx())
            )
            startAngle += sweepAngle
        }
    }
}
