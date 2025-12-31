package id.antasari.floodwatch_230104040122.data.repository


import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import id.antasari.floodwatch_230104040122.domain.model.Comment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Kita masih pakai collection "sensors" -> "comments" agar data lama tidak hilang
    private val sensorCollection = db.collection("sensors")

    // =================================================================
    // BAGIAN FITUR: PELAPORAN WARGA (UPLOAD & KOMENTAR)
    // =================================================================

    fun getComments(sensorId: String, onResult: (List<Comment>) -> Unit) {
        sensorCollection.document(sensorId).collection("comments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val comments = snapshot?.toObjects(Comment::class.java) ?: emptyList()
                onResult(comments)
            }
    }

    fun addComment(
        sensorId: String,
        text: String,
        imageUri: Uri?,
        onResult: (Boolean) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            Log.e("UPLOAD_CHECK", "Gagal: User belum login!")
            onResult(false)
            return
        }

        val commentRef = sensorCollection.document(sensorId).collection("comments").document()
        val commentId = commentRef.id

        // --- FUNGSI SIMPAN KE FIREBASE ---
        fun saveToFirebase(finalImageUrl: String) {
            Log.d("UPLOAD_CHECK", "Menyimpan ke Firebase. URL: $finalImageUrl")

            val newComment = Comment(
                id = commentId,
                userId = user.uid,
                userName = user.displayName ?: "Warga",
                text = text,
                photoUrl = finalImageUrl,
                timestamp = System.currentTimeMillis()
            )

            commentRef.set(newComment)
                .addOnSuccessListener {
                    Log.d("UPLOAD_CHECK", "BERHASIL SIMPAN DATA!")
                    onResult(true)
                }
                .addOnFailureListener { e ->
                    Log.e("UPLOAD_CHECK", "Gagal simpan ke Firebase: ${e.message}")
                    onResult(false)
                }
        }

        // --- LOGIKA UPLOAD KE CLOUDINARY ---
        if (imageUri != null) {
            Log.d("UPLOAD_CHECK", "Mulai Upload Foto ke Cloudinary...")
            try {
                MediaManager.get().upload(imageUri)
                    .unsigned("banjir_preset") // <--- Nama Preset Kamu
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String?) {}
                        override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                        override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                            Log.d("UPLOAD_CHECK", "Cloudinary: Upload Sukses!")
                            val cloudUrl = resultData?.get("secure_url") as? String ?: ""
                            saveToFirebase(cloudUrl)
                        }

                        override fun onError(requestId: String?, error: ErrorInfo?) {
                            Log.e("UPLOAD_CHECK", "Cloudinary GAGAL: ${error?.description}")
                            onResult(false)
                        }

                        override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                            onResult(false)
                        }
                    })
                    .dispatch()
            } catch (e: Exception) {
                Log.e("UPLOAD_CHECK", "FATAL ERROR: ${e.message}")
                onResult(false)
            }
        } else {
            // Jika tidak ada foto
            saveToFirebase("")
        }
    }
}