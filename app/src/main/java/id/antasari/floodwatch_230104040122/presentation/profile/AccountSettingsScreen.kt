package id.antasari.floodwatch_230104040122.presentation.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(navController: NavController) {

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    // 1. AMBIL DATA SAAT INI DARI FIREBASE
    // Kita gunakan remember agar bisa diedit di text field
    var tempName by remember { mutableStateOf(user?.displayName ?: "") }
    var tempEmail by remember { mutableStateOf(user?.email ?: "") }

    // (Opsional) Karena Firebase Auth standar tidak simpan no HP tanpa verifikasi SMS,
    // kita kosongkan dulu atau buat dummy lokal
    var tempPhone by remember { mutableStateOf("0812-xxxx-xxxx") }

    // State Loading saat menyimpan
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BgGrayApp
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

            // --- A. HEADER ESTETIK ---
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
                    // Hiasan Bubbles
                    Box(modifier = Modifier.align(Alignment.TopEnd).offset(20.dp, (-20).dp).size(180.dp).background(Color.White.copy(0.1f), CircleShape))
                    Box(modifier = Modifier.align(Alignment.CenterStart).offset((-40).dp, 10.dp).size(120.dp).background(Color.White.copy(0.1f), CircleShape))
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
                        text = "Pengaturan Akun",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // --- B. FORM EDIT ---
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(top = 100.dp)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Edit Informasi", color = BlueEnd, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(24.dp))

                        // Input Nama
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            label = { Text("Nama Lengkap") },
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = inputColorsv2()
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Input Email (READ ONLY)
                        // Mengganti email di Firebase butuh verifikasi ulang, jadi kita kunci dulu biar aman
                        OutlinedTextField(
                            value = tempEmail,
                            onValueChange = { }, // Tidak bisa diubah
                            readOnly = true,     // KUNCI
                            label = { Text("Email (Tidak dapat diubah)") },
                            leadingIcon = { Icon(Icons.Default.Email, null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF5F5F5), // Abu-abu
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                disabledTextColor = Color.Gray,
                                focusedBorderColor = Color.LightGray,
                                unfocusedBorderColor = Color.LightGray
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Input Telepon
                        OutlinedTextField(
                            value = tempPhone,
                            onValueChange = { tempPhone = it },
                            label = { Text("Nomor Telepon") },
                            leadingIcon = { Icon(Icons.Default.Phone, null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = inputColorsv2()
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // --- TOMBOL SIMPAN REAL ---
                        Button(
                            onClick = {
                                if (tempName.isNotEmpty()) {
                                    isLoading = true

                                    // 2. UPDATE PROFILE KE FIREBASE
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(tempName)
                                        .build()

                                    user?.updateProfile(profileUpdates)
                                        ?.addOnCompleteListener { task ->
                                            isLoading = false
                                            if (task.isSuccessful) {
                                                Toast.makeText(context, "Profil Diperbarui!", Toast.LENGTH_SHORT).show()
                                                navController.popBackStack() // Kembali ke Profil
                                            } else {
                                                Toast.makeText(context, "Gagal Update: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(context, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BlueEnd),
                            enabled = !isLoading // Disable tombol kalau lagi loading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Simpan Perubahan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper Colors biar kodingan di atas gak kepanjangan
@Composable
fun inputColorsv2() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    cursorColor = BlueEnd,
    focusedBorderColor = BlueEnd,
    unfocusedBorderColor = Color.LightGray,
    focusedLabelColor = BlueEnd,
    unfocusedLabelColor = Color.Gray,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White
)