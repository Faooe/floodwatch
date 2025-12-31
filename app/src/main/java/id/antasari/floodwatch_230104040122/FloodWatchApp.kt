package id.antasari.floodwatch_230104040122

import android.app.Application
import com.cloudinary.android.MediaManager
import dagger.hilt.android.HiltAndroidApp
import android.util.Log

@HiltAndroidApp
class FloodWatchApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // --- KONFIGURASI CLOUDINARY ---
        val config = HashMap<String, String>()

        // CEK 3 BARIS INI DENGAN TELITI:
        config["cloud_name"] = "dlb8dv4ei" // <--- Pastikan ini benar (dari dashboard kamu)
        config["api_key"] = "869713756192849"
        config["api_secret"] = "aipAmJjS_FVab-aiBsnktj8e1BI"

        try {
            MediaManager.init(this, config)
            Log.d("CLOUDINARY_INIT", "Cloudinary Berhasil Diaktifkan!")
        } catch (e: Exception) {
            Log.e("CLOUDINARY_INIT", "Gagal Init Cloudinary: ${e.message}")
        }
    }
}