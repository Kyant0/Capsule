package com.kyant.capsule.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import com.kyant.capsule.ContinuousCapsule
import com.kyant.capsule.continuities.G2Continuity
import com.kyant.capsule.continuities.G2ContinuityProfile
import com.kyant.capsule.path.toPath
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun MainContent() {
    val color = Color(0xFF0088FF)
    val contentColor = { Color.White }

    Column(
        Modifier
            .safeDrawingPadding()
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var isSvgExportDialogVisible by remember { mutableStateOf(false) }

        var showBaseline by remember { mutableStateOf(false) }
        var showCurvatureComb by remember { mutableStateOf(false) }

        val maxRadius = with(LocalDensity.current) {
            LocalWindowInfo.current.containerSize.width.toDp() / 2f - 16.dp
        }
        val radiusDp = remember { mutableFloatStateOf(64f) }
        var invertedAspectRatio by remember { mutableStateOf(false) }
        val aspectRatio = remember { mutableFloatStateOf(1f) }
        var scale by remember { mutableFloatStateOf(0.75f) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        val defaultContinuity = remember { G2Continuity() }

        val extendedFraction =
            remember { mutableFloatStateOf(defaultContinuity.profile.extendedFraction.toFloat()) }
        val arcFraction =
            remember { mutableFloatStateOf(defaultContinuity.profile.arcFraction.toFloat()) }
        val bezierCurvatureScale =
            remember { mutableFloatStateOf(defaultContinuity.profile.bezierCurvatureScale.toFloat()) }
        val arcCurvatureScale =
            remember { mutableFloatStateOf(defaultContinuity.profile.arcCurvatureScale.toFloat()) }

        val capsuleExtendedFraction =
            remember { mutableFloatStateOf(defaultContinuity.capsuleProfile.extendedFraction.toFloat()) }
        val capsuleArcFraction =
            remember { mutableFloatStateOf(defaultContinuity.capsuleProfile.arcFraction.toFloat()) }

        val currentContinuity by remember {
            derivedStateOf {
                G2Continuity(
                    profile = G2ContinuityProfile.RoundedRectangle.copy(
                        extendedFraction = extendedFraction.floatValue.toDouble(),
                        arcFraction = arcFraction.floatValue.toDouble(),
                        bezierCurvatureScale = bezierCurvatureScale.floatValue.toDouble(),
                        arcCurvatureScale = arcCurvatureScale.floatValue.toDouble()
                    ),
                    capsuleProfile = G2ContinuityProfile.Capsule.copy(
                        extendedFraction = capsuleExtendedFraction.floatValue.toDouble(),
                        arcFraction = capsuleArcFraction.floatValue.toDouble()
                    )
                )
            }
        }

        Box(
            Modifier
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    val width = placeable.width
                    val height = width
                    layout(width, height) {
                        placeable.place(0, 0)
                    }
                }
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .padding(16.dp)
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, zoom, rotation ->
                            scale = scale * zoom
                            offset += pan
                        }
                    }
                    .background(Color.Black.copy(alpha = 0.05f))
                    .clipToBounds()
                    .aspectRatio(1f)
                    .graphicsLayer {
                        translationX = offset.x
                        translationY = offset.y
                    }
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .drawBehind {
                        val radiusPx =
                            radiusDp.floatValue.dp.toPx().toDouble()
                                .fastCoerceIn(0.0, size.minDimension.toDouble() * 0.5)
                        val pathSegments = currentContinuity.createRoundedRectanglePathSegments(
                            width = size.width.toDouble(),
                            height = size.height.toDouble(),
                            topLeft = radiusPx,
                            topRight = radiusPx,
                            bottomRight = radiusPx,
                            bottomLeft = radiusPx
                        )

                        if (showCurvatureComb) {
                            val curvatureComb = pathSegments.toCurvatureComb(size.minDimension * 0.15f)
                            drawPath(
                                curvatureComb,
                                Color.Red,
                                style = Stroke(1.dp.toPx())
                            )
                        }

                        if (showBaseline) {
                            drawOutline(
                                RoundedCornerShape(radiusDp.floatValue.dp)
                                    .createOutline(size, layoutDirection, this),
                                color = Color.Red
                            )
                        }

                        drawPath(
                            pathSegments.toPath(),
                            color
                        )
                    }
                    .layout { measurable, constraints ->
                        val width: Int
                        val height: Int
                        if (!invertedAspectRatio) {
                            width = constraints.maxWidth
                            height = (width / aspectRatio.floatValue).fastRoundToInt()
                        } else {
                            height = constraints.maxHeight
                            width = (height / aspectRatio.floatValue).fastRoundToInt()
                        }
                        val placeable = measurable.measure(Constraints.fixed(width, height))

                        layout(width, height) {
                            placeable.place(0, 0)
                        }
                    }
                    .fillMaxWidth()
            )
        }

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(end = 64.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    Modifier
                        .clip(ContinuousCapsule)
                        .background(color)
                        .clickable { showBaseline = !showBaseline }
                        .height(40.dp)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        if (showBaseline) "Hide baseline"
                        else "Show baseline",
                        color = contentColor
                    )
                }

                Box(
                    Modifier
                        .clip(ContinuousCapsule)
                        .background(color)
                        .clickable { showCurvatureComb = !showCurvatureComb }
                        .height(40.dp)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        if (showCurvatureComb) "Hide curvature comb"
                        else "Show curvature comb",
                        color = contentColor
                    )
                }

                Box(
                    Modifier
                        .clip(ContinuousCapsule)
                        .background(color)
                        .clickable {
                            with(defaultContinuity.profile) {
                                extendedFraction.floatValue = this.extendedFraction.toFloat()
                                arcFraction.floatValue = this.arcFraction.toFloat()
                                bezierCurvatureScale.floatValue = this.bezierCurvatureScale.toFloat()
                                arcCurvatureScale.floatValue = this.arcCurvatureScale.toFloat()
                            }

                            with(defaultContinuity.capsuleProfile) {
                                capsuleExtendedFraction.floatValue = this.extendedFraction.toFloat()
                                capsuleArcFraction.floatValue = this.arcFraction.toFloat()
                            }
                        }
                        .height(40.dp)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        "Reset profiles",
                        color = contentColor
                    )
                }

                Box(
                    Modifier
                        .clip(ContinuousCapsule)
                        .background(color)
                        .clickable { isSvgExportDialogVisible = true }
                        .height(40.dp)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        "Export SVG",
                        color = contentColor
                    )
                }

                if (isSvgExportDialogVisible) {
                    SvgExportDialog(
                        onDismissRequest = { isSvgExportDialogVisible = false },
                        continuity = { currentContinuity }
                    )
                }
            }

            BasicText(
                "Shape",
                Modifier.padding(horizontal = 8.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Slider(
                    radiusDp,
                    0f..maxRadius.value,
                    "Corner radius",
                    { "${it.roundToInt()}dp" },
                )
                Slider(
                    aspectRatio,
                    1f..3f,
                    "Aspect ratio",
                    { it.toFixed(3) },
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        Modifier
                            .clip(ContinuousCapsule)
                            .background(color)
                            .clickable {
                                radiusDp.floatValue = 64f
                                aspectRatio.floatValue = 1f
                                scale = 0.75f
                                offset = Offset.Zero
                            }
                            .height(40.dp)
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            "Reset",
                            color = contentColor
                        )
                    }
                    Box(
                        Modifier
                            .clip(ContinuousCapsule)
                            .background(color)
                            .clickable { invertedAspectRatio = !invertedAspectRatio }
                            .height(40.dp)
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            if (invertedAspectRatio) "Inverted aspect ratio"
                            else "Invert aspect ratio",
                            color = contentColor
                        )
                    }
                }
            }

            BasicText(
                "G2 profile of rounded rectangle",
                Modifier.padding(horizontal = 8.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Slider(
                    extendedFraction,
                    0f..1f,
                    "Extended fraction",
                    { (it * 100f).toFixed(3) + "%" }
                )
                Slider(
                    arcFraction,
                    0f..0.8f,
                    "Arc fraction",
                    { (it * 100f).toFixed(3) + "%" }
                )
                Slider(
                    bezierCurvatureScale,
                    0.5f..2f,
                    "Bezier curvature scale",
                    { (it * 100f).toFixed(3) + "%" }
                )
                Slider(
                    arcCurvatureScale,
                    0.85f..1.5f,
                    "Arc curvature scale",
                    { (it * 100f).toFixed(3) + "%" }
                )
            }

            BasicText(
                "G2 profile of capsule",
                Modifier.padding(horizontal = 8.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Slider(
                    capsuleExtendedFraction,
                    0f..1f,
                    "Extended fraction",
                    { (it * 100f).toFixed(3) + "%" }
                )
                Slider(
                    capsuleArcFraction,
                    0f..1f,
                    "Arc fraction",
                    { (it * 100f).toFixed(3) + "%" }
                )
            }
        }
    }
}

private fun Float.toFixed(digits: Int): String =
    ((this * 10f.pow(digits)).roundToInt() / 10f.pow(digits)).toString()
    
