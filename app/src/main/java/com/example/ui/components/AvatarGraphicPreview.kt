package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.data.models.AvatarCustomizationState

@Composable
fun AvatarGraphicPreview(
    state: AvatarCustomizationState,
    modifier: Modifier = Modifier,
    characterName: String = "Aura Character"
) {
  val infiniteTransition = rememberInfiniteTransition(label = "avatarGlow")
  val glowOffset by
      infiniteTransition.animateFloat(
          initialValue = 0f,
          targetValue = 20f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(2000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
          label = "glow")

  // Color mappings based on selections
  val hairColorValue =
      when (state.hairColor) {
        "Platinum Blonde" -> Color(0xFFFFF1B0)
        "Midnight Black" -> Color(0xFF1F1C2B)
        "Pastel Pink" -> Color(0xFFFFB7B2)
        "Honey Brown" -> Color(0xFFB57242)
        "Crimson Red" -> Color(0xFFD32F2F)
        "Electric Violet" -> Color(0xFF9C27B0)
        else -> Color(0xFFFFF1B0)
      }

  val eyeColorValue =
      when (state.eyeColor) {
        "Emerald Green" -> Color(0xFF2ECC71)
        "Sapphire Blue" -> Color(0xFF3498DB)
        "Dark Amber" -> Color(0xFFE67E22)
        "Violet Glow" -> Color(0xFF8E44AD)
        "Ice Blue" -> Color(0xFF00E5FF)
        else -> Color(0xFF2ECC71)
      }

  val outfitColor =
      when (state.outfit) {
        "Luxury Silk Gown" -> Color(0xFFFFD700)
        "Casual Streetwear" -> Color(0xFF00E5FF)
        "Cyber Neon Jacket" -> Color(0xFFFF1744)
        "Cozy Cashmere Sweater" -> Color(0xFFFF80AB)
        "Evening Cocktail Dress" -> Color(0xFF9C27B0)
        else -> Color(0xFFFFD700)
      }

  val bgGradient =
      when (state.backgroundVibe) {
        "Sunset Penthouse" -> listOf(Color(0xFF2C1051), Color(0xFFFF5252), Color(0xFFFFB300))
        "Neon Cyberpunk Lounge" -> listOf(Color(0xFF0D0B27), Color(0xFF8E24AA), Color(0xFF00E5FF))
        "Cozy Coffee Shop" -> listOf(Color(0xFF3E2723), Color(0xFF6D4C41), Color(0xFFD7CCC8))
        "Luxury Yacht Deck" -> listOf(Color(0xFF002171), Color(0xFF0D47A1), Color(0xFF80DEEA))
        else -> listOf(Color(0xFF2C1051), Color(0xFFFF5252), Color(0xFFFFB300))
      }

  val skinColor =
      when (state.skinTone) {
        "Warm Porcelain" -> Color(0xFFFFE0BD)
        "Golden Sunkissed" -> Color(0xFFF1C27D)
        "Deep Bronze" -> Color(0xFFC68642)
        "Fair Soft Glow" -> Color(0xFFFFF0E1)
        else -> Color(0xFFFFE0BD)
      }

  Card(
      modifier = modifier.fillMaxWidth().height(280.dp),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(bgGradient))) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f

            // Ambient glow halo
            drawCircle(
                brush =
                    Brush.radialGradient(
                        colors = listOf(hairColorValue.copy(alpha = 0.4f), Color.Transparent),
                        center = Offset(centerX, centerY - 20f),
                        radius = 180f + glowOffset),
                center = Offset(centerX, centerY - 20f),
                radius = 180f + glowOffset)

            // Character Torso / Outfit
            val outfitPath =
                Path().apply {
                  moveTo(centerX - 90f, height)
                  lineTo(centerX - 60f, height - 90f)
                  quadraticTo(centerX, height - 120f, centerX + 60f, height - 90f)
                  lineTo(centerX + 90f, height)
                  close()
                }
            drawPath(outfitPath, color = outfitColor)

            // Neck
            drawRect(
                color = skinColor,
                topLeft = Offset(centerX - 20f, height - 130f),
                size = Size(40f, 30f))

            // Accessory: Choker or Necklace
            if (state.accessory.contains("Choker") ||
                state.accessory.contains("Earrings") ||
                state.accessory.contains("Gold")) {
              drawRect(
                  color = Color(0xFFFFD700),
                  topLeft = Offset(centerX - 22f, height - 118f),
                  size = Size(44f, 8f))
            }

            // Head/Face Oval
            val faceCenterY = height - 180f
            drawOval(
                color = skinColor,
                topLeft = Offset(centerX - 50f, faceCenterY - 60f),
                size = Size(100f, 120f))

            // Eyes
            val eyeY = faceCenterY - 10f
            // Left Eye
            drawCircle(color = Color.White, center = Offset(centerX - 22f, eyeY), radius = 10f)
            drawCircle(color = eyeColorValue, center = Offset(centerX - 22f, eyeY), radius = 6f)
            drawCircle(color = Color.Black, center = Offset(centerX - 22f, eyeY), radius = 3f)

            // Right Eye
            drawCircle(color = Color.White, center = Offset(centerX + 22f, eyeY), radius = 10f)
            drawCircle(color = eyeColorValue, center = Offset(centerX + 22f, eyeY), radius = 6f)
            drawCircle(color = Color.Black, center = Offset(centerX + 22f, eyeY), radius = 3f)

            // Eyebrows & Lashes
            drawLine(
                color = hairColorValue.copy(alpha = 0.9f),
                start = Offset(centerX - 32f, eyeY - 14f),
                end = Offset(centerX - 12f, eyeY - 12f),
                strokeWidth = 3f)
            drawLine(
                color = hairColorValue.copy(alpha = 0.9f),
                start = Offset(centerX + 12f, eyeY - 12f),
                end = Offset(centerX + 32f, eyeY - 14f),
                strokeWidth = 3f)

            // Lips
            drawArc(
                color = Color(0xFFFF5252),
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(centerX - 12f, faceCenterY + 22f),
                size = Size(24f, 14f))

            // Hair Drawing based on Hair Style
            when (state.hairStyle) {
              "Long Waves",
              "Curled Ponytail" -> {
                // Left flowing hair wave
                val leftHair =
                    Path().apply {
                      moveTo(centerX - 50f, faceCenterY - 50f)
                      quadraticTo(centerX - 80f, faceCenterY + 20f, centerX - 65f, height - 50f)
                      lineTo(centerX - 40f, height - 50f)
                      quadraticTo(centerX - 50f, faceCenterY, centerX - 40f, faceCenterY - 40f)
                      close()
                    }
                drawPath(leftHair, color = hairColorValue)

                // Right flowing hair wave
                val rightHair =
                    Path().apply {
                      moveTo(centerX + 50f, faceCenterY - 50f)
                      quadraticTo(centerX + 80f, faceCenterY + 20f, centerX + 65f, height - 50f)
                      lineTo(centerX + 40f, height - 50f)
                      quadraticTo(centerX + 50f, faceCenterY, centerX + 40f, faceCenterY - 40f)
                      close()
                    }
                drawPath(rightHair, color = hairColorValue)
              }
              else -> { // Short or Crown
                val topHair =
                    Path().apply {
                      moveTo(centerX - 55f, faceCenterY - 30f)
                      quadraticTo(centerX, faceCenterY - 90f, centerX + 55f, faceCenterY - 30f)
                      quadraticTo(centerX, faceCenterY - 50f, centerX - 55f, faceCenterY - 30f)
                      close()
                    }
                drawPath(topHair, color = hairColorValue)
              }
            }

            // Crown / Glasses Accessory
            if (state.accessory.contains("Crown")) {
              val crownPath =
                  Path().apply {
                    moveTo(centerX - 35f, faceCenterY - 60f)
                    lineTo(centerX - 35f, faceCenterY - 85f)
                    lineTo(centerX - 18f, faceCenterY - 70f)
                    lineTo(centerX, faceCenterY - 95f)
                    lineTo(centerX + 18f, faceCenterY - 70f)
                    lineTo(centerX + 35f, faceCenterY - 85f)
                    lineTo(centerX + 35f, faceCenterY - 60f)
                    close()
                  }
              drawPath(crownPath, color = Color(0xFFFFD700))
            } else if (state.accessory.contains("Glasses") || state.accessory.contains("Visor")) {
              drawRect(
                  color = Color.Black.copy(alpha = 0.7f),
                  topLeft = Offset(centerX - 40f, eyeY - 10f),
                  size = Size(80f, 22f))
              drawRect(
                  color = Color(0xFF00E5FF),
                  topLeft = Offset(centerX - 38f, eyeY - 8f),
                  size = Size(76f, 18f),
                  style = Stroke(width = 2f))
            }
          }

          // Overlay Badges
          Box(
              modifier = Modifier.fillMaxSize().padding(16.dp),
              contentAlignment = Alignment.BottomStart) {
                Column(
                    modifier =
                        Modifier.clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)) {
                      Text(
                          text = characterName,
                          style = MaterialTheme.typography.titleMedium,
                          color = Color.White)
                      Text(
                          text = "${state.hairColor} • ${state.eyeColor} • ${state.outfit}",
                          style = MaterialTheme.typography.labelSmall,
                          color = Color(0xFFFFD700))
                    }
              }
        }
      }
}
