package com.kyant.capsule.demo

import androidx.compose.ui.graphics.Path
import com.kyant.capsule.core.Point
import com.kyant.capsule.path.PathSegment
import com.kyant.capsule.path.PathSegments
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal fun PathSegments.toCurvatureComb(
    scale: Float = 100f,
    density: Int = 50
): Path {
    val path = Path()
    val maxCurvature = maxOf { segment ->
        (0..density).maxOf { i ->
            val t = i / density.toDouble()
            segment.curvatureAt(t)
        }
    }
    forEach { segment ->
        for (i in 0..density) {
            val t = i / density.toDouble()
            val point = segment.pointAt(t)
            val tangent = segment.unitTangentAt(t)
            val normal = Point(-tangent.y, tangent.x)
            val curvature = segment.curvatureAt(t)
            val combLength = curvature / maxCurvature * scale
            path.moveTo(
                point.x.toFloat(),
                point.y.toFloat()
            )
            path.lineTo(
                (point.x - normal.x * combLength).toFloat(),
                (point.y - normal.y * combLength).toFloat()
            )
        }
    }
    return path
}

internal fun PathSegments.toTangentialAngleComb(
    scale: Float = -300f,
    density: Int = 400
): Path {
    val path = Path()
    var x = 0f
    take(3).forEach { segment ->
        for (i in 0..density) {
            val t = i / density.toDouble()
            val tangent = segment.unitTangentAt(t)
            val angle = atan2(tangent.y, tangent.x)
            val combLength = angle * scale
            path.moveTo(x, 0f)
            path.lineTo(x, combLength.toFloat())
            x += 1f
        }
    }
    return path
}

private fun PathSegment.pointAt(t: Double): Point =
    when (this) {
        is PathSegment.Line -> from + (to - from) * t

        is PathSegment.Arc -> {
            val angle = startAngle + sweepAngle * t
            Point(
                center.x + cos(angle) * radius,
                center.y + sin(angle) * radius
            )
        }

        is PathSegment.Circle -> {
            val angle = 2.0 * PI * t
            Point(
                center.x + cos(angle) * radius,
                center.y + sin(angle) * radius
            )
        }

        is PathSegment.Cubic -> {
            val u = 1.0 - t
            p0 * (u * u * u) + p1 * (3.0 * u * u * t) + p2 * (3.0 * u * t * t) + p3 * (t * t * t)
        }
    }

private fun PathSegment.unitTangentAt(t: Double): Point =
    when (this) {
        is PathSegment.Line -> (to - from).normalized()
        is PathSegment.Arc -> {
            val angle = startAngle + sweepAngle * t
            Point(-sin(angle), cos(angle))
        }

        is PathSegment.Circle -> {
            val angle = 2.0 * PI * t
            Point(-sin(angle), cos(angle))
        }

        is PathSegment.Cubic -> {
            val u = 1.0 - t
            val d1 = (p1 - p0) * (3.0 * u * u) + (p2 - p1) * (6.0 * u * t) + (p3 - p2) * (3.0 * t * t)
            d1.normalized()
        }
    }

private fun PathSegment.curvatureAt(t: Double): Double =
    when (this) {
        is PathSegment.Line -> 0.0
        is PathSegment.Arc -> 1.0 / radius
        is PathSegment.Circle -> 1.0 / radius
        is PathSegment.Cubic -> {
            val u = 1.0 - t
            val d1 = (p1 - p0) * (3.0 * u * u) + (p2 - p1) * (6.0 * u * t) + (p3 - p2) * (3.0 * t * t)
            val d2 = (p2 - p1 * 2.0 + p0) * (6.0 * u) + (p3 - p2 * 2.0 + p1) * (6.0 * t)
            val cross = d1.x * d2.y - d1.y * d2.x
            val d1Length = sqrt(d1.x * d1.x + d1.y * d1.y)
            if (d1Length != 0.0) {
                cross / (d1Length * d1Length * d1Length)
            } else {
                0.0
            }
        }
    }
