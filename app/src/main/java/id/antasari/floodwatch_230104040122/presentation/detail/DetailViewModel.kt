package id.antasari.floodwatch_230104040122.presentation.detail

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.antasari.floodwatch_230104040122.data.repository.ReportRepository // <--- Ganti Import ini
import id.antasari.floodwatch_230104040122.domain.model.Comment
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val reportRepository: ReportRepository // <--- Ganti Injeksi ini
) : ViewModel() {

    var commentText by mutableStateOf("")
    var selectedImageUri by mutableStateOf<Uri?>(null)
    var commentsList by mutableStateOf<List<Comment>>(emptyList())
    var isLoading by mutableStateOf(false)

    // Load komentar saat layar dibuka
    fun loadComments(sensorId: String) {
        // Panggil fungsi dari reportRepository
        reportRepository.getComments(sensorId) { comments ->
            commentsList = comments
        }
    }

    // Kirim komentar
    fun sendComment(sensorId: String) {
        // Jangan kirim kalau kosong semua
        if (commentText.isBlank() && selectedImageUri == null) return

        isLoading = true

        // Panggil fungsi dari reportRepository
        reportRepository.addComment(sensorId, commentText, selectedImageUri) { success ->
            isLoading = false
            if (success) {
                // Reset inputan kalau berhasil
                commentText = ""
                selectedImageUri = null
            }
        }
    }
}