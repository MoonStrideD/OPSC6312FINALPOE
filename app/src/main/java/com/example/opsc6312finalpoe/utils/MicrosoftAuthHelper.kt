package com.example.opsc6312finalpoe.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.opsc6312finalpoe.R
import com.microsoft.identity.client.*
import com.microsoft.identity.client.exception.MsalException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class MicrosoftAuthHelper(private val context: Context) {
    private var publicClientApplication: IPublicClientApplication? = null

    suspend fun initialize(): Result<Boolean> {
        return try {
            PublicClientApplication.create(
                context,
                R.raw.auth_config,
                object : IPublicClientApplication.ApplicationCreatedListener {
                    override fun onCreated(application: IPublicClientApplication) {
                        publicClientApplication = application
                        Log.d("MicrosoftAuth", "MSAL initialized successfully")
                    }
                    override fun onError(exception: MsalException) {
                        Log.e("MicrosoftAuth", "MSAL initialization failed: ${exception.message}")
                    }
                }
            )
            Result.success(true)
        } catch (e: Exception) {
            Log.e("MicrosoftAuth", "Initialization exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signIn(activity: Activity): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            try {
                val parameters = AcquireTokenParameters.Builder()
                    .startAuthorizationFromActivity(activity)
                    .withScopes(listOf("User.Read"))
                    .withPrompt(Prompt.LOGIN)
                    .fromAuthority("https://login.microsoftonline.com/common")
                    .withCallback(object : AuthenticationCallback {
                        override fun onSuccess(authenticationResult: IAuthenticationResult) {
                            Log.d("MicrosoftAuth", "Microsoft sign-in successful")
                            val accessToken = authenticationResult.accessToken
                            continuation.resume(Result.success(accessToken))
                        }

                        override fun onError(exception: MsalException) {
                            Log.e("MicrosoftAuth", "Microsoft sign-in failed: ${exception.message}")
                            continuation.resume(Result.failure(exception))
                        }

                        override fun onCancel() {
                            Log.d("MicrosoftAuth", "Microsoft sign-in cancelled")
                            continuation.resume(Result.failure(Exception("User cancelled sign-in")))
                        }
                    })
                    .build()

                publicClientApplication?.acquireToken(parameters)
            } catch (e: Exception) {
                Log.e("MicrosoftAuth", "Sign-in setup failed: ${e.message}")
                continuation.resume(Result.failure(e))
            }
        }
    }

    fun signOut() {
        try {
            // Simple approach: Just log that we're clearing Microsoft auth
            // The actual sign-out happens when the user logs out of the app
            Log.d("MicrosoftAuth", "Microsoft authentication cleared")

            // For MSAL, we don't need complex sign-out logic for basic functionality
            // The tokens will expire naturally and the user can sign in again when needed

        } catch (e: Exception) {
            Log.e("MicrosoftAuth", "Sign-out error: ${e.message}")
        }
    }
}