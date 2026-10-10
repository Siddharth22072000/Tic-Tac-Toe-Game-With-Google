package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.CellMark
import com.example.game.SessionScore
import com.example.ui.theme.LocalAppTheme

@Composable
fun ScoreBoard(
    currentPlayer: CellMark,
    sessionScore: SessionScore,
    playerXName: String,
    playerOName: String,
    isAiThinking: Boolean,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Player X Card
        PlayerScoreCard(
            modifier = Modifier
                .weight(1f)
                .testTag("player_x_card"),
            name = playerXName,
            mark = CellMark.X,
            score = sessionScore.xWins,
            isActive = currentPlayer == CellMark.X,
            accentColor = theme.xColor,
            statusText = if (currentPlayer == CellMark.X) "Turn" else null
        )

        // Draws Card
        DrawScoreCard(
            modifier = Modifier.testTag("draws_card"),
            draws = sessionScore.draws
        )

        // Player O Card
        PlayerScoreCard(
            modifier = Modifier
                .weight(1f)
                .testTag("player_o_card"),
            name = playerOName,
            mark = CellMark.O,
            score = sessionScore.oWins,
            isActive = currentPlayer == CellMark.O,
            accentColor = theme.oColor,
            statusText = if (currentPlayer == CellMark.O) {
                if (isAiThinking) "Thinking..." else "Turn"
            } else null
        )
    }
}

@Composable
private fun PlayerScoreCard(
    name: String,
    mark: CellMark,
    score: Int,
    isActive: Boolean,
    accentColor: Color,
    statusText: String?,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isActive) accentColor else theme.gridColor.copy(alpha = 0.5f),
        label = "borderColor"
    )

    val backgroundBrush = if (isActive) {
        Brush.verticalGradient(
            colors = listOf(
                theme.surfaceVariant,
                accentColor.copy(alpha = 0.12f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(theme.surface, theme.surface)
        )
    }

    Card(
        modifier = modifier
            .shadow(
                elevation = if (isActive) 6.dp else 1.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = if (isActive) accentColor else Color.Black
            )
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(backgroundBrush)
                .padding(vertical = 10.dp, horizontal = 10.dp)
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header with Symbol & Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (mark == CellMark.X) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "X",
                                tint = accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Circle,
                                contentDescription = "O",
                                tint = accentColor,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        color = theme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Score value
                Text(
                    text = score.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )

                // Status or Turn Pill
                if (statusText != null) {
                    Box(
                        modifier = Modifier
                            .scale(if (isActive) pulseScale else 1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusText.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun DrawScoreCard(
    draws: Int,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current

    Card(
        modifier = modifier
            .border(
                width = 1.dp,
                color = theme.gridColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surface)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
        ) {
            Text(
                text = "TIES",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = theme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = draws.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = theme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
