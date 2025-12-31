package id.antasari.floodwatch_230104040122.domain.model

data class Comment(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val photoUrl: String = "", // Link foto dari Cloudinary
    val timestamp: Long = System.currentTimeMillis()
)