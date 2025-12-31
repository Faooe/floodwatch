package id.antasari.floodwatch_230104040122.presentation.profile

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HelpSupportScreen(navController: NavController) {
    Scaffold(
        containerColor = BgGrayApp // Ambil dari ProfileScreen / Shared
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

            // --- A. HEADER ESTETIK (BUBBLES) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
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
                    // Bubbles
                    Box(modifier = Modifier.align(Alignment.TopEnd).offset(20.dp, (-20).dp).size(180.dp).background(Color.White.copy(0.1f), CircleShape))
                    Box(modifier = Modifier.align(Alignment.CenterStart).offset((-40).dp, 10.dp).size(120.dp).background(Color.White.copy(0.1f), CircleShape))
                }

                // Header Content
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
                        text = "Bantuan & Dukungan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // --- B. KONTEN (FAQ LIST) ---
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(top = 100.dp) // Supaya menumpuk header
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Judul Section 1
                Text("Pertanyaan Umum (FAQ)", fontWeight = FontWeight.Bold, color = BgGrayApp, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))

                // --- LIST PERTANYAAN ---
                FAQItem(
                    question = "Bagaimana cara melaporkan banjir?",
                    answer = "Anda dapat menekan tombol '+' (Tambah) di halaman Home, lalu isi detail lokasi dan foto kejadian, kemudian tekan Kirim."
                )
                Spacer(modifier = Modifier.height(12.dp))

                FAQItem(
                    question = "Apakah data ketinggian air realtime?",
                    answer = "Ya, data sensor diperbarui setiap 1 menit sekali dan terhubung langsung dengan server pusat FloodWatch."
                )
                Spacer(modifier = Modifier.height(12.dp))

                FAQItem(
                    question = "Bagaimana cara mengubah profil?",
                    answer = "Masuk ke menu Profil, pilih 'Pengaturan Akun', lalu ubah data yang diinginkan dan tekan Simpan."
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Judul Section 2
                Text("Hubungi Kami", fontWeight = FontWeight.Bold, color = BlueEnd, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))

                // Kartu Kontak
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ContactItem(Icons.Default.Email, "Email Support", "support@banjarmasin.go.id")
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(0.3f))
                        ContactItem(Icons.Default.Call, "Call Center", "112 (Bebas Pulsa)")
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// --- KOMPONEN ITEM FAQ (Expandable) ---
@Composable
fun FAQItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize( // Animasi halus saat buka/tutup
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .clickable { expanded = !expanded } // Klik untuk buka/tutup
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = question,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF2D3436),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color.LightGray.copy(0.2f))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = answer,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// --- KOMPONEN KONTAK ---
@Composable
fun ContactItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(40.dp).background(BlueStart.copy(0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = BlueEnd, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3436))
        }
    }
}