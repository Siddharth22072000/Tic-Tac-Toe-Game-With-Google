package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

private data class Particle(
    val initialX: Float,
    val speedX: Float,
    val speedY: Float,
    val color: Color,
    val size: Float,
    val rotationSpeed: Float
)

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    trigger: Boolean
) {
    if (!trigger) return

    val progress = remember(trigger) { Animatable(0f) }

    val particles = remember(trigger) {
        val colors = listOf(
            Color(0xFF00E5FF),
            Color(0xFFFF2E63),
            Color(0xFFFBBF24),
            Color(0xFF10B981),
            Color(0xFFA855F7),
            Color(0xFFEC4899)
        )
        List(60) {
            Particle(
                initialX = Random.nextFloat(),
                speedX = (Random.nextFloat() - 0.5f) * 600f,
                speedY = Random.nextFloat() * 1200f + 600f,
                color = colors.random(),
                size = Random.nextFloat() * 14f + 8f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f
            )
        }
    }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
        )
    }

    if (progress.value < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height
            val t = progress.value

            particles.forEach { p ->
                val x = (p.initialX * canvasW) + (p.speedX * t)
                val y = -50f + (p.speedY * t)
                val alpha = (1f - t).coerceIn(0f, 1f)
                drawRect(
                    color = p.color.copy(alpha = alpha),
                    topLeft = Offset(x, y),
                    size = Size(p.size, p.size * 0.7f)
                )
            }
        }
    }
}
