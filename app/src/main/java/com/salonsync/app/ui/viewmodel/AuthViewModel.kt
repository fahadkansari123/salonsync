package com.salonsync.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.salonsync.app.data.model.Role
import com.salonsync.app.data.model.User
import com.salonsync.app.data.repository.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class PasswordResetSent(val email: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        val currentUid = FirebaseManager.currentUser?.uid
        if (currentUid != null) {
            loadUserProfile(currentUid)
        }
    }

    private fun loadUserProfile(uid: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            FirebaseManager.observeUserProfile(uid).collect { user ->
                if (user != null) {
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    val defaultEmail = FirebaseManager.currentUser?.email ?: ""
                    val newUser = User(
                        uid = uid,
                        email = defaultEmail,
                        name = defaultEmail.substringBefore("@").ifEmpty { "User" },
                        role = "customer"
                    )
                    FirebaseManager.saveUserProfile(newUser)
                    _authState.value = AuthState.Authenticated(newUser)
                }
            }
        }
    }

    fun login(emailInput: String, passInput: String) {
        val email = emailInput.trim()
        val pass = passInput.trim()

        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Please enter email and password")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = FirebaseManager.auth.signInWithEmailAndPassword(email, pass).await()
                val uid = result.user?.uid
                if (uid != null) {
                    loadUserProfile(uid)
                } else {
                    _authState.value = AuthState.Error("Sign in failed")
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _authState.value = AuthState.Error("Incorrect password or email format. Please check your credentials.")
            } catch (e: FirebaseAuthInvalidUserException) {
                _authState.value = AuthState.Error("Account not found. Please click 'Sign Up' below to create your account.")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Login failed. Please try again.")
            }
        }
    }

    fun sendPasswordReset(emailInput: String) {
        val email = emailInput.trim()
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Please enter your email address to reset password")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                FirebaseManager.auth.sendPasswordResetEmail(email).await()
                _authState.value = AuthState.PasswordResetSent(email)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Failed to send reset email")
            }
        }
    }

    fun register(emailInput: String, passInput: String, nameInput: String, phoneInput: String, role: Role) {
        val email = emailInput.trim()
        val pass = passInput.trim()
        val name = nameInput.trim()
        val phone = phoneInput.trim()

        if (email.isBlank() || pass.isBlank() || name.isBlank()) {
            _authState.value = AuthState.Error("Please fill in required fields")
            return
        }

        if (pass.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters long")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = FirebaseManager.auth.createUserWithEmailAndPassword(email, pass).await()
                val uid = result.user?.uid
                if (uid != null) {
                    val user = User(
                        uid = uid,
                        name = name,
                        email = email,
                        phone = phone,
                        role = role.name.lowercase()
                    )
                    FirebaseManager.saveUserProfile(user)
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    _authState.value = AuthState.Error("Registration failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Registration error")
            }
        }
    }

    fun quickDemoLogin(role: Role) {
        val demoEmail = if (role == Role.ADMIN) "admin@salonsync.com" else "customer@salonsync.com"
        val demoPass = "password123"
        val demoName = if (role == Role.ADMIN) "Demo Admin" else "Demo Customer"

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = FirebaseManager.auth.signInWithEmailAndPassword(demoEmail, demoPass).await()
                val uid = result.user?.uid
                if (uid != null) {
                    loadUserProfile(uid)
                }
            } catch (e: Exception) {
                // If demo user does not exist yet, auto create it
                try {
                    val createResult = FirebaseManager.auth.createUserWithEmailAndPassword(demoEmail, demoPass).await()
                    val uid = createResult.user?.uid
                    if (uid != null) {
                        val user = User(
                            uid = uid,
                            name = demoName,
                            email = demoEmail,
                            phone = "+919876543210",
                            role = role.name.lowercase()
                        )
                        FirebaseManager.saveUserProfile(user)
                        _authState.value = AuthState.Authenticated(user)
                    }
                } catch (createErr: Exception) {
                    _authState.value = AuthState.Error("Demo login error: ${createErr.localizedMessage}")
                }
            }
        }
    }

    private val _profileMessage = MutableStateFlow<String?>(null)
    val profileMessage: StateFlow<String?> = _profileMessage.asStateFlow()

    fun clearProfileMessage() {
        _profileMessage.value = null
    }

    fun updateUserProfile(nameInput: String, phoneInput: String) {
        val currentUser = (_authState.value as? AuthState.Authenticated)?.user ?: return
        val newName = nameInput.trim()
        val newPhone = phoneInput.trim()

        if (newName.isBlank()) {
            _profileMessage.value = "Name cannot be empty"
            return
        }

        viewModelScope.launch {
            val updatedUser = currentUser.copy(name = newName, phone = newPhone)
            val result = FirebaseManager.saveUserProfile(updatedUser)
            if (result.isSuccess) {
                _authState.value = AuthState.Authenticated(updatedUser)
                _profileMessage.value = "Profile updated successfully! ✨"
            } else {
                _profileMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Failed to update profile"
            }
        }
    }

    fun sendPasswordResetForCurrentUser() {
        val email = (_authState.value as? AuthState.Authenticated)?.user?.email
            ?: FirebaseManager.currentUser?.email
            ?: return
        sendPasswordReset(email)
    }

    fun signOut() {
        FirebaseManager.auth.signOut()
        _authState.value = AuthState.Idle
    }
}
