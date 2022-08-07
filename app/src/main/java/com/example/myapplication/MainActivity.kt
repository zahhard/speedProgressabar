package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SpeedometerScreen()
                }
            }
        }
    }
}

@Composable
fun SpeedometerScreen() {
    var targetValue by remember {
        mutableStateOf(0f)
    }
    val progress = remember(targetValue) { Animatable(initialValue = 0f) }
    val scope = rememberCoroutineScope()
    Column(Modifier.padding(16.dp)) {
        Slider(value = targetValue, onValueChange = { targetValue = it })
        val intValue = targetValue * 55
        Text(text = "${intValue.toInt()}")
        Button(onClick = {
            scope.launch {
                progress.animateTo(
                    targetValue = intValue,
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = FastOutLinearInEasing,
                    )
                )
            }
        }) {
            Text(text = "Go")
        }
        Speedometer(progress.value.toInt())
    }
}

@Composable
fun Speedometer(
    progress: Int,
) {
    val arcDegrees = 275
    val startArcAngle = 135f
    val startStepAngle = -45
    val numberOfMarkers = 55
    val degreesMarkerStep = arcDegrees / numberOfMarkers

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        onDraw = {
            drawIntoCanvas { canvas ->
                val w = drawContext.size.width
                val h = drawContext.size.height
                val centerOffset = Offset(w / 2f, h / 2f)
                val quarterOffset = Offset(w / 4f, h / 4f)

                // Drawing Center Arc background
                val (mainColor, secondaryColor) = when {
                    progress < 20 -> // Red
                        Color(41, 169, 225, 255) to Color(0xFFE9E7E7)
                    progress < 40 -> // Orange
                        Color(41, 169, 225, 255) to Color(0xFFE9E9E9)
                    else -> // Green
                        Color(41, 169, 225, 255) to Color(0xFFF1F1F1)
                }
                val paint = Paint().apply {
                    color = mainColor
                }
                val centerArcSize = Size(w / 2f, h / 2f)
                val centerArcStroke = Stroke(50f, 0f, StrokeCap.Square)
                drawArc(
                    secondaryColor,
                    startArcAngle,
                    arcDegrees.toFloat(),
                    false,
                    topLeft = quarterOffset,
                    size = centerArcSize,
                    style = centerArcStroke
                )
                // Drawing Center Arc progress
                drawArc(
                    mainColor,
                    startArcAngle,
                    (degreesMarkerStep * progress).toFloat(),
                    false,
                    topLeft = quarterOffset,
                    size = centerArcSize,
                    style = centerArcStroke
                )
                // Drawing the pointer circle
//                drawCircle(mainColor, 80f, centerOffset)
                drawCircle(Color.White, 25f, centerOffset)
//                drawCircle(Color.Black, 30f, centerOffset)

                // Drawing Line Markers
                for ((counter, degrees) in (startStepAngle..(startStepAngle + arcDegrees) step degreesMarkerStep).withIndex()) {
                    val lineEndX = 80f
                    paint.color = mainColor
                    val lineStartX = if (counter % 5 == 0) {
                        paint.strokeWidth = 3f
                        0f
                    } else {
                        paint.strokeWidth = 1f
                        lineEndX * .2f
                    }
                    canvas.save()
                    canvas.rotate(degrees.toFloat(), w / 2f, h / 2f)
//                    canvas.drawLine(
//                        Offset(lineStartX, h / 2f),
//                        Offset(lineEndX, h / 2f),
//                        paint
//                    )
                    // Drawing Pointer
                    if (counter == progress) {
                        paint.color =  Color(41, 169, 225, 255)
                        canvas.drawPath(
                            Path().apply {
                                moveTo(w / 2, (h / 2) - 5)
                                moveTo((w / 2) + 45, (h / 2))
                                lineTo(w / 2, (h / 2) + 15)
                                lineTo(w / 2.5f, (h / 2))
                                lineTo(w / 2, (h / 2) - 15)
                                lineTo((w / 2) + 45, (h / 2))
                                close()
                            },
                            paint
                        )
                    }
                    canvas.restore()
                }

                drawCircle(Color.Black, 20f, centerOffset)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        SpeedometerScreen()
    }
}