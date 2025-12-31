package id.antasari.floodwatch_230104040122.presentation.sensor

import android.content.Intent
import android.net.Uri
import android.preference.PreferenceManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import id.antasari.floodwatch_230104040122.domain.model.Sensor
import id.antasari.floodwatch_230104040122.presentation.home.BottomNavigationBar
import id.antasari.floodwatch_230104040122.presentation.home.HomeViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

// --- PALET WARNA ---
val BgGrayApp = Color(0xFFF4F6F8)
val TextBlack = Color(0xFF1A1C1E)
val TextGray = Color(0xFF6C757D)
val BlueStart = Color(0xFF1E88E5)
val BlueEnd = Color(0xFF1565C0)
val BluePrimary = Color(0xFF1565C0)
val GreenBg = Color(0xFFE8F5E9)
val GreenText = Color(0xFF2E7D32)
val YellowBg = Color(0xFFFFF9C4)
val YellowText = Color(0xFFFBC02D)
val RedBg = Color(0xFFFFEBEE)
val RedText = Color(0xFFC62828)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorListScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val sensorList by viewModel.sensorState.collectAsState()

    // --- STATE UNTUK POPUP MAP ---
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedSensor by remember { mutableStateOf<Sensor?>(null) }
    val sheetState = rememberModalBottomSheetState()

    // STATE SEARCH
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = if (searchQuery.isEmpty()) {
        sensorList
    } else {
        sensorList.filter { sensor ->
            sensor.name.contains(searchQuery, ignoreCase = true) ||
                    sensor.status.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = BgGrayApp,
        topBar = {
            // --- HEADER CANTIK ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(BlueStart, BlueEnd)
                        ),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
            ) {
                Box(modifier = Modifier.offset(x = 280.dp, y = (-20).dp).size(150.dp).background(Color.White.copy(alpha = 0.1f), CircleShape))
                Box(modifier = Modifier.offset(x = (-30).dp, y = 60.dp).size(80.dp).background(Color.White.copy(alpha = 0.05f), CircleShape))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Monitoring Area", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text("Data sensor real-time", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->

        // --- KONTEN UTAMA ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Cari lokasi sensor...", color = TextGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextGray)
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            // LIST SENSOR
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredList) { sensor ->
                    SensorListItemv2(
                        sensor = sensor,
                        onClick = {
                            // SAAT KLIK: Buka Popup Map
                            selectedSensor = sensor
                            showBottomSheet = true
                        }
                    )
                }

                if (filteredList.isEmpty() && searchQuery.isNotEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                            Text("Lokasi tidak ditemukan", color = TextGray)
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }

        // --- POPUP MAP (BOTTOM SHEET) ---
        if (showBottomSheet && selectedSensor != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                SensorMapPopupOsmdroid(sensor = selectedSensor!!)
            }
        }
    }
}

// --- KOMPONEN KARTU SENSOR (UI LIST) ---
@Composable
fun SensorListItemv2(
    sensor: Sensor,
    onClick: () -> Unit
) {
    val (badgeBg, badgeTxt) = when (sensor.status.uppercase()) {
        "AMAN" -> Pair(GreenBg, GreenText)
        "WASPADA" -> Pair(YellowBg, YellowText)
        else -> Pair(RedBg, RedText)
    }

    val nameParts = sensor.name.split(" (")
    val title = nameParts.getOrElse(0) { sensor.name }
    val subtitle = if (nameParts.size > 1) "ID: ${nameParts[1].replace(")", "")}" else "Sensor Area"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = subtitle, fontSize = 12.sp, color = TextGray)
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(badgeBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).background(badgeTxt, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = sensor.status, color = badgeTxt, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Water Level", fontSize = 11.sp, color = TextGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${sensor.waterLevel} cm", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Last Update", fontSize = 11.sp, color = TextGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Baru saja", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextBlack)
                }
            }
        }
    }
}

// --- KOMPONEN POPUP MAP (OSMDROID + TOMBOL GOOGLE MAPS) ---
@Composable
fun SensorMapPopupOsmdroid(sensor: Sensor) {
    val context = LocalContext.current

    // Inisialisasi Konfigurasi Osmdroid (Wajib agar map tidak blank)
    DisposableEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = context.packageName
        onDispose { }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Judul Popup
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = sensor.name,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextBlack
            )
            Text(
                text = "Status: ${sensor.status} (${sensor.waterLevel.toInt()} cm)",
                color = if(sensor.status == "BAHAYA") Color.Red else Color.Gray,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. MAP VIEW (Osmdroid)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp) // Tinggi map
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
        ) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                    }
                },
                update = { mapView ->
                    val point = GeoPoint(sensor.lat, sensor.lng)

                    // Pindah kamera ke titik sensor
                    mapView.controller.setCenter(point)

                    // Bersihkan marker lama
                    mapView.overlays.clear()

                    // Tambah Marker Baru
                    val marker = Marker(mapView)
                    marker.position = point
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = sensor.name
                    marker.snippet = "Status: ${sensor.status}"

                    // Tampilkan balon info marker
                    marker.showInfoWindow()

                    mapView.overlays.add(marker)
                    mapView.invalidate() // Refresh map
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. TOMBOL: BUKA DI GOOGLE MAPS
        Button(
            onClick = {
                // Logic Intent ke Google Maps
                val gmmIntentUri = Uri.parse("geo:${sensor.lat},${sensor.lng}?q=${sensor.lat},${sensor.lng}(${sensor.name})")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps") // Paksa buka App Google Maps

                try {
                    context.startActivity(mapIntent)
                } catch (e: Exception) {
                    // Kalau HP tidak ada Google Maps App, buka via browser
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${sensor.lat},${sensor.lng}"))
                    context.startActivity(browserIntent)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Buka Rute di Google Maps", color = Color.White)
        }
    }
}