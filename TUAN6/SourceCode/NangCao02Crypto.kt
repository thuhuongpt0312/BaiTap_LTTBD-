package com.example.tuan_6

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// =====================================================================
// NÂNG CAO 02: Coroutines + Retrofit, dữ liệu giá crypto gần thời gian thực
//  - Retrofit: gọi HTTP GET lấy JSON
//  - Coroutines: suspend (gọi API không đứng màn hình), async/await (3 API chạy SONG SONG),
//                delay trong vòng lặp (tự làm mới 30 giây một lần)
// 3 API công khai, không cần khóa:
//  1. CoinGecko    : giá, % thay đổi 24h, đường giá 7 ngày
//  2. Binance      : giá BTC/USDT trên sàn
//  3. CryptoCompare: tin tức mới nhất
// =====================================================================

// ---------- Mô hình dữ liệu ----------
data class Sparkline(val price: List<Double>? = null)

data class Coin(
    val id: String = "",
    val symbol: String = "",
    val name: String = "",
    @SerializedName("current_price") val giaHienTai: Double = 0.0,
    @SerializedName("price_change_percentage_24h") val thayDoi24h: Double? = null,
    @SerializedName("sparkline_in_7d") val sparkline: Sparkline? = null
)

// Binance trả số dưới dạng chuỗi
data class Ticker24h(
    val symbol: String = "",
    val lastPrice: String = "0",
    val priceChangePercent: String = "0"
)

data class TinTuc(val title: String = "", val source: String = "")

data class PhanHoiTinTuc(@SerializedName("Data") val data: List<TinTuc>? = null)

// ---------- Các API ----------
interface CoinGeckoApi {
    @GET("api/v3/coins/markets")
    suspend fun layGia(
        @Query("vs_currency") tienTe: String,
        @Query("ids") ids: String,
        @Query("sparkline") sparkline: Boolean,
        @Query("price_change_percentage") phanTram: String
    ): List<Coin>
}

interface BinanceApi {
    @GET("api/v3/ticker/24hr")
    suspend fun ticker(@Query("symbol") symbol: String): Ticker24h
}

interface TinTucApi {
    @GET("data/v2/news/")
    suspend fun tinMoi(@Query("lang") ngonNgu: String): PhanHoiTinTuc
}

object DichVuCrypto {
    private fun taoRetrofit(goc: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(goc)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val coinGecko: CoinGeckoApi = taoRetrofit("https://api.coingecko.com/").create(CoinGeckoApi::class.java)
    val binance: BinanceApi = taoRetrofit("https://api.binance.com/").create(BinanceApi::class.java)
    val tinTuc: TinTucApi = taoRetrofit("https://min-api.cryptocompare.com/").create(TinTucApi::class.java)
}

// ---------- ViewModel ----------
class CryptoViewModel : ViewModel() {
    var danhSachCoin by mutableStateOf<List<Coin>>(emptyList())
        private set
    var tinNoi by mutableStateOf<TinTuc?>(null)
        private set
    var giaBinance by mutableStateOf("")
        private set
    var capNhatLuc by mutableStateOf("")
        private set
    var thoiGianTai by mutableStateOf(0L)
        private set
    var loi by mutableStateOf("")
        private set
    var dangTai by mutableStateOf(false)
        private set

    // Hàm suspend: gọi cả 3 API cùng lúc bằng async, rồi đợi (await) lấy kết quả.
    // Chạy song song nên tổng thời gian ≈ thời gian của API chậm nhất,
    // không phải cộng dồn cả 3 như khi gọi nối tiếp.
    suspend fun taiDuLieu() {
        dangTai = true
        val batDau = System.currentTimeMillis()

        coroutineScope {
            val ganGia = async {
                runCatching {
                    DichVuCrypto.coinGecko.layGia("usd", "ethereum,bitcoin,litecoin,monero", true, "24h")
                }.getOrNull()
            }
            val ganBinance = async {
                runCatching { DichVuCrypto.binance.ticker("BTCUSDT") }.getOrNull()
            }
            val ganTin = async {
                runCatching { DichVuCrypto.tinTuc.tinMoi("EN").data }.getOrNull()
            }

            val gia = ganGia.await()
            val binance = ganBinance.await()
            val tin = ganTin.await()

            if (gia != null) {
                danhSachCoin = gia
                loi = ""
            } else {
                loi = "Không tải được giá (CoinGecko). Kiểm tra mạng hoặc thử lại sau ít phút."
            }
            if (binance != null) {
                val g = binance.lastPrice.toDoubleOrNull() ?: 0.0
                val p = binance.priceChangePercent.toDoubleOrNull() ?: 0.0
                giaBinance = dinhDangGia(g) + " (" + dinhDangPhanTram(p) + ")"
            }
            if (tin != null && tin.isNotEmpty()) {
                tinNoi = tin[0]
            }
        }

        thoiGianTai = System.currentTimeMillis() - batDau
        capNhatLuc = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        dangTai = false
    }
}

// ---------- Định dạng ----------
fun dinhDangGia(gia: Double): String {
    return String.format(Locale.US, "\$%,.2f", gia)
}

fun dinhDangPhanTram(p: Double): String {
    return String.format(Locale.US, "%+.2f%%", p)
}

val mauNenTien = Color(0xFF14161F)
val mauTheTien = Color(0xFF222533)
val mauTang = Color(0xFF4CAF50)
val mauGiam = Color(0xFFE53935)

// Vẽ đường giá (sparkline) bằng Canvas
@Composable
fun DuongGia(gia: List<Double>, mau: Color, modifier: Modifier) {
    Canvas(modifier = modifier) {
        if (gia.size > 1) {
            val thap = gia.min()
            val cao = gia.max()
            var chenh = cao - thap
            if (chenh == 0.0) {
                chenh = 1.0
            }
            val duong = Path()
            for (i in gia.indices) {
                val x = size.width * i / (gia.size - 1)
                val y = size.height - ((gia[i] - thap) / chenh * size.height).toFloat()
                if (i == 0) {
                    duong.moveTo(x, y)
                } else {
                    duong.lineTo(x, y)
                }
            }
            drawPath(duong, mau, style = Stroke(width = 3f))
        }
    }
}

@Composable
fun TheYeuThich(coin: Coin, modifier: Modifier) {
    val p = coin.thayDoi24h ?: 0.0
    var mau = mauTang
    if (p < 0) {
        mau = mauGiam
    }
    Column(
        modifier = modifier
            .background(mauTheTien, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Text(coin.symbol.uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(dinhDangGia(coin.giaHienTai), color = Color.White, fontSize = 15.sp)
            Text(dinhDangPhanTram(p), color = mau, fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        DuongGia(
            gia = coin.sparkline?.price ?: emptyList(),
            mau = Color(0xFF4D7CFE),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
    }
}

// ---------- Màn hình ----------
@Composable
fun NangCao02Crypto(vm: CryptoViewModel = viewModel()) {
    // Tự làm mới 30 giây một lần. LaunchedEffect là một coroutine, tự hủy khi rời màn hình.
    LaunchedEffect(Unit) {
        while (true) {
            vm.taiDuLieu()
            delay(30000)
        }
    }

    // Sắp xếp theo thứ tự ETH, BTC, LTC, XMR như hình mẫu
    val thuTu = listOf("ethereum", "bitcoin", "litecoin", "monero")
    val dsHienThi = mutableListOf<Coin>()
    for (id in thuTu) {
        val coin = vm.danhSachCoin.find { it.id == id }
        if (coin != null) {
            dsHienThi.add(coin)
        }
    }
    val eth = vm.danhSachCoin.find { it.id == "ethereum" }
    val btc = vm.danhSachCoin.find { it.id == "bitcoin" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(mauNenTien)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Home",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text("New", color = Color.White, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))

            // Tin mới nhất (CryptoCompare)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF1B2A5C), Color(0xFF0E1530))),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    "LIVE",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(mauGiam, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "BREAKING NEWS",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFC928))
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                var tieuDeTin = "Đang tải tin tức..."
                var nguonTin = ""
                val tin = vm.tinNoi
                if (tin != null) {
                    tieuDeTin = tin.title
                    nguonTin = tin.source
                }
                Text(tieuDeTin, color = Color.White, fontSize = 13.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                if (nguonTin != "") {
                    Text(nguonTin, color = Color.Gray, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Favorites", color = Color.White, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (eth != null) {
                    TheYeuThich(eth, Modifier.weight(1f))
                }
                if (btc != null) {
                    TheYeuThich(btc, Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("All Fluctuations", color = Color.White, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))

            if (vm.loi != "") {
                Text(vm.loi, color = mauGiam, fontSize = 12.sp)
            }
            if (vm.dangTai && vm.danhSachCoin.isEmpty()) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        // Danh sách biến động giá
        items(dsHienThi.size) { i ->
            val coin = dsHienThi[i]
            val p = coin.thayDoi24h ?: 0.0
            var mau = mauTang
            if (p < 0) {
                mau = mauGiam
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .background(mauTheTien, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF2C3045), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(coin.symbol.uppercase().take(1), color = Color(0xFF4D7CFE), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(coin.symbol.uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                    Text(coin.name, color = Color.Gray, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(dinhDangGia(coin.giaHienTai), color = Color.White, fontWeight = FontWeight.Bold)
                    Text(dinhDangPhanTram(p), color = mau, fontSize = 12.sp)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            if (vm.giaBinance != "") {
                Text("Binance BTC/USDT: " + vm.giaBinance, color = Color.LightGray, fontSize = 12.sp)
            }
            if (vm.capNhatLuc != "") {
                Text(
                    "Cập nhật lúc " + vm.capNhatLuc + " · tải xong sau " + vm.thoiGianTai + " ms (3 API chạy song song)",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
