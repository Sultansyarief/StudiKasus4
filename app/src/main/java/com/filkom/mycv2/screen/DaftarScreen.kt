package com.filkom.mycv2.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.filkom.mycv2.viewmodel.AuthViewModel
import com.filkom.mycv2.viewmodel.RegisterStatus

@Composable
fun DaftarScreen(
    viewModel: AuthViewModel = viewModel(),
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    val registerStatus by viewModel.registerStatus.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // State untuk input form (pre-fill jika ada current user)
    var nim by remember { mutableStateOf(currentUser?.nim ?: "") }
    var nama by remember { mutableStateOf(currentUser?.nama ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var alamat by remember { mutableStateOf(currentUser?.alamat ?: "") }

    // Update form jika current user berubah
    LaunchedEffect(currentUser) {
        currentUser?.let {
            nim = it.nim
            nama = it.nama
            email = it.email
            alamat = it.alamat
        }
    }

    // Handle register status
    LaunchedEffect(registerStatus) {
        when (registerStatus) {
            is RegisterStatus.Success -> {
                Toast.makeText(
                    context,
                    (registerStatus as RegisterStatus.Success).message,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetRegisterStatus()
                onRegisterSuccess()
            }
            is RegisterStatus.Error -> {
                Toast.makeText(
                    context,
                    (registerStatus as RegisterStatus.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetRegisterStatus()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        Text(
            text = "DAFTAR",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Input NIM
        OutlinedTextField(
            value = nim,
            onValueChange = { nim = it },
            label = { Text("NIM") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        )

        // Input Nama
        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            label = { Text("Nama") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        )

        // Input Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        )

        // Input Alamat
        OutlinedTextField(
            value = alamat,
            onValueChange = { alamat = it },
            label = { Text("Alamat") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            minLines = 3,
            maxLines = 5
        )

        // Tombol SIMPAN
        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 20.dp)
                .fillMaxWidth(),
            onClick = {
                viewModel.register(nim, nama, email, alamat)
            }
        ) {
            Text("SIMPAN")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DaftarScreenPreview() {
    DaftarScreen(onRegisterSuccess = {})
}