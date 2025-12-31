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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WaterDrop
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

val BlueStart = Color(0xFF1E88E5)
val BlueEnd = Color(0xFF1565C0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance() // Inisialisasi Firebase Auth

    // Cek jika user sudah login sebelumnya (Auto Login)
    LaunchedEffect(Unit) {
        if (auth.currentUser != null) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(BlueStart, BlueEnd)))
    ) {
        // Hiasan Bubbles
        Box(modifier = Modifier.align(Alignment.TopEnd).offset(40.dp, (-40).dp).size(200.dp).background(Color.White.copy(0.1f), CircleShape))
        Box(modifier = Modifier.align(Alignment.TopStart).offset((-50).dp, 100.dp).size(150.dp).background(Color.White.copy(0.1f), CircleShape))
        Box(modifier = Modifier.align(Alignment.BottomCenter).offset(y = 50.dp).size(300.dp).background(Color.White.copy(0.05f), CircleShape))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Box(modifier = Modifier.size(100.dp).background(Color.White.copy(0.2f), CircleShape).padding(20.dp), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.WaterDrop, contentDescription = "Logo", tint = Color.White, modifier = Modifier.size(60.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("FloodWatch", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Pantau Banjir Secara Realtime", fontSize = 14.sp, color = Color.White.copy(0.8f))

            Spacer(modifier = Modifier.height(48.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Silakan Masuk", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BlueEnd, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(24.dp))

                    // INPUT EMAIL
                    OutlinedTextField(
                        value = email, onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = inputColors(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // INPUT PASSWORD
                    OutlinedTextField(
                        value = password, onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(image, null) }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = inputColors(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // TOMBOL LOGIN (REAL FIREBASE)
                    Button(
                        onClick = {
                            if (email.isEmpty() || password.isEmpty()) {
                                Toast.makeText(context, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                            } else {
                                // --- LOGIKA FIREBASE ---
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                                            onLoginSuccess()
                                        } else {
                                            val errorMsg = task.exception?.message ?: "Login Gagal"
                                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueEnd),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Masuk Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // LINK KE REGISTER
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Belum punya akun? ", color = Color.White.copy(0.8f), fontSize = 14.sp)
                Text(
                    text = "Daftar",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        navController.navigate("register")
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun inputColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    cursorColor = BlueEnd,
    focusedBorderColor = BlueEnd,
    unfocusedBorderColor = Color.Gray,
    focusedLabelColor = BlueEnd,
    unfocusedLabelColor = Color.Gray,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White
)