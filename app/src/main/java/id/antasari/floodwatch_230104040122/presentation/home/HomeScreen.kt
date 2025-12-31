package id.antasari.floodwatch_230104040122.presentation.home

import android.content.Intent // IMPORT PENTING
import android.net.Uri // IMPORT PENTING
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext // IMPORT PENTING
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import id.antasari.floodwatch_230104040122.domain.model.Sensor
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

// --- PALET WARNA ---
val BrandBlue = Color(0xFF1E88E5)
val BgGray = Color(0xFFF5F7FA)
val CardWhite = Color.White
val TextDark = Color(0xFF2D3436)
val TextGray = Color(0xFF636E72)

val SuccessGreen = Color(0xFF00C853)
val WarningYellow = Color(0xFFFFAB00)
val DangerRed = Color(0xFFD50000)

fun getStatusColor(status: String): Color {
    return when (status.uppercase()) {
        "BAHAYA" -> DangerRed
        "WASPADA" -> WarningYellow
        "AMAN" -> SuccessGreen
        else -> TextGray
    }
}

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val sensorList by viewModel.sensorState.collectAsState()
    var selectedSensor by remember { mutableStateOf<Sensor?>(null) }

    LaunchedEffect(sensorList) {
        if (sensorList.isNotEmpty() && (selectedSensor == null || sensorList.none { it.docId == selectedSensor?.docId })) {
            selectedSensor = sensorList.first()
        } else if (sensorList.isEmpty()) {
            selectedSensor = null
        }
    }

    Scaffold(
        containerColor = BgGray,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_sensor") },
                containerColor = BrandBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Lapor")
            }
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // --- HEADER SECTION ---
            HeaderSection(
                currentSensor = selectedSensor,
                allSensors = sensorList,
                onSensorSelected = { selectedSensor = it },
                onDeleteSensor = { viewModel.deleteSensor(it.docId) },
                onProfileClick = { navController.navigate("profile") }
            )

            Column(modifier = Modifier.padding(16.dp)) {
                if (selectedSensor != null) {
                    WaterLevelCard(selectedSensor!!)
                    Spacer(modifier = Modifier.height(16.dp))
                    RiskIndexCard(selectedSensor!!)
                } else {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        if (sensorList.isEmpty()) Text("Belum ada data sensor", color = TextGray)
                        else CircularProgressIndicator(color = BrandBlue)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // KARTU MAPS (LIVE)
                NearbySensorsCard(navController, sensorList)

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun HeaderSection(
    currentSensor: Sensor?,
    allSensors: List<Sensor>,
    onSensorSelected: (Sensor) -> Unit,
    onDeleteSensor: (Sensor) -> Unit,
    onProfileClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .background(
                brush = Brush.verticalGradient(colors = listOf(BrandBlue, Color(0xFF1565C0))),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
    ) {
        Box(modifier = Modifier.offset(x = 280.dp, y = (-40).dp).size(150.dp).background(Color.White.copy(alpha = 0.1f), CircleShape))
        Box(modifier = Modifier.offset(x = (-40).dp, y = 80.dp).size(100.dp).background(Color.White.copy(alpha = 0.05f), CircleShape))

        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Halo, Warga!", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Text("FloodWatch", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape).size(40.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Lokasi Pantauan:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box {
                                Row(modifier = Modifier.clickable { expanded = true }, verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentSensor?.name ?: "Pilih...",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.widthIn(max = 140.dp)
                                    )
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                                }

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier
                                        .background(CardWhite)
                                        .width(275.dp)
                                        .heightIn(max = 150.dp)
                                ) {
                                    allSensors.forEach { sensor ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(sensor.name, fontWeight = FontWeight.Bold, color = TextDark)
                                                }
                                            },
                                            onClick = { onSensorSelected(sensor); expanded = false }
                                        )
                                        Divider(color = Color.LightGray.copy(alpha = 0.3f))
                                    }
                                }
                            }
                            if (currentSensor != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = { onDeleteSensor(currentSensor) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.White.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Icon(Icons.Default.Star, contentDescription = "Cuaca", tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(28.dp))
                    Text("28°C", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Cerah", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun WaterLevelCard(data: Sensor) {
    Card(colors = CardDefaults.cardColors(containerColor = CardWhite), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Tinggi Air Saat Ini", color = TextGray, fontSize = 14.sp)
                Box(modifier = Modifier.size(32.dp).background(BrandBlue.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.LocationOn, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(16.dp)) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("${data.waterLevel} cm", color = TextDark, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Lokasi: ${data.name}", color = TextGray, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun RiskIndexCard(data: Sensor) {
    val statusColor = getStatusColor(data.status)
    val icon = when (data.status.uppercase()) { "BAHAYA" -> Icons.Default.Warning; "WASPADA" -> Icons.Default.Info; else -> Icons.Default.ThumbUp }
    Card(colors = CardDefaults.cardColors(containerColor = CardWhite), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Status Banjir", color = TextGray, fontSize = 14.sp)
                Box(modifier = Modifier.size(32.dp).background(statusColor.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp)) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(data.status, color = statusColor, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            val progress = when(data.status.uppercase()) { "BAHAYA" -> 0.9f; "WASPADA" -> 0.6f; else -> 0.2f }
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(progress = progress, modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)), color = statusColor, trackColor = Color.LightGray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.width(12.dp))
                Text("${(progress * 100).toInt()}%", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// --- BAGIAN INI YANG DIMODIFIKASI UNTUK KLIK MAPS ---
@Composable
fun NearbySensorsCard(navController: NavController, sensors: List<Sensor>) {
    val context = LocalContext.current // Butuh context untuk buka Maps

    Card(colors = CardDefaults.cardColors(containerColor = CardWhite), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Peta Sebaran (Live)", color = TextDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Lihat List >", color = BrandBlue, fontSize = 14.sp, modifier = Modifier.clickable { navController.navigate("sensor_list") })
            }
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFE3F2FD))) {
                AndroidView(
                    factory = { context ->
                        Configuration.getInstance().userAgentValue = context.packageName
                        MapView(context).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(13.0)
                            // Default ke Banjarmasin kalau belum ada sensor
                            controller.setCenter(GeoPoint(-3.3194, 114.5928))
                        }
                    },
                    update = { mapView ->
                        mapView.overlays.clear()
                        sensors.forEach { sensor ->
                            val marker = Marker(mapView)
                            marker.position = GeoPoint(sensor.lat, sensor.lng)
                            marker.title = sensor.name
                            marker.snippet = "Status: ${sensor.status}\n(Klik untuk Navigasi Google Maps)"
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                            // --- EVENT KLIK MARKER ---
                            marker.setOnMarkerClickListener { _, _ ->
                                // Tampilkan Info Window kecil
                                marker.showInfoWindow()

                                // Buka Google Maps
                                val gmmIntentUri = Uri.parse("geo:${sensor.lat},${sensor.lng}?q=${sensor.lat},${sensor.lng}(${sensor.name})")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps") // Paksa buka App Maps

                                try {
                                    context.startActivity(mapIntent)
                                } catch (e: Exception) {
                                    // Jika tidak punya App Maps, buka via Browser
                                    val browserIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    context.startActivity(browserIntent)
                                }
                                true
                            }

                            mapView.overlays.add(marker)
                        }
                        // Fokus ke sensor pertama jika ada
                        if (sensors.isNotEmpty()) {
                            mapView.controller.setCenter(GeoPoint(sensors[0].lat, sensors[0].lng))
                        }
                        mapView.invalidate()
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val SelectedColor = BrandBlue
    val UnselectedColor = Color(0xFF9A9A9A)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        color = Color.White,
        shadowElevation = 20.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, "") },
                label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                selected = currentRoute == "home",
                onClick = { if (currentRoute != "home") navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = SelectedColor, selectedTextColor = SelectedColor, unselectedIconColor = UnselectedColor, unselectedTextColor = UnselectedColor, indicatorColor = Color(0xFFEEEEEE))
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.Settings, "") },
                label = { Text("Sensor", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                selected = currentRoute == "sensor_list",
                onClick = { if (currentRoute != "sensor_list") navController.navigate("sensor_list") },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = SelectedColor, selectedTextColor = SelectedColor, unselectedIconColor = UnselectedColor, unselectedTextColor = UnselectedColor, indicatorColor = Color(0xFFEEEEEE))
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.Notifications, "Notifikasi") },
                label = { Text("Notifikasi", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                selected = currentRoute == "notification",
                onClick = { if (currentRoute != "notification") navController.navigate("notification") },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = SelectedColor, selectedTextColor = SelectedColor, unselectedIconColor = UnselectedColor, unselectedTextColor = UnselectedColor, indicatorColor = Color(0xFFEEEEEE))
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, "") },
                label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                selected = currentRoute == "profile",
                onClick = { if (currentRoute != "profile") navController.navigate("profile") },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = SelectedColor, selectedTextColor = SelectedColor, unselectedIconColor = UnselectedColor, unselectedTextColor = UnselectedColor, indicatorColor = Color(0xFFEEEEEE))
            )
        }
    }
}