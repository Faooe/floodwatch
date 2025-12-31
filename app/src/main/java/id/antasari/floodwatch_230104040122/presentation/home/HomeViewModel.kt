package id.antasari.floodwatch_230104040122.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.antasari.floodwatch_230104040122.data.repository.SensorRepository
import id.antasari.floodwatch_230104040122.domain.model.Sensor
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SensorRepository
) : ViewModel() {

    // 1. ALIRAN DATA DARI FIREBASE (Real-time)
    val sensorState: StateFlow<List<Sensor>> = repository.sensors
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. FUNGSI HAPUS DATA (Harus di dalam kurung kurawal class)
    fun deleteSensor(docId: String) {
        if (docId.isNotEmpty()) {
            repository.deleteSensor(docId)
        }
    }
}