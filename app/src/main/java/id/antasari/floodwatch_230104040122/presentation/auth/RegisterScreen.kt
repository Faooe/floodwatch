package id.antasari.floodwatch_230104040122.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

private val RegisterBlueStart = Color(0xFF1E88E5)
private val RegisterBlueEnd = Color(0xFF1565C0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance() // Inisialisasi Firebase

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(RegisterBlueStart, RegisterBlueEnd)))
    ) {
        // Hiasan Bubbles
        Box(modifier = Modifier.align(Alignment.TopEnd).offset(40.dp, (-40).dp).size(200.dp).background(Color.White.copy(0.1f), CircleShape))
        Box(modifier = Modifier.align(Alignment.CenterStart).offset((-50).dp, 100.dp).size(150.dp).background(Color.White.copy(0.1f), CircleShape))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text("Buat Akun Baru", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Gabung bersama FloodWatch", fontSize = 14.sp, color = Color.White.copy(0.8f))

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    // INPUT NAMA
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text("Nama Lengkap") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = registerInputColors()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // INPUT EMAIL
                    OutlinedTextField(
                        value = email, onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = registerInputColors()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // INPUT PASSWORD
                    OutlinedTextField(
                        value = password, onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(image, null) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = registerInputColors()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // INPUT KONFIRMASI PASSWORD
                    OutlinedTextField(
                        value = confirmPassword, onValueChange = { confirmPassword = it },
                        label = { Text("Ulangi Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = registerInputColors()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // TOMBOL DAFTAR (REAL FIREBASE)
                    Button(
                        onClick = {
                            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                                Toast.makeText(context, "Mohon lengkapi data!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (password != confirmPassword) {
                                Toast.makeText(context, "Password tidak sama!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // 1. Buat User
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser
                                        // 2. Update Nama Profil
                                        val profileUpdates = UserProfileChangeRequest.Builder()
                                            .setDisplayName(name).build()

                                        user?.updateProfile(profileUpdates)
                                            ?.addOnCompleteListener {
                                                Toast.makeText(context, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
                                                navController.popBackStack() // Kembali ke Login
                                            }
                                    } else {
                                        val errorMsg = task.exception?.message ?: "Gagal Daftar"
                                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RegisterBlueEnd),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Daftar Sekarang", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Link ke Login
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sudah punya akun? ", color = Color.White.copy(0.8f))
                Text(
                    text = "Masuk",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun registerInputColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    cursorColor = RegisterBlueEnd,
    focusedBorderColor = RegisterBlueEnd,
    unfocusedBorderColor = Color.Gray,
    focusedLabelColor = RegisterBlueEnd,
    unfocusedLabelColor = Color.Gray,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White
)