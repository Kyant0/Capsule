package com.kyant.capsule.demo

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.dp
import com.kyant.capsule.CapsuleShape

@Composable
fun Slider(
    state: MutableState<Float>,
    valueRange: ClosedRange<Float>,
    label: String,
    value: (value: Float) -> String,
    modifier: Modifier = Modifier
) {
    val layoutDirection = LocalLayoutDirection.current
    val sliderColor = Color(0xFF90CAF9)

    val range = valueRange.endInclusive - valueRange.start

    Column(
        modifier
            .border(1.dp, Color(0xFF2196F3), CapsuleShape)
            .clip(CapsuleShape)
            .drawBehind {
                val value = (state.value - valueRange.start) / range
                drawRect(
                    sliderColor,
                    topLeft =
                        if (layoutDirection == Ltr) Offset.Zero
                        else Offset(size.width * (1f - value), 0f),
                    size = size.copy(width = size.width * value)
                )
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    val delta = dragAmount.x / size.width * if (layoutDirection == Ltr) 1f else -1f
                    state.value = (state.value + delta * range).coerceIn(valueRange)
                }
            }
            .fillMaxWidth()
            .padding(16.dp, 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        BasicText(label)

        BasicText(value(state.value))
    }
}
