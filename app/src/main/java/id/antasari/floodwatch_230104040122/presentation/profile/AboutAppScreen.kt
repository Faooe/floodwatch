package id.antasari.floodwatch_230104040122.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Kita gunakan warna dari ProfileScreen (Pastikan satu package)

@Composable
fun AboutAppScreen(navController: NavController) {
    Scaffold(
        containerColor = BgGrayApp // Ambil dari ProfileScreen
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

            // --- A. HEADER ESTETIK (BUBBLES) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp) // Header sedikit lebih tinggi untuk logo
            ) {
                // Gradient Background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                        .background(
                            brush = Brush.verticalGradient(colors = listOf(BlueStart, BlueEnd))
                        )
                ) {
                    // Hiasan Bubbles
                    Box(modifier = Modifier.align(Alignment.TopEnd).offset(20.dp, (-20).dp).size(180.dp).background(Color.White.copy(0.1f), CircleShape))
                    Box(modifier = Modifier.align(Alignment.CenterStart).offset((-40).dp, 10.dp).size(120.dp).background(Color.White.copy(0.1f), CircleShape))
                    Box(modifier = Modifier.align(Alignment.BottomEnd).offset((-30).dp, (-20).dp).size(60.dp).background(Color.White.copy(0.15f), CircleShape))
                }

                // Tombol Kembali & Judul
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.background(Color.White.copy(0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Tentang Aplikasi",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // --- B. KONTEN (CARD) ---
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(top = 100.dp) // Agar menumpuk header
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Logo / Icon Besar
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(BlueStart.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = BlueEnd,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Nama & Versi
                        Text("FloodWatch", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BlueEnd)
                        Text("Versi 1.0.0 (Beta)", fontSize = 14.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(24.dp))
                        Divider(color = Color.LightGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(24.dp))

                        // Deskripsi
                        Text(
                            text = "FloodWatch adalah aplikasi pemantauan ketinggian air sungai secara realtime untuk wilayah Banjarmasin dan sekitarnya. Aplikasi ini bertujuan membantu warga mengantisipasi banjir lebih dini.",
                            fontSize = 14.sp,
                            color = Color(0xFF2D3436),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Fitur Utama
                        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                            Text("Fitur Utama:", fontWeight = FontWeight.Bold, color = BlueEnd, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            FeatureItem("Monitoring Sensor Realtime")
                            FeatureItem("Peta Sebaran Lokasi")
                            FeatureItem("Notifikasi Status Bahaya")
                            FeatureItem("Pelaporan Warga")
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Copyright Footer
                        Text("Developed by Husin Nafarin Ramadhani", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text("© 2024 All Rights Reserved", fontSize = 11.sp, color = Color.LightGray)
                    }
                }
            }
        }
    }
}

// Komponen Item Fitur
@Composable
fun FeatureItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00C853), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 13.sp, color = Color(0xFF2D3436))
    }
}