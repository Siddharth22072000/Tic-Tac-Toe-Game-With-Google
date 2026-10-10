package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.game.BoardDimension
import com.example.game.CellMark
import com.example.game.GameResult
import com.example.ui.theme.LocalAppTheme

@Composable
fun BoardView(
    board: List<CellMark>,
    dimension: BoardDimension,
    gameResult: GameResult,
    hintIndex: Int?,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current
    val size = dimension.size

    val winningIndices = remember(gameResult) {
        if (gameResult is GameResult.Win) gameResult.winningIndices else emptyList()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val maxBoardWidth = minOf(maxWidth, 400.dp)

        CardContainer(
            modifier = Modifier
                .widthIn(max = maxBoardWidth)
                .aspectRatio(1f)
                .testTag("game_board")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (row in 0 until size) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (col in 0 until size) {
                            val index = row * size + col
                            val mark = board.getOrElse(index) { CellMark.EMPTY }
                            val isWinningCell = winningIndices.contains(index)
                            val isHintCell = hintIndex == index

                            CellView(
                                index = index,
                                mark = mark,
                                isWinningCell = isWinningCell,
                                isHintCell = isHintCell,
                                winnerMark = (gameResult as? GameResult.Win)?.winner,
                                onClick = { onCellClick(index) },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val theme = LocalAppTheme.current
    Box(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black)
            .clip(RoundedCornerShape(24.dp))
            .background(theme.surface)
            .border(2.dp, theme.gridColor.copy(alpha = 0.6f), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun CellView(
    index: Int,
    mark: CellMark,
    isWinningCell: Boolean,
    isHintCell: Boolean,
    winnerMark: CellMark?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current

    val infiniteTransition = rememberInfiniteTransition(label = "cellAnimation")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val hintPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hintPulse"
    )

    val winnerColor = when (winnerMark) {
        CellMark.X -> theme.xColor
        CellMark.O -> theme.oColor
        else -> theme.primary
    }

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isWinningCell -> winnerColor.copy(alpha = 0.28f)
            isHintCell -> theme.primary.copy(alpha = 0.15f)
            else -> theme.surfaceVariant.copy(alpha = 0.6f)
        },
        label = "cellBg"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isWinningCell -> winnerColor
            isHintCell -> theme.primary.copy(alpha = hintPulseAlpha)
            else -> Color.Transparent
        },
        label = "cellBorder"
    )

    val cellScale = if (isWinningCell) pulseScale else 1f

    Box(
        modifier = modifier
            .scale(cellScale)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = if (isWinningCell || isHintCell) 2.5.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = theme.primary),
                role = Role.Button,
                onClick = onClick
            )
            .testTag("cell_$index"),
        contentAlignment = Alignment.Center
    ) {
        if (mark != CellMark.EMPTY) {
            MarkVisual(
                mark = mark,
                isWinning = isWinningCell,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            )
        }
    }
}

@Composable
private fun MarkVisual(
    mark: CellMark,
    isWinning: Boolean,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current
    val scaleAnim = remember { Animatable(0f) }

    LaunchedEffect(mark) {
        scaleAnim.snapTo(0f)
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val strokeColor = if (mark == CellMark.X) theme.xColor else theme.oColor

    Canvas(
        modifier = modifier
            .scale(scaleAnim.value)
    ) {
        val w = size.width
        val h = size.height
        val strokeWidth = (w * 0.14f).coerceIn(4f, 16f)

        if (mark == CellMark.X) {
            // Draw X
            val pad = w * 0.15f
            drawLine(
                color = strokeColor,
                start = Offset(pad, pad),
                end = Offset(w - pad, h - pad),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = strokeColor,
                start = Offset(w - pad, pad),
                end = Offset(pad, h - pad),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        } else if (mark == CellMark.O) {
            // Draw O
            val radius = (minOf(w, h) - strokeWidth) * 0.38f
            drawCircle(
                color = strokeColor,
                center = Offset(w / 2f, h / 2f),
                radius = radius,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}
