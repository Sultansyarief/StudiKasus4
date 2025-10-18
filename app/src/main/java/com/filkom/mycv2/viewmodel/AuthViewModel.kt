package com.filkom.mycv2.viewmodel

import androidx.lifecycle.ViewModel
import com.filkom.mycv2.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {

    // State untuk menyimpan data user yang sedang login
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // State untuk menyimpan daftar user yang terdaftar (simulasi database)
    private val _registeredUsers = MutableStateFlow<List<User>>(emptyList())
    val registeredUsers: StateFlow<List<User>> = _registeredUsers.asStateFlow()

    // State untuk status login
    private val _loginStatus = MutableStateFlow<LoginStatus>(LoginStatus.Idle)
    val loginStatus: StateFlow<LoginStatus> = _loginStatus.asStateFlow()

    // State untuk status registrasi
    private val _registerStatus = MutableStateFlow<RegisterStatus>(RegisterStatus.Idle)
    val registerStatus: StateFlow<RegisterStatus> = _registerStatus.asStateFlow()

    /**
     * Fungsi untuk Login
     */
    fun login(nim: String, nama: String, email: String, password: String) {
        if (nim.isEmpty() || nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _loginStatus.value = LoginStatus.Error("Semua field harus diisi!")
            return
        }

        // Cek apakah user sudah terdaftar
        val user = _registeredUsers.value.find {
            it.nim == nim && it.email == email
        }

        if (user != null) {
            _currentUser.value = user
            _loginStatus.value = LoginStatus.Success
        } else {
            // Jika belum terdaftar, buat user baru (auto register)
            val newUser = User(
                nim = nim,
                nama = nama,
                email = email,
                password = password,
                alamat = ""
            )
            _currentUser.value = newUser
            _loginStatus.value = LoginStatus.Success
        }
    }

    /**
     * Fungsi untuk Daftar/Register
     */
    fun register(nim: String, nama: String, email: String, alamat: String) {
        if (nim.isEmpty() || nama.isEmpty() || email.isEmpty() || alamat.isEmpty()) {
            _registerStatus.value = RegisterStatus.Error("Semua field harus diisi!")
            return
        }

        // Cek apakah NIM sudah terdaftar
        val existingUser = _registeredUsers.value.find { it.nim == nim }

        if (existingUser != null) {
            // Update data user yang sudah ada
            val updatedUser = existingUser.copy(
                nama = nama,
                email = email,
                alamat = alamat
            )
            _registeredUsers.value = _registeredUsers.value.map {
                if (it.nim == nim) updatedUser else it
            }
            _currentUser.value = updatedUser
            _registerStatus.value = RegisterStatus.Success("Data berhasil diupdate!")
        } else {
            // Tambah user baru
            val newUser = User(
                nim = nim,
                nama = nama,
                email = email,
                password = "",
                alamat = alamat
            )
            _registeredUsers.value = _registeredUsers.value + newUser
            _currentUser.value = newUser
            _registerStatus.value = RegisterStatus.Success("Registrasi berhasil!")
        }
    }

    /**
     * Fungsi untuk Logout
     */
    fun logout() {
        _currentUser.value = null
        _loginStatus.value = LoginStatus.Idle
        _registerStatus.value = RegisterStatus.Idle
    }

    /**
     * Reset status
     */
    fun resetLoginStatus() {
        _loginStatus.value = LoginStatus.Idle
    }

    fun resetRegisterStatus() {
        _registerStatus.value = RegisterStatus.Idle
    }
}

// Sealed class untuk Login Status
sealed class LoginStatus {
    object Idle : LoginStatus()
    object Success : LoginStatus()
    data class Error(val message: String) : LoginStatus()
}

// Sealed class untuk Register Status
sealed class RegisterStatus {
    object Idle : RegisterStatus()
    data class Success(val message: String) : RegisterStatus()
    data class Error(val message: String) : RegisterStatus()
}