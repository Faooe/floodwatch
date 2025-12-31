package id.antasari.floodwatch_230104040122.presentation.profile

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import id.antasari.floodwatch_230104040122.presentation.home.BottomNavigationBar

// --- CONFIG WARNA PREMIUM ---
val BlueStart = Color(0xFF1E88E5)
val BlueEnd = Color(0xFF1565C0)
val BgGrayApp = Color(0xFFF8F9FA)
val LogoutBg = Color(0xFFFFEBEE)
val LogoutBorder = Color(0xFFFFCDD2)
val LogoutText = Color(0xFFC62828)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        initCloudinary(context, "dlb8dv4ei")
    }

    var currentUser by remember { mutableStateOf(auth.currentUser) }
    var photoUrl by remember { mutableStateOf(currentUser?.photoUrl) }
    var isLoadingUpload by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedImageUri ->
            isLoadingUpload = true
            MediaManager.get().upload(selectedImageUri)
                .unsigned("floodwatch_preset")
                .option("folder", "profile_images")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                        val downloadUrl = resultData?.get("secure_url").toString()
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(downloadUrl))
                            .build()
                        currentUser?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { task ->
                                isLoadingUpload = false
                                if (task.isSuccessful) {
                                    currentUser?.reload()
                                    photoUrl = Uri.parse(downloadUrl)
                                    Toast.makeText(context, "Foto berhasil diubah!", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        isLoadingUpload = false
                        Toast.makeText(context, "Gagal Upload: ${error?.description}", Toast.LENGTH_SHORT).show()
                    }
                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                })
                .dispatch()
        }
    }

    LaunchedEffect(Unit) {
        auth.currentUser?.reload()?.addOnCompleteListener {
            currentUser = auth.currentUser
            photoUrl = currentUser?.photoUrl
        }
    }

    val stats by viewModel.statsState.collectAsState()
    val realName = if (currentUser?.displayName.isNullOrEmpty()) "Pengguna" else currentUser?.displayName!!
    val realEmail = currentUser?.email ?: "user@email.com"
    val initial = realName.firstOrNull()?.toString()?.uppercase() ?: "U"

    Scaffold(
        containerColor = BgGrayApp,
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        // 1. COLUMN INDUK (TIDAK ADA SCROLL DI SINI)
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // --- BAGIAN HEADER (FIXED / DIAM) ---
            Box(modifier = Modifier.fillMaxWidth().height(380.dp)) {
                // Background Gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 40.dp)
                        .clip(RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp))
                        .background(brush = Brush.verticalGradient(colors = listOf(BlueStart, BlueEnd)))
                ) {
                    Box(modifier = Modifier.align(Alignment.TopEnd).offset(40.dp, (-30).dp).size(200.dp).background(Color.White.copy(0.08f), CircleShape))
                    Box(modifier = Modifier.align(Alignment.CenterStart).offset((-50).dp, 20.dp).size(150.dp).background(Color.White.copy(0.08f), CircleShape))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Profil Pengguna", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(28.dp))

                    // Avatar Logic
                    Box(contentAlignment = Alignment.Center) {
                        Card(
                            shape = CircleShape,
                            elevation = CardDefaults.cardElevation(8.dp),
                            modifier = Modifier.size(110.dp).clickable { launcher.launch("image/*") }
                        ) {
                            if (isLoadingUpload) {
                                Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(32.dp), color = BlueEnd)
                                }
                            } else if (photoUrl != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context).data(photoUrl).crossfade(true).build(),
                                    contentDescription = "Profile Picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE3F2FD)), contentAlignment = Alignment.Center) {
                                    Text(text = initial, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = BlueEnd)
                                }
                            }
                        }
                        Box(
                            modifier = Modifier.align(Alignment.BottomEnd).offset(4.dp, 4.dp).size(36.dp)
                                .clip(CircleShape).background(Color.White).border(2.dp, BlueEnd, CircleShape)
                                .clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Edit, "Edit", tint = BlueEnd, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(realName, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(realEmail, color = Color.White.copy(0.9f), fontSize = 14.sp)
                }

                // Kartu Statistik (Tetap menempel di header)
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter).padding(horizontal = 24.dp).height(85.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(6.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(count = stats.totalLaporan, label = "Laporan")
                        Divider(modifier = Modifier.height(30.dp).width(1.dp), color = Color.LightGray.copy(0.5f))
                        StatItem(count = stats.totalWilayah, label = "Lokasi")
                        Divider(modifier = Modifier.height(30.dp).width(1.dp), color = Color.LightGray.copy(0.5f))
                        StatItem(count = stats.statusAkun, label = "Status", isText = true)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. BAGIAN MENU (SCROLL DI SINI)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Mengisi sisa ruang ke bawah
                    .verticalScroll(rememberScrollState()) // Scroll hanya berlaku di area ini
                    .padding(horizontal = 24.dp) // Padding dipindah ke sini
            ) {
                Text("PENGATURAN", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(bottom = 12.dp, start = 4.dp))
                ProfileMenuItem(Icons.Default.Settings, "Edit Profil", onClick = { navController.navigate("account_settings") })
                Spacer(modifier = Modifier.height(12.dp))
                ProfileMenuItem(Icons.Default.Info, "Tentang Aplikasi", onClick = { navController.navigate("about_app") })
                Spacer(modifier = Modifier.height(12.dp))
                ProfileMenuItem(Icons.Default.Face, "Bantuan", onClick = { navController.navigate("help_support") })

                Spacer(modifier = Modifier.height(40.dp))

                // Tombol Logout (Ikut ke-scroll di dalam menu)
                Card(
                    onClick = {
                        auth.signOut()
                        Toast.makeText(context, "Sampai jumpa lagi!", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LogoutBg),
                    border = BorderStroke(1.dp, LogoutBorder),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(36.dp).background(Color.White.copy(0.6f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ExitToApp, null, tint = LogoutText, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Keluar Aplikasi", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LogoutText)
                            Text("Mengakhiri sesi anda saat ini", fontSize = 11.sp, color = LogoutText.copy(alpha = 0.7f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp)) // Jarak aman di bawah
            }
        }
    }
}

//HELPER UNTUK INIT CLOUDINARY
private fun initCloudinary(context: Context, cloudName: String) {
    try { MediaManager.get() } catch (e: Exception) {
        val config = HashMap<String, String>()
        config["dlb8dv4ei"] = cloudName
        MediaManager.init(context, config)
    }
}

@Composable
fun StatItem(count: String, label: String, isText: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (isText) Color(0xFF00C853) else BlueEnd)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().height(58.dp).clickable { onClick() }
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color(0xFF78909C), modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontSize = 15.sp, color = Color(0xFF263238), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ArrowForward, null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
        }
    }
}