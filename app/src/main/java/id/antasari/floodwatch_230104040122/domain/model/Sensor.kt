package id.antasari.floodwatch_230104040122.domain.model

data class Sensor(
    val docId: String = "",       // ID Dokumen dari Firebase
    val name: String = "",        // Nama Lokasi
    val waterLevel: Double = 0.0, // Tinggi Air
    val status: String = "AMAN",  // Status (AMAN/WASPADA/BAHAYA)
    val lat: Double = 0.0,        // <-- INI YANG MUNGKIN KURANG TADI
    val lng: Double = 0.0,        // <-- INI JUGA
    val lastUpdated: Long = 0L    // <-- DAN INI
) {
    // Constructor kosong (Wajib untuk Firebase deserialization)
    constructor() : this("", "", 0.0, "AMAN", 0.0, 0.0, 0L)
}