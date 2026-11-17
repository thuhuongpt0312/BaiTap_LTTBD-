package com.example.fibonacci

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlin.math.atan2
import kotlin.math.min

// ---------------------------------------------------------------
// 1. LOGIC: Fibonacci + hình học xoắn ốc
// ---------------------------------------------------------------

/** Sinh [count] số Fibonacci đầu tiên: 1, 1, 2, 3, 5, 8, ... */
fun fibonacci(count: Int): List<Long> {
    val list = mutableListOf(1L, 1L)
    while (list.size < count) list.add(list[list.size - 1] + list[list.size - 2])
    return list.take(count)
}

/** Một ô vuông của xoắn ốc + tâm cung tròn nằm trong ô đó. */
data class Piece(
    val l: Float, val t: Float, val r: Float, val b: Float,
    val cx: Float, val cy: Float
) {
    val side get() = r - l
}

/**
 * Xếp các ô vuông cạnh Fibonacci quanh nhau theo thứ tự: phải, lên, trái, xuống, ...
 * Mỗi ô có một cung 1/4 đường tròn; các cung nối liền nhau thành xoắn ốc.
 */
fun buildPieces(fibs: List<Long>): List<Piece> {
    // Bước 1: đặt các ô vuông
    data class Box(val l: Float, val t: Float, val r: Float, val b: Float)

    val boxes = mutableListOf(Box(0f, 0f, 1f, 1f))
    var bb = boxes[0] // bounding box của tất cả ô đã đặt
    for (i in 1 until fibs.size) {
        val s = fibs[i].toFloat()
        val box = when ((i - 1) % 4) {
            0 -> Box(bb.r, bb.t, bb.r + s, bb.t + s)      // sang phải
            1 -> Box(bb.l, bb.t - s, bb.l + s, bb.t)      // lên trên
            2 -> Box(bb.l - s, bb.b - s, bb.l, bb.b)      // sang trái
            else -> Box(bb.r - s, bb.b, bb.r, bb.b + s)   // xuống dưới
        }
        boxes.add(box)
        bb = Box(min(bb.l, box.l), min(bb.t, box.t), maxOf(bb.r, box.r), maxOf(bb.b, box.b))
    }

    // Bước 2: tìm tâm cung tròn = góc giao của cạnh chung với ô trước và ô sau
    return boxes.mapIndexed { i, bx ->
        if (i == 0) {
            Piece(bx.l, bx.t, bx.r, bx.b, cx = bx.r, cy = bx.t)
        } else {
            val d = (i - 1) % 4   // hướng đặt ô hiện tại
            val dn = i % 4        // hướng đặt ô kế tiếp
            val prevVal = when (d) { 0 -> bx.l; 1 -> bx.b; 2 -> bx.r; else -> bx.t }
            val nextVal = when (dn) { 0 -> bx.r; 1 -> bx.t; 2 -> bx.l; else -> bx.b }
            val cx = if (d % 2 == 0) prevVal else nextVal
            val cy = if (d % 2 == 0) nextVal else prevVal
            Piece(bx.l, bx.t, bx.r, bx.b, cx, cy)
        }
    }
}

// ---------------------------------------------------------------
// 2. UI: màn hình danh sách (LazyColumn) + màn hình xoắn ốc (Canvas)
// ---------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(onShowSpiral: (Int) -> Unit) {
    var n by remember { mutableIntStateOf(10) }
    val fibs = remember(n) { fibonacci(n) }

    Scaffold(topBar = { TopAppBar(title = { Text("Dãy Fibonacci & Tỉ lệ vàng") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Số phần tử N = $n", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = n.toFloat(),
                onValueChange = { n = it.toInt() },
                valueRange = 3f..20f,
                steps = 16
            )
            Text(
                "φ ≈ 1.618034  (tỉ số F(n)/F(n-1) tiến dần về φ)",
                style = MaterialTheme.typography.bodySmall
            )

            // LazyColumn: chỉ vẽ các dòng đang hiển thị trên màn hình
            LazyColumn(
                modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(fibs) { i, f ->
                    val ratio = if (i == 0) "—" else "%.6f".format(f.toDouble() / fibs[i - 1])
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("F(${i + 1}) = $f", style = MaterialTheme.typography.titleMedium)
                            Text("tỉ số: $ratio")
                        }
                    }
                }
            }

            Button(onClick = { onShowSpiral(n) }, modifier = Modifier.fillMaxWidth()) {
                Text("Xem xoắn ốc Fibonacci")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpiralScreen(n: Int, onBack: () -> Unit) {
    val fibs = remember(n) { fibonacci(n) }
    val pieces = remember(n) { buildPieces(fibs) }
    val measurer = rememberTextMeasurer()
    val palette = listOf(
        Color(0xFFEF5350), Color(0xFFFFA726), Color(0xFFFFEE58),
        Color(0xFF66BB6A), Color(0xFF42A5F5), Color(0xFFAB47BC)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Xoắn ốc Fibonacci (N = $n)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Canvas(Modifier.padding(padding).fillMaxSize().padding(12.dp)) {
            // Tính tỉ lệ phóng to để vừa khít màn hình
            val minX = pieces.minOf { it.l }
            val minY = pieces.minOf { it.t }
            val w = pieces.maxOf { it.r } - minX
            val h = pieces.maxOf { it.b } - minY
            val sc = min(size.width / w, size.height / h)
            val offX = (size.width - w * sc) / 2f - minX * sc
            val offY = (size.height - h * sc) / 2f - minY * sc
            fun sx(x: Float) = x * sc + offX
            fun sy(y: Float) = y * sc + offY

            pieces.forEachIndexed { i, p ->
                val color = palette[i % palette.size]
                val topLeft = Offset(sx(p.l), sy(p.t))
                val sz = Size(p.side * sc, p.side * sc)

                // Ô vuông: nền nhạt + viền
                drawRect(color.copy(alpha = 0.35f), topLeft, sz)
                drawRect(color, topLeft, sz, style = Stroke(width = 2f))

                // Số Fibonacci ghi trong ô (chỉ ghi khi ô đủ lớn)
                if (p.side * sc > 28f) {
                    drawText(
                        measurer, "${fibs[i]}",
                        topLeft = Offset(topLeft.x + 6f, topLeft.y + 4f),
                        style = TextStyle(fontSize = 14.sp, color = Color.DarkGray)
                    )
                }

                // Cung 1/4 đường tròn: tâm (cx, cy), bán kính = cạnh ô
                val ox = if (p.cx == p.l) p.r else p.l
                val oy = if (p.cy == p.t) p.b else p.t
                val angA = Math.toDegrees(atan2(0f, ox - p.cx).toDouble()).toFloat()
                val angB = Math.toDegrees(atan2(oy - p.cy, 0f).toDouble()).toFloat()
                val sweep = ((angB - angA + 540f) % 360f) - 180f   // ±90°
                val rad = p.side * sc
                drawArc(
                    color = Color(0xFFD81B60),
                    startAngle = angA,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(sx(p.cx) - rad, sy(p.cy) - rad),
                    size = Size(rad * 2, rad * 2),
                    style = Stroke(width = 6f, cap = StrokeCap.Round)
                )
            }
        }
    }
}

// ---------------------------------------------------------------
// 3. NAVIGATION + Activity
// ---------------------------------------------------------------

@Composable
fun FibonacciApp() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "list") {
        composable("list") {
            ListScreen(onShowSpiral = { n -> nav.navigate("spiral/$n") })
        }
        composable(
            route = "spiral/{n}",
            arguments = listOf(navArgument("n") { type = NavType.IntType })
        ) { entry ->
            val n = entry.arguments?.getInt("n") ?: 8
            SpiralScreen(n = n, onBack = { nav.popBackStack() })
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme { FibonacciApp() }
        }
    }
}