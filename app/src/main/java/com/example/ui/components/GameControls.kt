package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.BoardDimension
import com.example.game.CellMark
import com.example.game.Difficulty
import com.example.game.GameMode
import com.example.game.GameResult
import com.example.ui.theme.LocalAppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameControls(
    gameMode: GameMode,
    difficulty: Difficulty,
    dimension: BoardDimension,
    gameResult: GameResult,
    canUndo: Boolean,
    onModeChange: (GameMode) -> Unit,
    onDifficultyChange: (Difficulty) -> Unit,
    onDimensionChange: (BoardDimension) -> Unit,
    onUndoClick: () -> Unit,
    onHintClick: () -> Unit,
    onResetClick: () -> Unit,
    playerXName: String,
    playerOName: String,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Status Banner
        StatusBanner(
            gameResult = gameResult,
            playerXName = playerXName,
            playerOName = playerOName
        )

        // Game Configuration Selectors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mode Selectors
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                GameMode.values().forEach { mode ->
                    val selected = gameMode == mode
                    ElevatedFilterChip(
                        selected = selected,
                        onClick = { onModeChange(mode) },
                        label = {
                            Text(
                                text = mode.label,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            selectedContainerColor = theme.primary,
                            selectedLabelColor = Color.White,
                            containerColor = theme.surface,
                            labelColor = theme.onSurface
                        ),
                        modifier = Modifier.testTag("mode_${mode.name}")
                    )
                }
            }

            // Grid Size Selectors
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                BoardDimension.values().forEach { dim ->
                    val selected = dimension == dim
                    ElevatedFilterChip(
                        selected = selected,
                        onClick = { onDimensionChange(dim) },
                        label = {
                            Text(
                                text = dim.displayName,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            selectedContainerColor = theme.primary,
                            selectedLabelColor = Color.White,
                            containerColor = theme.surface,
                            labelColor = theme.onSurface
                        ),
                        modifier = Modifier.testTag("dimension_${dim.name}")
                    )
                }
            }
        }

        // Difficulty Selector (only when VS_AI)
        if (gameMode == GameMode.VS_AI) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI Level:",
                    style = MaterialTheme.typography.labelMedium,
                    color = theme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(end = 4.dp)
                )
                Difficulty.values().forEach { diff ->
                    val selected = difficulty == diff
                    ElevatedFilterChip(
                        selected = selected,
                        onClick = { onDifficultyChange(diff) },
                        label = {
                            Text(
                                text = diff.label,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            selectedContainerColor = Color(diff.badgeColorHex),
                            selectedLabelColor = Color.White,
                            containerColor = theme.surface,
                            labelColor = theme.onSurface
                        ),
                        modifier = Modifier.testTag("diff_${diff.name}")
                    )
                }
            }
        }

        // Action Buttons Row: Hint, Undo, New Game
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hint Button
            OutlinedButton(
                onClick = onHintClick,
                enabled = gameResult is GameResult.InProgress,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = theme.onSurface
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("hint_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Hint",
                    modifier = Modifier.size(18.dp),
                    tint = theme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Hint", fontSize = 13.sp)
            }

            // Undo Button
            OutlinedButton(
                onClick = onUndoClick,
                enabled = canUndo,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = theme.onSurface
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("undo_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Undo,
                    contentDescription = "Undo",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Undo", fontSize = 13.sp)
            }

            // New Game / Reset Button
            Button(
                onClick = onResetClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = theme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .weight(1.3f)
                    .height(48.dp)
                    .testTag("reset_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "New Game",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "New Game", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StatusBanner(
    gameResult: GameResult,
    playerXName: String,
    playerOName: String
) {
    val theme = LocalAppTheme.current

    AnimatedVisibility(
        visible = gameResult !is GameResult.InProgress,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        val (bannerText, bannerColor, icon) = when (gameResult) {
            is GameResult.Win -> {
                val winnerName = if (gameResult.winner == CellMark.X) playerXName else playerOName
                val color = if (gameResult.winner == CellMark.X) theme.xColor else theme.oColor
                Triple("$winnerName Wins!", color, Icons.Default.EmojiEvents)
            }
            is GameResult.Draw -> {
                Triple("It's a Draw!", theme.primary, Icons.Default.Handshake)
            }
            GameResult.InProgress -> Triple("", Color.Transparent, Icons.Default.Handshake)
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = bannerColor.copy(alpha = 0.16f),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, bannerColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .padding(vertical = 2.dp)
                .testTag("status_banner")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = bannerColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = bannerText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = bannerColor
                )
            }
        }
    }
}
