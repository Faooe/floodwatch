package id.antasari.floodwatch_230104040122.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.antasari.floodwatch_230104040122.data.repository.SensorRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// Class sederhana untuk menampung data statistik
data class ProfileStats(
    val totalLaporan: String = "0",
    val totalWilayah: String = "0",
    val statusAkun: String = "Aktif"
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    repository: SensorRepository
) : ViewModel() {

    // Kita ambil data dari Repository, lalu kita ubah (Map) jadi angka statistik
    val statsState: StateFlow<ProfileStats> = repository.sensors
        .map { sensorList ->
            ProfileStats(
                // Hitung total semua data sensor yang masuk
                totalLaporan = sensorList.size.toString(),

                // Hitung jumlah Nama Lokasi yang unik (Distinct)
                // Jadi kalau ada 2 laporan di "Sungai Martapura", dihitung 1 wilayah
                totalWilayah = sensorList.map { it.name }.distinct().size.toString(),

                statusAkun = "Aktif" // Default Aktif
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileStats()
        )
}