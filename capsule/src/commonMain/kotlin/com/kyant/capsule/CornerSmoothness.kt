package com.kyant.capsule

import androidx.annotation.FloatRange
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.util.fastCoerceAtMost
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Immutable
data class CornerSmoothness(
    @param:FloatRange(from = 0.0, to = 1.0) val circleFraction: Float,
    @param:FloatRange(from = 0.0) val extendedFraction: Float
) {

    private val circleRadians = HalfPI * circleFraction
    private val bezierRadians = (HalfPI - circleRadians) / 2f

    private val sin = sin(bezierRadians)
    private val cos = cos(bezierRadians)
    private val a = 1f - sin / (1f + cos) // 2/3 at arcsin(0.6)
    private val d = 1.5f * sin / (1f + cos) / (1f + cos)
    private val ad = a + d // minimum 17/18 at arcsin(0.6)

    private fun Path.topRightCorner0(size: Size, r: Float, dy: Float) {
        val w = size.width
        cubicTo(
            w,
            r * ad,
            w,
            r * a,
            w - r * (1f - cos),
            r * (1f - sin)
        )
    }

    private fun Path.topRightCircle(size: Size, r: Float) {
        if (circleRadians > 0f) {
            arcToRad(
                rect = Rect(
                    center = Offset(size.width - r, r),
                    radius = r
                ),
                startAngleRadians = -bezierRadians,
                sweepAngleRadians = -circleRadians,
                forceMoveTo = false
            )
        }
    }

    private fun Path.topRightCorner1(size: Size, r: Float, dx: Float) {
        val w = size.width
        cubicTo(
            w - r * a,
            0f,
            w - r * ad,
            0f,
            w - r - dx,
            0f
        )
    }

    private fun Path.topLeftCorner1(size: Size, r: Float, dx: Float) {
        cubicTo(
            r * ad,
            0f,
            r * a,
            0f,
            r * (1f - sin),
            r * (1f - cos)
        )
    }

    private fun Path.topLeftCircle(size: Size, r: Float) {
        if (circleRadians > 0f) {
            arcToRad(
                rect = Rect(
                    center = Offset(r, r),
                    radius = r
                ),
                startAngleRadians = -(HalfPI + bezierRadians),
                sweepAngleRadians = -circleRadians,
                forceMoveTo = false
            )
        }
    }

    private fun Path.topLeftCorner0(size: Size, r: Float, dy: Float) {
        cubicTo(
            0f,
            r * a,
            0f,
            r * ad,
            0f,
            r + dy
        )
    }

    private fun Path.bottomLeftCorner0(size: Size, r: Float, dy: Float) {
        val h = size.height
        cubicTo(
            0f,
            h - r * ad,
            0f,
            h - r * a,
            r * (1f - cos),
            h - r * (1f - sin)
        )
    }

    private fun Path.bottomLeftCircle(size: Size, r: Float) {
        if (circleRadians > 0f) {
            arcToRad(
                rect = Rect(
                    center = Offset(r, size.height - r),
                    radius = r
                ),
                startAngleRadians = -(HalfPI * 2f + bezierRadians),
                sweepAngleRadians = -circleRadians,
                forceMoveTo = false
            )
        }
    }

    private fun Path.bottomLeftCorner1(size: Size, r: Float, dx: Float) {
        val h = size.height
        cubicTo(
            r * a,
            h,
            r * ad,
            h,
            r - dx,
            h
        )
    }

    private fun Path.bottomRightCorner1(size: Size, r: Float, dx: Float) {
        val w = size.width
        val h = size.height
        cubicTo(
            w - r * ad,
            h,
            w - r * a,
            h,
            w - r * (1f - sin),
            h - r * (1f - cos)
        )
    }

    private fun Path.bottomRightCircle(size: Size, r: Float) {
        if (circleRadians > 0f) {
            arcToRad(
                rect = Rect(
                    center = Offset(size.width - r, size.height - r),
                    radius = r
                ),
                startAngleRadians = -(HalfPI * 3f + bezierRadians),
                sweepAngleRadians = -circleRadians,
                forceMoveTo = false
            )
        }
    }

    private fun Path.bottomRightCorner0(size: Size, r: Float, dy: Float) {
        val w = size.width
        val h = size.height
        cubicTo(
            w,
            h - r * a,
            w,
            h - r * ad,
            w,
            h - r + dy
        )
    }

    private fun Path.rightCircle(size: Size, r: Float) {
        arcToRad(
            rect = Rect(
                center = Offset(size.width - r, r),
                radius = r
            ),
            startAngleRadians = bezierRadians + circleRadians,
            sweepAngleRadians = -(bezierRadians + circleRadians) * 2f,
            forceMoveTo = false
        )
    }

    private fun Path.leftCircle(size: Size, r: Float) {
        arcToRad(
            rect = Rect(
                center = Offset(r, r),
                radius = r
            ),
            startAngleRadians = -(HalfPI + bezierRadians),
            sweepAngleRadians = -(bezierRadians + circleRadians) * 2f,
            forceMoveTo = false
        )
    }

    private fun Path.topCircle(size: Size, r: Float) {
        arcToRad(
            rect = Rect(
                center = Offset(r, r),
                radius = r
            ),
            startAngleRadians = -bezierRadians,
            sweepAngleRadians = -(bezierRadians + circleRadians) * 2f,
            forceMoveTo = false
        )
    }

    private fun Path.bottomCircle(size: Size, r: Float) {
        arcToRad(
            rect = Rect(
                center = Offset(r, size.height - r),
                radius = r
            ),
            startAngleRadians = -(HalfPI * 2f + bezierRadians),
            sweepAngleRadians = -(bezierRadians + circleRadians) * 2f,
            forceMoveTo = false
        )
    }

    internal fun createRoundedRectanglePath(
        size: Size,
        topRight: Float,
        topLeft: Float,
        bottomLeft: Float,
        bottomRight: Float
    ): Path {
        val (width, height) = size
        val (centerX, centerY) = size.center
        val maxR = min(centerX, centerY)

        val topRightDy = (topRight * extendedFraction).fastCoerceAtMost(centerY - topRight)
        val topRightDx = (topRight * extendedFraction).fastCoerceAtMost(centerX - topRight)
        val topLeftDx = (topLeft * extendedFraction).fastCoerceAtMost(centerX - topLeft)
        val topLeftDy = (topLeft * extendedFraction).fastCoerceAtMost(centerY - topLeft)
        val bottomLeftDy = (bottomLeft * extendedFraction).fastCoerceAtMost(centerY - bottomLeft)
        val bottomLeftDx = (bottomLeft * extendedFraction).fastCoerceAtMost(centerX - bottomLeft)
        val bottomRightDx = (bottomRight * extendedFraction).fastCoerceAtMost(centerX - bottomRight)
        val bottomRightDy = (bottomRight * extendedFraction).fastCoerceAtMost(centerY - bottomRight)

        return Path().apply {
            when {
                // capsule
                topRight == maxR && topLeft == maxR && bottomLeft == maxR && bottomRight == maxR -> {
                    if (width > height) {
                        // right circle
                        rightCircle(size, maxR)
                        // top right corner
                        topRightCorner1(size, topRight, topRightDx)
                        // top line
                        lineTo(topLeft + topLeftDx, 0f)
                        // top left corner
                        topLeftCorner1(size, topLeft, topLeftDx)
                        // left circle
                        leftCircle(size, maxR)
                        // bottom left corner
                        bottomLeftCorner1(size, bottomLeft, -bottomLeftDx)
                        // bottom line
                        lineTo(width - bottomRight - bottomRightDx, height)
                        // bottom right corner
                        bottomRightCorner1(size, bottomRight, -bottomRightDx)
                    } else {
                        // right line
                        moveTo(width, height - bottomRight - bottomRightDy)
                        lineTo(width, topRight + topRightDy)
                        // top right corner
                        topRightCorner0(size, topRight, -topRightDy)
                        // top circle
                        topCircle(size, maxR)
                        // top left corner
                        topLeftCorner0(size, topLeft, topLeftDy)
                        // left line
                        lineTo(0f, height - bottomLeft - bottomLeftDy)
                        // bottom left corner
                        bottomLeftCorner0(size, bottomLeft, bottomLeftDy)
                        // bottom circle
                        bottomCircle(size, maxR)
                        // bottom right corner
                        bottomRightCorner0(size, bottomRight, -bottomRightDy)
                    }
                }

                // rounded rectangle
                else -> {
                    // right line
                    moveTo(width, height - bottomRight - bottomRightDy)
                    lineTo(width, topRight + topRightDy)

                    // top right corner
                    if (topRight > 0f) {
                        topRightCorner0(size, topRight, -topRightDy)
                        topRightCircle(size, topRight)
                        topRightCorner1(size, topRight, topRightDx)
                    }

                    // top line
                    lineTo(topLeft + topLeftDx, 0f)

                    // top left corner
                    if (topLeft > 0f) {
                        topLeftCorner1(size, topLeft, topLeftDx)
                        topLeftCircle(size, topLeft)
                        topLeftCorner0(size, topLeft, topLeftDy)
                    }

                    // left line
                    lineTo(0f, height - bottomLeft - bottomLeftDy)

                    // bottom left corner
                    if (bottomLeft > 0f) {
                        bottomLeftCorner0(size, bottomLeft, bottomLeftDy)
                        bottomLeftCircle(size, bottomLeft)
                        bottomLeftCorner1(size, bottomLeft, -bottomLeftDx)
                    }

                    // bottom line
                    lineTo(width - bottomRight - bottomRightDx, height)

                    // bottom right corner
                    if (bottomRight > 0f) {
                        bottomRightCorner1(size, bottomRight, -bottomRightDx)
                        bottomRightCircle(size, bottomRight)
                        bottomRightCorner0(size, bottomRight, -bottomRightDy)
                    }
                }
            }
        }
    }

    companion object {

        @Stable
        val Default: CornerSmoothness =
            CornerSmoothness(
                // ~= 16.26 deg, bezierRadians = arcsin(0.6)
                circleFraction = 1f - 2f * asin(0.6f) / HalfPI,
                extendedFraction = 0.75f
            )

        @Stable
        val None: CornerSmoothness =
            CornerSmoothness(
                circleFraction = 1f,
                extendedFraction = 0f
            )
    }
}

private const val HalfPI = (PI / 2f).toFloat()
