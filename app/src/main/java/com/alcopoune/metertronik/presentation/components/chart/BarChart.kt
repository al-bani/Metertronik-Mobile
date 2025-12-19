import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alcopoune.metertronik.presentation.components.PrimaryCard

data class BarData(
    val label: String,
    val value1: Float, // Untuk batang merah
    val value2: Float  // Untuk batang biru
)

@Composable
fun DoubleBarChart(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(220.dp)
) {
    val data = listOf(
        BarData("Sen", 45f, 30f),
        BarData("Sel", 52f, 40f),
        BarData("Rab", 38f, 55f),
        BarData("Kam", 65f, 42f),
        BarData("Jum", 48f, 60f),
        BarData("Sab", 72f, 50f),
        BarData("Min", 60f, 45f),
        BarData("Sen", 45f, 30f),
        BarData("Sel", 52f, 40f),
        BarData("Rab", 38f, 55f),
        BarData("Kam", 65f, 42f),
        BarData("Jum", 48f, 60f),
        BarData("Sab", 72f, 50f),
        BarData("Min", 60f, 45f)
    )
    val maxVal = (data.maxOfOrNull { maxOf(it.value1, it.value2) } ?: 100f) * 1.2f

    // Scroll state horizontal
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(vertical = 16.dp)
    ) {
        Canvas(
            modifier = Modifier
                .height(250.dp)
                .width((data.size * 60).dp) // Set lebar canvas berdasarkan jumlah data
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
                    color = Color.Red,
                    topLeft = Offset(
                        x = xCenter - barWidth - spacing / 2,
                        y = canvasHeight - bar1Height
                    ),
                    size = Size(barWidth, bar1Height)
                )

                drawRect(
                    color = Color.Blue,
                    topLeft = Offset(
                        x = xCenter + spacing / 2,
                        y = canvasHeight - bar2Height
                    ),
                    size = Size(barWidth, bar2Height)
                )

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
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
    }
}
