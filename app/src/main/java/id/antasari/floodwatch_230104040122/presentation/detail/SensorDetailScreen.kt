package id.antasari.floodwatch_230104040122.presentation.detail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

// --- PALET WARNA (Konsisten dengan halaman sebelumnya) ---
val BgGrayApp = Color(0xFFF4F6F8)
val TextBlack = Color(0xFF1A1C1E)
val BlueStart = Color(0xFF1E88E5)
val BlueEnd = Color(0xFF1565C0)
val BluePrimary = Color(0xFF1565C0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorDetailScreen(
    navController: NavController,
    sensorId: String,
    sensorName: String,
    viewModel: DetailViewModel = hiltViewModel()
) {
    LaunchedEffect(sensorId) {
        viewModel.loadComments(sensorId)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> viewModel.selectedImageUri = uri }

    Scaffold(
        containerColor = BgGrayApp, // Latar belakang abu terang
        topBar = {
            // --- CUSTOM HEADER GRADIENT BIRU ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp) // Sedikit lebih pendek dari halaman Home
                    .background(
                        brush = Brush.verticalGradient(colors = listOf(BlueStart, BlueEnd)),
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
            ) {
                // Hiasan Bubbles Tipis
                Box(modifier = Modifier.offset(x = 280.dp, y = (-20).dp).size(150.dp).background(Color.White.copy(alpha = 0.1f), CircleShape))
                Box(modifier = Modifier.offset(x = (-30).dp, y = 50.dp).size(80.dp).background(Color.White.copy(alpha = 0.05f), CircleShape))

                // Isi Header (Tombol Back & Judul)
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp, start = 16.dp, end = 16.dp), // Padding atas untuk status bar
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(text = sensorName, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                        Text(text = "Laporan & Diskusi Warga", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        },
        bottomBar = {
            // --- BAGIAN INPUT BAWAH (Putih Bersih) ---
            Column(
                Modifier
                    .background(Color.White)
                    .padding(12.dp) // Padding luar agar tidak mepet
            ) {
                // Preview foto kecil
                if (viewModel.selectedImageUri != null) {
                    Box(Modifier.padding(bottom = 8.dp)) {
                        AsyncImage(
                            model = viewModel.selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Baris Input & Tombol
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(BgGrayApp, RoundedCornerShape(24.dp)) // Background abu di area ketik
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Foto", tint = BluePrimary)
                    }

                    // Textfield transparan agar menyatu dengan background abu
                    OutlinedTextField(
                        value = viewModel.commentText,
                        onValueChange = { viewModel.commentText = it },
                        placeholder = { Text("Tulis laporan situasi...", color = Color.Gray, fontSize = 14.sp) },
                        modifier = Modifier.weight(1f),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextBlack
                        )
                    )

                    // Tombol Kirim
                    IconButton(
                        onClick = { viewModel.sendComment(sensorId) },
                        enabled = !viewModel.isLoading
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = BluePrimary)
                        } else {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = "Kirim",
                                tint = if (viewModel.commentText.isNotEmpty() || viewModel.selectedImageUri != null) BluePrimary else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // --- DAFTAR KOMENTAR (TENGAH) ---
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Jarak antar kartu
        ) {
            items(viewModel.commentsList) { comment ->
                CommentItem(comment)
            }
        }
    }
}

// --- KARTU KOMENTAR YANG BARU (Putih Bersih) ---
@Composable
fun CommentItem(comment: Comment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), // KARTU PUTIH
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Ada bayangan sedikit
    ) {
        Column(Modifier.padding(16.dp)) {
            // Header Nama
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BluePrimary.copy(alpha = 0.1f)), // Avatar biru muda
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = comment.userName.take(1).uppercase(), color = BluePrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = comment.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextBlack)
                    // (Opsional: Tambah waktu disini nanti)
                    Text(text = "Warga", fontSize = 11.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Isi Komentar Teks (Warna Hitam agar terbaca)
            if (comment.text.isNotEmpty()) {
                Text(text = comment.text, fontSize = 15.sp, color = TextBlack, lineHeight = 22.sp)
            }

            // Isi Komentar Foto (Jika ada)
            if (comment.photoUrl.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = comment.photoUrl,
                    contentDescription = "Foto Laporan",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}