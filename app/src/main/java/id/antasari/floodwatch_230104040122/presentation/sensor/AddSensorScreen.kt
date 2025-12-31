package id.antasari.floodwatch_230104040122.presentation.sensor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import id.antasari.floodwatch_230104040122.ui.theme.WaterBluePrimary
// IMPORT OSMDROID
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

// Warna Tambahan
val BgLight = Color(0xFFF5F7FA)
val SurfaceWhite = Color.White
val TextSecondary = Color(0xFF636E72)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSensorScreen(
    navController: NavController,
    viewModel: AddSensorViewModel = hiltViewModel()
) {
    // State Form
    var sensorId by remember { mutableStateOf("SN-${(1000..9999).random()}") }
    var locationName by remember { mutableStateOf("") }
    var waterLevel by remember { mutableStateOf("") }

    // State Peta (Default Banjarmasin)
    var currentLat by remember { mutableDoubleStateOf(-3.3194) }
    var currentLng by remember { mutableDoubleStateOf(114.5928) }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            // Header Gradient Biru
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(WaterBluePrimary, Color(0xFF1565C0))
                        ),
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Input Sensor IoT",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {

            // --- 1. BAGIAN PETA (PIN LOCATION) ---
            Text("Titik Lokasi Sensor", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = WaterBluePrimary)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth().height(320.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // WIDGET PETA
                    AndroidView(
                        factory = { context ->
                            Configuration.getInstance().userAgentValue = context.packageName
                            MapView(context).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setMultiTouchControls(true)
                                controller.setZoom(15.0)
                                controller.setCenter(GeoPoint(currentLat, currentLng))

                                addMapListener(object : MapListener {
                                    override fun onScroll(event: ScrollEvent?): Boolean {
                                        val center = mapCenter
                                        currentLat = center.latitude
                                        currentLng = center.longitude
                                        return true
                                    }
                                    override fun onZoom(event: ZoomEvent?): Boolean = true
                                })
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // PIN MERAH DI TENGAH
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Pin",
                        tint = Color.Red,
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center)
                            .offset(y = (-24).dp)
                    )

                    // Tulisan Petunjuk
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Geser peta untuk menentukan titik", fontSize = 12.sp, color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // INFO KOORDINAT
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Koordinat GPS", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = String.format("%.4f, %.4f", currentLat, currentLng),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = WaterBluePrimary
                        )
                    }
                    Button(
                        onClick = {
                            currentLat = -3.3194
                            currentLng = 114.5928
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WaterBluePrimary.copy(alpha = 0.1f)),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text("Reset", color = WaterBluePrimary, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 2. INPUT DATA ---
            Text("Detail Data", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = WaterBluePrimary)
            Spacer(modifier = Modifier.height(8.dp))

            // SENSOR ID (Read Only / Auto)
            OutlinedTextField(
                value = sensorId,
                onValueChange = { },
                label = { Text("Sensor ID") },
                readOnly = true,
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = WaterBluePrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WaterBluePrimary,
                    unfocusedBorderColor = Color.LightGray,
                    // --- PERBAIKAN WARNA ---
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black // Penting karena readOnly
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // INPUT LOKASI
            OutlinedTextField(
                value = locationName,
                onValueChange = { locationName = it },
                label = { Text("Nama Lokasi") },
                placeholder = { Text("Contoh: Siring Sungai") },
                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = WaterBluePrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WaterBluePrimary,
                    unfocusedBorderColor = Color.LightGray,
                    // --- PERBAIKAN WARNA ---
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // INPUT TINGGI AIR
            OutlinedTextField(
                value = waterLevel,
                onValueChange = { if (it.all { char -> char.isDigit() }) waterLevel = it },
                label = { Text("Tinggi Air (cm)") },
                suffix = { Text("cm") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = WaterBluePrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WaterBluePrimary,
                    unfocusedBorderColor = Color.LightGray,
                    // --- PERBAIKAN WARNA ---
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- 3. TOMBOL SIMPAN ---
            Button(
                onClick = {
                    if (locationName.isNotEmpty() && waterLevel.isNotEmpty()) {
                        val finalName = "$locationName ($sensorId)"
                        viewModel.submitReport(finalName, waterLevel, currentLat, currentLng)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = WaterBluePrimary),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text("REGISTRASI SENSOR", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}