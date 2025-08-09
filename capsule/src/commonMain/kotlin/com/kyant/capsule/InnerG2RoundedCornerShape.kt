package com.kyant.capsule

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

@Stable
fun G2RoundedCornerShape.inner(
    innerPadding: Dp
): G2RoundedCornerShape {
    return InnerG2RoundedCornerShape(this, innerPadding)
}

@Immutable
private data class InnerG2RoundedCornerShape(
    val outerShape: G2RoundedCornerShape,
    val innerPadding: Dp
) :
    G2RoundedCornerShape(
        topStart = InnerCornerSize(outerShape.topStart, innerPadding),
        topEnd = InnerCornerSize(outerShape.topEnd, innerPadding),
        bottomEnd = InnerCornerSize(outerShape.bottomEnd, innerPadding),
        bottomStart = InnerCornerSize(outerShape.bottomStart, innerPadding),
        cornerSmoothness = outerShape.cornerSmoothness
    )

@Immutable
private data class InnerCornerSize(
    private val outerCornerSize: CornerSize,
    private val innerPadding: Dp
) : CornerSize {

    override fun toPx(shapeSize: Size, density: Density): Float {
        return outerCornerSize.toPx(shapeSize, density) - with(density) { innerPadding.toPx() }
    }
}
