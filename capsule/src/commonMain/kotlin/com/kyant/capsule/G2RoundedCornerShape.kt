package com.kyant.capsule

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import kotlin.math.min

@Immutable
open class G2RoundedCornerShape(
    topStart: CornerSize,
    topEnd: CornerSize,
    bottomEnd: CornerSize,
    bottomStart: CornerSize,
    val cornerSmoothness: CornerSmoothness = CornerSmoothness.Default
) :
    CornerBasedShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart
    ) {

    override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        layoutDirection: LayoutDirection
    ): Outline {
        if (topStart + topEnd + bottomEnd + bottomStart == 0f) {
            return Outline.Rectangle(size.toRect())
        }

        val (width, height) = size
        val (centerX, centerY) = size.center

        val maxR = min(centerX, centerY)
        val topLeft = (if (layoutDirection == Ltr) topStart else topEnd).fastCoerceIn(0f, maxR)
        val topRight = (if (layoutDirection == Ltr) topEnd else topStart).fastCoerceIn(0f, maxR)
        val bottomRight = (if (layoutDirection == Ltr) bottomEnd else bottomStart).fastCoerceIn(0f, maxR)
        val bottomLeft = (if (layoutDirection == Ltr) bottomStart else bottomEnd).fastCoerceIn(0f, maxR)

        if (cornerSmoothness.circleFraction >= 1f ||
            (width == height &&
                    topLeft == centerX &&
                    topLeft == topRight && bottomLeft == bottomRight)
        ) {
            return Outline.Rounded(
                RoundRect(
                    rect = size.toRect(),
                    topLeft = CornerRadius(topLeft),
                    topRight = CornerRadius(topRight),
                    bottomRight = CornerRadius(bottomRight),
                    bottomLeft = CornerRadius(bottomLeft)
                )
            )
        }

        return Outline.Generic(
            cornerSmoothness.createRoundedRectanglePath(
                size = size,
                topRight = topRight,
                topLeft = topLeft,
                bottomLeft = bottomLeft,
                bottomRight = bottomRight
            )
        )
    }

    override fun copy(
        topStart: CornerSize,
        topEnd: CornerSize,
        bottomEnd: CornerSize,
        bottomStart: CornerSize
    ): G2RoundedCornerShape {
        return G2RoundedCornerShape(
            topStart = topStart,
            topEnd = topEnd,
            bottomEnd = bottomEnd,
            bottomStart = bottomStart,
            cornerSmoothness = cornerSmoothness
        )
    }

    fun copy(
        topStart: CornerSize = this.topStart,
        topEnd: CornerSize = this.topEnd,
        bottomEnd: CornerSize = this.bottomEnd,
        bottomStart: CornerSize = this.bottomStart,
        cornerSmoothness: CornerSmoothness = this.cornerSmoothness
    ): G2RoundedCornerShape {
        return G2RoundedCornerShape(
            topStart = topStart,
            topEnd = topEnd,
            bottomEnd = bottomEnd,
            bottomStart = bottomStart,
            cornerSmoothness = cornerSmoothness
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is G2RoundedCornerShape) return false

        if (topStart != other.topStart) return false
        if (topEnd != other.topEnd) return false
        if (bottomEnd != other.bottomEnd) return false
        if (bottomStart != other.bottomStart) return false
        if (cornerSmoothness != other.cornerSmoothness) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topStart.hashCode()
        result = 31 * result + topEnd.hashCode()
        result = 31 * result + bottomEnd.hashCode()
        result = 31 * result + bottomStart.hashCode()
        result = 31 * result + cornerSmoothness.hashCode()
        return result
    }

    override fun toString(): String {
        return "G2RoundedCornerShape(topStart=$topStart, topEnd=$topEnd, bottomEnd=$bottomEnd, " +
                "bottomStart=$bottomStart, cornerSmoothing=$cornerSmoothness)"
    }
}

@Stable
val G2RectangleShape: G2RoundedCornerShape = G2RoundedCornerShape(0f)

@Stable
val CapsuleShape: G2RoundedCornerShape = CapsuleShape()

@Suppress("FunctionName")
@Stable
fun CapsuleShape(
    cornerSmoothness: CornerSmoothness = CornerSmoothness.Default
): G2RoundedCornerShape =
    G2RoundedCornerShape(
        topStartPercent = 50,
        topEndPercent = 50,
        bottomEndPercent = 50,
        bottomStartPercent = 50,
        cornerSmoothness = cornerSmoothness
    )

@Stable
fun G2RoundedCornerShape(
    corner: CornerSize,
    cornerSmoothness: CornerSmoothness = CornerSmoothness.Default
): G2RoundedCornerShape =
    G2RoundedCornerShape(
        topStart = corner,
        topEnd = corner,
        bottomEnd = corner,
        bottomStart = corner,
        cornerSmoothness = cornerSmoothness
    )

@Stable
fun G2RoundedCornerShape(
    size: Dp,
    cornerSmoothness: CornerSmoothness = CornerSmoothness.Default
): G2RoundedCornerShape =
    G2RoundedCornerShape(
        corner = CornerSize(size),
        cornerSmoothness = cornerSmoothness
    )

@Stable
fun G2RoundedCornerShape(
    @FloatRange(from = 0.0) size: Float,
    cornerSmoothness: CornerSmoothness = CornerSmoothness.Default
): G2RoundedCornerShape =
    G2RoundedCornerShape(
        corner = CornerSize(size),
        cornerSmoothness = cornerSmoothness
    )

@Stable
fun G2RoundedCornerShape(
    @IntRange(from = 0, to = 100) percent: Int,
    cornerSmoothness: CornerSmoothness = CornerSmoothness.Default
): G2RoundedCornerShape =
    G2RoundedCornerShape(
        corner = CornerSize(percent),
        cornerSmoothness = cornerSmoothness
    )

@Stable
fun G2RoundedCornerShape(
    topStart: Dp = 0.dp,
    topEnd: Dp = 0.dp,
    bottomEnd: Dp = 0.dp,
    bottomStart: Dp = 0.dp,
    cornerSmoothness: CornerSmoothness = CornerSmoothness.Default
): G2RoundedCornerShape =
    G2RoundedCornerShape(
        topStart = CornerSize(topStart),
        topEnd = CornerSize(topEnd),
        bottomEnd = CornerSize(bottomEnd),
        bottomStart = CornerSize(bottomStart),
        cornerSmoothness = cornerSmoothness
    )

@Stable
fun G2RoundedCornerShape(
    @FloatRange(from = 0.0) topStart: Float = 0f,
    @FloatRange(from = 0.0) topEnd: Float = 0f,
    @FloatRange(from = 0.0) bottomEnd: Float = 0f,
    @FloatRange(from = 0.0) bottomStart: Float = 0f,
    cornerSmoothness: CornerSmoothness = CornerSmoothness.Default
): G2RoundedCornerShape =
    G2RoundedCornerShape(
        topStart = CornerSize(topStart),
        topEnd = CornerSize(topEnd),
        bottomEnd = CornerSize(bottomEnd),
        bottomStart = CornerSize(bottomStart),
        cornerSmoothness = cornerSmoothness
    )

@Stable
fun G2RoundedCornerShape(
    @IntRange(from = 0, to = 100) topStartPercent: Int = 0,
    @IntRange(from = 0, to = 100) topEndPercent: Int = 0,
    @IntRange(from = 0, to = 100) bottomEndPercent: Int = 0,
    @IntRange(from = 0, to = 100) bottomStartPercent: Int = 0,
    cornerSmoothness: CornerSmoothness = CornerSmoothness.Default
): G2RoundedCornerShape =
    G2RoundedCornerShape(
        topStart = CornerSize(topStartPercent),
        topEnd = CornerSize(topEndPercent),
        bottomEnd = CornerSize(bottomEndPercent),
        bottomStart = CornerSize(bottomStartPercent),
        cornerSmoothness = cornerSmoothness
    )
