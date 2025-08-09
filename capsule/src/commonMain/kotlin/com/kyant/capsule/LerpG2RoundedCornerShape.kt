package com.kyant.capsule

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.compose.ui.util.lerp

@Stable
fun lerp(
    start: G2RoundedCornerShape,
    stop: G2RoundedCornerShape,
    fraction: Float
): G2RoundedCornerShape {
    return LerpG2RoundedCornerShape(start, stop, fraction)
}

@Immutable
private data class LerpG2RoundedCornerShape(
    val start: G2RoundedCornerShape,
    val stop: G2RoundedCornerShape,
    val fraction: Float
) :
    G2RoundedCornerShape(
        topStart = LerpCornerSize(start.topStart, stop.topStart, fraction),
        topEnd = LerpCornerSize(start.topEnd, stop.topEnd, fraction),
        bottomEnd = LerpCornerSize(start.bottomEnd, stop.bottomEnd, fraction),
        bottomStart = LerpCornerSize(start.bottomStart, stop.bottomStart, fraction),
        cornerSmoothness =
            CornerSmoothness(
                circleFraction =
                    lerp(
                        start.cornerSmoothness.circleFraction,
                        stop.cornerSmoothness.circleFraction,
                        fraction.fastCoerceAtLeast(0f)
                    ),
                extendedFraction =
                    lerp(
                        start.cornerSmoothness.extendedFraction,
                        stop.cornerSmoothness.extendedFraction,
                        fraction.fastCoerceAtLeast(0f)
                    )
            )
    )

@Immutable
private data class LerpCornerSize(
    private val start: CornerSize,
    private val stop: CornerSize,
    private val fraction: Float
) : CornerSize {

    override fun toPx(shapeSize: Size, density: Density): Float {
        return lerp(
            start.toPx(shapeSize, density),
            stop.toPx(shapeSize, density),
            fraction
        ).fastCoerceAtLeast(0f)
    }
}
