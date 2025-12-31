package id.antasari.floodwatch_230104040122.presentation.notification

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import id.antasari.floodwatch_230104040122.domain.model.Comment
import id.antasari.floodwatch_230104040122.domain.model.Sensor
import id.antasari.floodwatch_230104040122.presentation.home.BottomNavigationBar
import id.antasari.floodwatch_230104040122.presentation.home.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

// --- CONFIG WARNA ---
private val BluePrimary = Color(0xFF1E88E5)
private val BlueDark = Color(0xFF1565C0)
private val BgClean = Color(0xFFF5F7FA) // Abu-abu bersih
private val WarningColor = Color(0xFFF57C00)
private val DangerColor = Color(0xFFD32F2F)

@Composable
fun NotificationScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    notificationViewModel: NotificationViewModel = hiltViewModel()
) {
    // State untuk menyimpan lokasi mana yang sedang dibuka chat-nya
    var selectedSensorName by remember { mutableStateOf<String?>(null) }

    // Tombol Back HP: Jika sedang chat, tutup chat. Jika tidak, keluar aplikasi/default.
    BackHandler(enabled = selectedSensorName != null) {
        selectedSensorName = null
    }

    Scaffold(
        containerColor = BgClean,
        bottomBar = {
            // Sembunyikan navigasi bawah saat chat terbuka supaya layar lebih luas
            if (selectedSensorName == null) {
                BottomNavigationBar(navController)
            }
        }
    ) { paddingValues ->

        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (selectedSensorName == null) {
                // --- TAMPILAN 1: LIST NOTIFIKASI ---
                NotificationListContent(
                    homeViewModel = homeViewModel,
                    onItemClick = { sensorName ->
                        // Set channel chat sesuai nama lokasi
                        notificationViewModel.setChannel(sensorName)
                        selectedSensorName = sensorName
                    }
                )
            } else {
                // --- TAMPILAN 2: CHAT ROOM ---
                ChatRoomContent(
                    sensorName = selectedSensorName!!,
                    viewModel = notificationViewModel,
                    onBack = { selectedSensorName = null }
                )
            }
        }
    }
}

// ==========================================
// BAGIAN 1: LIST NOTIFIKASI (Home Style)
// ==========================================
@Composable
fun NotificationListContent(
    homeViewModel: HomeViewModel,
    onItemClick: (String) -> Unit
) {
    val allSensors by homeViewModel.sensorState.collectAsState()

    // FILTER LOGIC: Hanya tampilkan BAHAYA atau WASPADA. (AMAN disembunyikan)
    val dangerSensors = remember(allSensors) {
        allSensors.filter {
            it.status == "BAHAYA" || it.status == "WASPADA"
        }.sortedByDescending { it.status == "BAHAYA" }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // --- HEADER BIRU + BUBBLE (MIRIP HOME) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(Brush.verticalGradient(colors = listOf(BluePrimary, BlueDark)))
        ) {
            // Hiasan Bubble Transparan
            Box(modifier = Modifier.align(Alignment.TopEnd).offset(30.dp, (-30).dp).size(140.dp).background(Color.White.copy(0.1f), CircleShape))
            Box(modifier = Modifier.align(Alignment.BottomStart).offset((-20).dp, 20.dp).size(100.dp).background(Color.White.copy(0.1f), CircleShape))

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Notifikasi", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Pantau lokasi rawan & kirim laporan", fontSize = 14.sp, color = Color.White.copy(0.9f))
            }
        }

        // --- LIST CONTENT ---
        if (dangerSensors.isEmpty()) {
            // Tampilan kosong jika semua aman
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(80.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Aman! Tidak ada peringatan banjir.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(dangerSensors) { sensor ->
                    NotificationItemCard(sensor, onClick = { onItemClick(sensor.name) })
                }
            }
        }
    }
}

@Composable
fun NotificationItemCard(sensor: Sensor, onClick: () -> Unit) {
    val isDanger = sensor.status == "BAHAYA"
    val statusColor = if (isDanger) DangerColor else WarningColor
    val bgIcon = if (isDanger) Color(0xFFFFEBEE) else Color(0xFFFFF3E0)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(50.dp).background(bgIcon, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Warning, null, tint = statusColor, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = sensor.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Text(text = "${sensor.status} • ${sensor.waterLevel} cm", color = statusColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Ketuk untuk lapor >", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

// ==========================================
// BAGIAN 2: CHAT ROOM UI
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomContent(
    sensorName: String,
    viewModel: NotificationViewModel,
    onBack: () -> Unit
) {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> viewModel.selectedImageUri = uri }

    Column(modifier = Modifier.fillMaxSize().background(BgClean)) {

        // --- HEADER CHAT BIRU + BUBBLE ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // Lebih compact dari header utama
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(Brush.verticalGradient(colors = listOf(BluePrimary, BlueDark)))
        ) {
            // Hiasan Bubble
            Box(modifier = Modifier.align(Alignment.TopEnd).offset(20.dp, (-20).dp).size(90.dp).background(Color.White.copy(0.1f), CircleShape))

            // Isi Header
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp), // Padding disesuaikan
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(verticalArrangement = Arrangement.Center) {
                    Text(text = sensorName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                    Text(text = "Forum Warga Sekitar", fontSize = 12.sp, color = Color.White.copy(0.85f))
                }
            }
        }

        // --- LIST CHAT ---
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            reverseLayout = true
        ) {
            items(viewModel.commentsList) { comment ->
                ChatBubble(comment)
            }
            if (viewModel.commentsList.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada laporan. Jadilah yang pertama!", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            }
        }

        // --- INPUT AREA (FIX KONTRAS HITAM) ---
        Surface(
            shadowElevation = 12.dp,
            color = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Preview Foto
                if (viewModel.selectedImageUri != null) {
                    Box(modifier = Modifier.padding(bottom = 12.dp)) {
                        AsyncImage(
                            model = viewModel.selectedImageUri, contentDescription = "Preview",
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { viewModel.selectedImageUri = null },
                            modifier = Modifier.size(24.dp).align(Alignment.TopEnd).offset(4.dp, (-4).dp).background(Color.Red, CircleShape)
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Tombol Tambah Foto
                    IconButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.background(Color(0xFFE3F2FD), CircleShape)
                    ) {
                        Icon(Icons.Default.Add, null, tint = BluePrimary)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // TextField (FIX WARNA TEXT HITAM DISINI)
                    OutlinedTextField(
                        value = viewModel.commentText,
                        onValueChange = { viewModel.commentText = it },
                        placeholder = { Text("Tulis laporan...", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,   // Text jadi HITAM saat ngetik
                            unfocusedTextColor = Color.Black, // Text jadi HITAM saat diam
                            focusedContainerColor = Color(0xFFFAFAFA),
                            unfocusedContainerColor = Color(0xFFFAFAFA),
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Tombol Kirim
                    IconButton(
                        onClick = { viewModel.sendReport() },
                        enabled = !viewModel.isLoading
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = BluePrimary)
                        } else {
                            Icon(Icons.Default.Send, null, tint = BluePrimary, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        }
    }
}

// --- ITEM BUBBLE CHAT (FIX ERROR) ---
@Composable
fun ChatBubble(comment: Comment) {
    val date = Date(comment.timestamp)
    val format = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())

    // FIX: Gunakan verticalAlignment = Alignment.Top (Bukan crossAxisAlignment)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar Bulat
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFBBDEFB), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if(comment.userName.isNotEmpty()) comment.userName.take(1).uppercase() else "?",
                fontWeight = FontWeight.Bold,
                color = BlueDark
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Bubble Isi Pesan
        Column(modifier = Modifier.weight(1f)) {
            // Header: Nama + Waktu
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if(comment.userName.isNotEmpty()) comment.userName else "Warga",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = format.format(date), fontSize = 10.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Body: Card Pesan
            Card(
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Tampilkan foto jika ada
                    if (comment.photoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = comment.photoUrl,
                            contentDescription = "Foto",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    // Tampilkan text jika ada
                    if (comment.text.isNotEmpty()) {
                        Text(text = comment.text, fontSize = 14.sp, color = Color(0xFF333333))
                    }
                }
            }
        }
    }
}