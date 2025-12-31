package id.antasari.floodwatch_230104040122.presentation.sensor

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.antasari.floodwatch_230104040122.data.repository.SensorRepository
import javax.inject.Inject

@HiltViewModel
class AddSensorViewModel @Inject constructor(
    private val repository: SensorRepository
) : ViewModel() {

    fun submitReport(name: String, waterLevelString: String, lat: Double, lng: Double) {
        if (name.isBlank() || waterLevelString.isBlank()) return

        val height = waterLevelString.toIntOrNull() ?: 0

        // SEKARANG KITA KIRIM KOORDINATNYA KE REPO
        repository.addSensorReport(name, height, lat, lng)
    }
}