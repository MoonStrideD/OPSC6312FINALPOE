package com.example.opsc6312finalpoe.repository

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.opsc6312finalpoe.models.User
import com.example.opsc6312finalpoe.utils.MicrosoftAuthHelper
import kotlinx.coroutines.launch

class AuthRepository(private val activity: Activity) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val microsoftAuthHelper = MicrosoftAuthHelper(activity)

    // One-tap Google Sign-In
    private val oneTapClient: SignInClient = Identity.getSignInClient(activity)
    private val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId("395438884628-3avh500f9s8c1boc65697sf3gqjkaqvi.apps.googleusercontent.com")
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()

    init {
        // Initialize Microsoft auth
        initializeMicrosoftAuth()
    }

    private fun initializeMicrosoftAuth() {
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            val result = microsoftAuthHelper.initialize()
            if (result.isSuccess) {
                Log.d("AuthRepository", "Microsoft auth initialized successfully")
            } else {
                Log.e("AuthRepository", "Microsoft auth initialization failed")
            }
        }
    }

    // EMAIL/PASSWORD AUTHENTICATION
    suspend fun loginWithEmail(email: String, password: String): Result<Boolean> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()

            // Update last login time
            authResult.user?.uid?.let { userId ->
                db.collection("users").document(userId)
                    .update("lastLogin", System.currentTimeMillis()).await()
            }

            Result.success(true)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Email login failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun registerWithEmail(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,
        role: String = "tenant"
    ): Result<Boolean> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = User(
                userId = authResult.user?.uid ?: "",
                email = email,
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                role = role
            )

            db.collection("users").document(user.userId).set(user).await()
            Log.d("AuthRepository", "User registered successfully: ${user.userId}")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Registration failed: ${e.message}")
            Result.failure(e)
        }
    }

    // GOOGLE AUTHENTICATION
    suspend fun startGoogleSignIn(): IntentSender? {
        return try {
            val result = oneTapClient.beginSignIn(signInRequest).await()
            result.pendingIntent.intentSender
        } catch (e: Exception) {
            Log.e("AuthRepository", "Google sign-in failed: ${e.message}")
            null
        }
    }

    suspend fun handleGoogleSignInResult(data: Intent?): Result<Boolean> {
        return try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken

            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()

                // Check if user exists, if not create new user
                val userId = authResult.user?.uid ?: ""
                val userDoc = db.collection("users").document(userId).get().await()

                if (!userDoc.exists()) {
                    val user = User(
                        userId = userId,
                        email = authResult.user?.email ?: "",
                        firstName = credential.givenName ?: "",
                        lastName = credential.familyName ?: "",
                        profilePhotoUrl = authResult.user?.photoUrl?.toString() ?: "",
                        role = "tenant"
                    )
                    db.collection("users").document(userId).set(user).await()
                    Log.d("AuthRepository", "New Google user created: $userId")
                } else {
                    // Update last login for existing user
                    db.collection("users").document(userId)
                        .update("lastLogin", System.currentTimeMillis()).await()
                }

                Result.success(true)
            } else {
                Result.failure(Exception("Google ID token is null"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Google sign-in handling failed: ${e.message}")
            Result.failure(e)
        }
    }

    // MICROSOFT AUTHENTICATION
    // MICROSOFT AUTHENTICATION - SIMPLIFIED VERSION
    suspend fun loginWithMicrosoft(): Result<Boolean> {
        return try {
            val provider = OAuthProvider.newBuilder("microsoft.com").build()

            // This handles the entire Microsoft OAuth flow
            val authResult = auth.startActivityForSignInWithProvider(activity, provider).await()

            // Create user in Firestore if needed
            val userId = authResult.user?.uid ?: ""
            val userDoc = db.collection("users").document(userId).get().await()

            if (!userDoc.exists()) {
                val user = User(
                    userId = userId,
                    email = authResult.user?.email ?: "",
                    firstName = authResult.user?.displayName?.split(" ")?.firstOrNull() ?: "",
                    lastName = authResult.user?.displayName?.split(" ")?.lastOrNull() ?: "",
                    profilePhotoUrl = authResult.user?.photoUrl?.toString() ?: "",
                    role = "tenant"
                )
                db.collection("users").document(userId).set(user).await()
            }

            Log.d("AuthRepository", "Microsoft login successful via Firebase")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Firebase Microsoft auth failed: ${e.message}")
            Result.failure(e)
        }
    }

    // UTILITY METHODS
    fun getCurrentUser() = auth.currentUser

    suspend fun getCurrentUserData(): User? {
        return try {
            val userId = auth.currentUser?.uid ?: return null
            db.collection("users").document(userId).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        auth.signOut()
        oneTapClient.signOut()
        // Microsoft sign-out will be handled when we implement it fully
    }

    suspend fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}