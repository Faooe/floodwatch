package id.antasari.floodwatch_230104040122.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import id.antasari.floodwatch_230104040122.domain.model.Sensor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SensorRepository @Inject constructor() {

    // Inisialisasi Firebase
    private val db = FirebaseFirestore.getInstance()
    private val sensorCollection = db.collection("sensors")

    // =================================================================
    // HANYA MENGURUS DATA SENSOR & MAPS
    // =================================================================

    /**
     * 1. GET DATA SENSOR (REAL-TIME)
     */
    val sensors: Flow<List<Sensor>> = callbackFlow {
        val listener = sensorCollection
            .orderBy("lastUpdated", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val data = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Sensor::class.java)?.copy(docId = doc.id)
                    }
                    trySend(data)
                }
            }
        awaitClose { listener.remove() }
    }

    /**
     * 2. ADD SENSOR BARU (Laporan Titik Banjir - Admin/User)
     */
    fun addSensorReport(name: String, height: Int, lat: Double, lng: Double) {
        val status = when {
            height >= 200 -> "BAHAYA"
            height >= 150 -> "WASPADA"
            else -> "AMAN"
        }

        val newSensor = Sensor(
            docId = "",
            name = name,
            waterLevel = height.toDouble(),
            status = status,
            lat = lat,
            lng = lng,
            lastUpdated = System.currentTimeMillis()
        )

        sensorCollection.add(newSensor)
    }

    /**
     * 3. DELETE SENSOR
     */
    fun deleteSensor(docId: String) {
        sensorCollection.document(docId).delete()
    }
}