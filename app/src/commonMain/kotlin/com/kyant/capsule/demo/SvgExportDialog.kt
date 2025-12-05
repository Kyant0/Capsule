package com.kyant.capsule.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.window.Dialog
import com.kyant.capsule.Continuity
import com.kyant.capsule.ContinuousCapsule
import com.kyant.capsule.ContinuousRoundedRectangle
import com.kyant.capsule.concentricOutset
import com.kyant.capsule.path.toPath
import com.kyant.capsule.path.toSvg
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@Composable
fun SvgExportDialog(
    onDismissRequest: () -> Unit,
    continuity: () -> Continuity
) {
    Dialog(onDismissRequest = onDismissRequest) {
        val color = Color(0xFF0088FF)
        val contentColor = { Color.White }

        val widthText = rememberTextFieldState("1000")
        val heightText = rememberTextFieldState("1000")
        val radiusText = rememberTextFieldState("250")

        var width by remember { mutableDoubleStateOf(1000.0) }
        var height by remember { mutableDoubleStateOf(1000.0) }
        var radius by remember { mutableDoubleStateOf(250.0) }
        val currentPathSegments by remember {
            derivedStateOf {
                val radius = radius.fastCoerceIn(0.0, min(width, height) * 0.5)
                continuity().createRoundedRectanglePathSegments(
                    width = width,
                    height = height,
                    topLeft = radius,
                    topRight = radius,
                    bottomRight = radius,
                    bottomLeft = radius
                )
            }
        }

        LaunchedEffect(Unit) {
            launch {
                snapshotFlow { widthText.text.toString().toDoubleOrNull() }
                    .collectLatest {
                        if (it != null && it > 0) {
                            width = it
                        }
                    }
            }
            launch {
                snapshotFlow { heightText.text.toString().toDoubleOrNull() }
                    .collectLatest {
                        if (it != null && it > 0) {
                            height = it
                        }
                    }
            }
            launch {
                snapshotFlow { radiusText.text.toString().toDoubleOrNull() }
                    .collectLatest {
                        if (it != null && it >= 0) {
                            radius = it
                        }
                    }
            }
        }

        Column(
            Modifier
                .clip(ContinuousRoundedRectangle(24.dp).concentricOutset(16.dp))
                .background(Color.White)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BasicText(
                "Export SVG",
                Modifier.padding(8.dp),
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium)
            )

            Box(
                Modifier
                    .padding(horizontal = 16.dp)
                    .drawBehind {
                        withTransform({
                            val maxDimension = max(width, height).toFloat()
                            val scale = size.minDimension / maxDimension
                            scale(scale, Offset.Zero)
                            translate(
                                (maxDimension - width.toFloat()) * 0.5f,
                                (maxDimension - height.toFloat()) * 0.5f
                            )
                        }) {
                            drawPath(
                                currentPathSegments.toPath(),
                                color
                            )
                        }
                    }
                    .aspectRatio(1f)
                    .align(Alignment.CenterHorizontally)
            )

            RowNoInline(
                Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    BasicText("Width")
                    BasicTextField(
                        widthText,
                        Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black.copy(alpha = 0.6f)),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )
                    )
                }

                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    BasicText("Height")
                    BasicTextField(
                        heightText,
                        Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black.copy(alpha = 0.6f)),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )
                    )
                }

                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    BasicText("Radius")
                    BasicTextField(
                        radiusText,
                        Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black.copy(alpha = 0.6f)),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
//                val context = LocalContext.current
//                val createFileLauncher = rememberLauncherForActivityResult(
//                    contract = ActivityResultContracts.StartActivityForResult()
//                ) { result ->
//                    if (result.resultCode == Activity.RESULT_OK) {
//                        val uri = result.data?.data
//                        if (uri != null) {
//                            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
//                                val svg = currentPathSegments.toSvg(asDocument = true)
//                                outputStream.write(svg.toByteArray())
//                            }
//                        }
//                    }
//                }

                Box(
                    Modifier
                        .clip(ContinuousCapsule)
                        .background(color)
                        .clickable {
//                            val svg = currentPathSegments.toSvg(asDocument = true)
//                            val tempFile =
//                                File(context.cacheDir, "continuous_rounded_rect.svg").apply {
//                                    writeBytes(svg.toByteArray())
//                                }
//                            val uri = FileProvider.getUriForFile(
//                                context,
//                                "${context.packageName}.provider",
//                                tempFile
//                            )
//                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
//                                type = "image/svg+xml"
//                                putExtra(Intent.EXTRA_STREAM, uri)
//                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                            }
//                            context.startActivity(Intent.createChooser(shareIntent, "Share SVG"))
                        }
                        .height(48.dp)
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        "Share",
                        color = contentColor
                    )
                }

                Box(
                    Modifier
                        .clip(ContinuousCapsule)
                        .background(color)
                        .clickable {
//                            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
//                                addCategory(Intent.CATEGORY_OPENABLE)
//                                type = "image/svg+xml"
//                                putExtra(Intent.EXTRA_TITLE, "continuous_rounded_rect.svg")
//                            }
//                            createFileLauncher.launch(intent)
                        }
                        .height(48.dp)
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        "Save",
                        color = contentColor
                    )
                }
            }
        }
    }
}
