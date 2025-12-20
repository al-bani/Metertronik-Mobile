import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alcopoune.metertronik.domain.model.HourlyData
import com.alcopoune.metertronik.utils.DecimalFormater

data class BarData(
    val label: String,
    val value1: Float, // max
    val value2: Float  // min
)

@Composable
fun DoubleBarChart(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(250.dp),
    hourly: List<HourlyData>,
    maxColor: Color = MaterialTheme.colorScheme.error,
    minColor: Color = MaterialTheme.colorScheme.onSecondary
) {
    val data = hourly.map {
        BarData(
            label = it.ts.substring(11, 16),
            value1 = it.maxPower.toFloat(),
            value2 = it.minPower.toFloat()
        )
    }

    val maxVal = (data.maxOfOrNull { maxOf(it.value1, it.value2) } ?: 100f) * 1f
    val scrollState = rememberScrollState()

    var popupText by remember { mutableStateOf<String?>(null) }
    var popupOffset by remember { mutableStateOf(Offset.Zero) }
    var popupColor by remember { mutableStateOf(Color.Transparent) }

    Box(modifier = modifier) {


        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(vertical = 12.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .height(230.dp)
                    .width((data.size * 60).dp)
                    .pointerInput(Unit) {
                        detectTapGestures { tap ->
                            popupText = null

                            val canvasWidth = size.width
                            val canvasHeight = size.height
                            val barGroupWidth = canvasWidth / data.size
                            val barWidth = barGroupWidth * 0.35f
                            val spacing = barGroupWidth * 0.05f

                            data.forEachIndexed { index, item ->
                                val xCenter = index * barGroupWidth + barGroupWidth / 2

                                val bar1Height = (item.value1 / maxVal) * canvasHeight
                                val bar2Height = (item.value2 / maxVal) * canvasHeight

                                val maxRect = Rect(
                                    Offset(
                                        xCenter - barWidth - spacing / 2,
                                        canvasHeight - bar1Height
                                    ),
                                    Size(barWidth, bar1Height)
                                )

                                val minRect = Rect(
                                    Offset(
                                        xCenter + spacing / 2,
                                        canvasHeight - bar2Height
                                    ),
                                    Size(barWidth, bar2Height)
                                )

                                when {
                                    maxRect.contains(tap) -> {
                                        popupText = "${item.value1}"
                                        popupOffset = tap
                                        popupColor = maxColor
                                    }
                                    minRect.contains(tap) -> {
                                        popupText = "${item.value2}"
                                        popupOffset = tap
                                        popupColor = minColor
                                    }
                                }
                            }
                        }
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val barGroupWidth = canvasWidth / data.size
                val barWidth = barGroupWidth * 0.35f
                val spacing = barGroupWidth * 0.05f

                data.forEachIndexed { index, item ->
                    val xCenter = index * barGroupWidth + barGroupWidth / 2

                    val bar1Height = (item.value1 / maxVal) * canvasHeight
                    val bar2Height = (item.value2 / maxVal) * canvasHeight

                    drawRect(
                        color = maxColor,
                        topLeft = Offset(
                            xCenter - barWidth - spacing / 2,
                            canvasHeight - bar1Height
                        ),
                        size = Size(barWidth, bar1Height)
                    )

                    drawRect(
                        color = minColor,
                        topLeft = Offset(
                            xCenter + spacing / 2,
                            canvasHeight - bar2Height
                        ),
                        size = Size(barWidth, bar2Height)
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        item.label,
                        xCenter,
                        canvasHeight + 40f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 30f
                        }
                    )
                }
            }
        }

        popupText?.let {
            Box(
                modifier = Modifier
                    .offset { IntOffset(popupOffset.x.toInt(), popupOffset.y.toInt() - 60) }
                    .background(
                        color = popupColor,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${DecimalFormater(it.toFloat())} W",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.background
                )
            }
        }
    }
}

