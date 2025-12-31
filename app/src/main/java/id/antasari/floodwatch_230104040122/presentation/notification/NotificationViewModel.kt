package id.antasari.floodwatch_230104040122.presentation.notification

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.antasari.floodwatch_230104040122.data.repository.ReportRepository
import id.antasari.floodwatch_230104040122.domain.model.Comment
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    // Menyimpan ID Lokasi yang sedang dibuka (misal: "Jalan Simpang Merdeka (SN-8264)")
    private var currentChannelId: String = ""

    // State UI
    var commentText by mutableStateOf("")
    var selectedImageUri by mutableStateOf<Uri?>(null)
    var commentsList by mutableStateOf<List<Comment>>(emptyList())
    var isLoading by mutableStateOf(false)

    // Fungsi dipanggil saat user klik salah satu kartu notifikasi
    fun setChannel(channelId: String) {
        currentChannelId = channelId
        loadReports() // Muat pesan khusus untuk lokasi ini saja
    }

    fun loadReports() {
        if (currentChannelId.isNotEmpty()) {
            reportRepository.getComments(currentChannelId) { comments ->
                commentsList = comments
            }
        }
    }

    fun sendReport() {
        if ((commentText.isBlank() && selectedImageUri == null) || currentChannelId.isEmpty()) return

        isLoading = true

        // Kirim ke channel ID yang spesifik (bukan global)
        reportRepository.addComment(currentChannelId, commentText, selectedImageUri) { success ->
            isLoading = false
            if (success) {
                commentText = ""
                selectedImageUri = null
                loadReports()
            }
        }
    }
}