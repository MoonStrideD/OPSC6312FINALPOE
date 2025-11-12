package com.example.opsc6312finalpoe.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.microsoft.identity.client.*
import com.microsoft.identity.client.exception.MsalException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MicrosoftAuthHelper(private val context: Context) {
    private var publicClientApplication: IPublicClientApplication? = null

    companion object {
        val SCOPES = listOf("User.Read") // Use List instead of Array
    }

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

    suspend fun signIn(activity: Activity): Result<IAuthenticationResult> {
        return suspendCancellableCoroutine { continuation ->
            try {
                // Get current accounts first
                val currentAccounts = publicClientApplication?.getCurrentAccount()?.let { listOf(it) } ?: emptyList()

                if (currentAccounts.isNotEmpty()) {
                    // Silent sign-in if account exists
                    val silentParameters = AcquireTokenSilentParameters.Builder()
                        .fromAuthority("https://login.microsoftonline.com/common")
                        .withScopes(SCOPES)
                        .forAccount(currentAccounts[0])
                        .fromAuthority("https://login.microsoftonline.com/common")
                        .build()

                    publicClientApplication?.acquireTokenSilentAsync(
                        silentParameters,
                        object : AuthenticationCallback {
                            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                                Log.d("MicrosoftAuth", "Silent sign-in successful")
                                continuation.resume(Result.success(authenticationResult))
                            }
                            override fun onError(exception: MsalException) {
                                Log.e("MicrosoftAuth", "Silent sign-in failed: ${exception.message}")
                                // Fall back to interactive sign-in
                                performInteractiveSignIn(activity, continuation)
                            }
                        }
                    )
                } else {
                    // Interactive sign-in
                    performInteractiveSignIn(activity, continuation)
                }
            } catch (e: Exception) {
                Log.e("MicrosoftAuth", "Sign-in error: ${e.message}")
                performInteractiveSignIn(activity, continuation)
            }
        }
    }

    private fun performInteractiveSignIn(
        activity: Activity,
        continuation: kotlin.coroutines.Continuation<Result<IAuthenticationResult>>
    ) {
        try {
            val parameters = AcquireTokenParameters.Builder()
                .startAuthorizationFromActivity(activity)
                .withScopes(SCOPES)
                .withPrompt(Prompt.LOGIN)
                .fromAuthority("https://login.microsoftonline.com/common")
                .withCallback(object : AuthenticationCallback {
                    override fun onSuccess(authenticationResult: IAuthenticationResult) {
                        Log.d("MicrosoftAuth", "Interactive sign-in successful")
                        continuation.resume(Result.success(authenticationResult))
                    }

                    override fun onError(exception: MsalException) {
                        Log.e("MicrosoftAuth", "Interactive sign-in failed: ${exception.message}")
                        continuation.resume(Result.failure(exception))
                    }

                    override fun onCancel() {
                        Log.d("MicrosoftAuth", "Sign-in cancelled by user")
                        continuation.resume(Result.failure(Exception("User cancelled sign-in")))
                    }
                })
                .build()

            publicClientApplication?.acquireToken(parameters)
        } catch (e: Exception) {
            Log.e("MicrosoftAuth", "Interactive sign-in setup failed: ${e.message}")
            continuation.resume(Result.failure(e))
        }
    }

    suspend fun signOut(): Result<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            try {
                val currentAccount = publicClientApplication?.getCurrentAccount()
                if (currentAccount != null) {
                    publicClientApplication?.signOut(
                        object : ISingleAccountPublicClientApplication.SignOutCallback {
                            override fun onSignOut() {
                                Log.d("MicrosoftAuth", "Sign out successful")
                                continuation.resume(Result.success(true))
                            }

                            override fun onError(exception: MsalException) {
                                Log.e("MicrosoftAuth", "Sign out failed: ${exception.message}")
                                continuation.resume(Result.failure(exception))
                            }
                        }
                    )
                } else {
                    continuation.resume(Result.success(true))
                }
            } catch (e: Exception) {
                Log.e("MicrosoftAuth", "Sign out error: ${e.message}")
                continuation.resume(Result.failure(e))
            }
        }
    }

    fun getCurrentAccount(): IAccount? {
        return try {
            publicClientApplication?.getCurrentAccount()
        } catch (e: Exception) {
            null
        }
    }
}