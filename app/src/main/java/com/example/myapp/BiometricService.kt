package com.example.myapp

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.myapp.R
import com.example.myapp.utils.str

private const val TAG = "BiometricService"

fun isBiometricAvailable(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)

    return when (
        biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL,
        )
    ) {
        BiometricManager.BIOMETRIC_SUCCESS -> {
            Log.d(TAG, context.getString(R.string.PIN_ready))
            true
        }

        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
            Log.e(TAG, context.getString(R.string.biometric_not_found))
            false
        }

        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
            Log.e(TAG, context.getString(R.string.no_hardware))
            false
        }

        else -> {
            Log.e(TAG, context.getString(R.string.unavailable))
            false
        }
    }
}

fun showBiometricPrompt(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onError: () -> Unit,
    onFailed: () -> Unit,
) {
    val executor = ContextCompat.getMainExecutor(activity)

    val biometricPrompt =
        BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d(TAG, "Authentication success ✅")
                    onSuccess()
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence,
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.e(TAG, "Authentication error ❌: $errString")
                    onError()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.e(TAG, "Authentication failed ❌")
                    onFailed()
                }
            },
        )

    val promptInfo =
        BiometricPrompt.PromptInfo
            .Builder()
            .setTitle("Secure Login")
            .setSubtitle("Use fingerprint or device PIN/password")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL,
            ).build()

    Log.d(TAG, "Launching biometric prompt 🚀")

    biometricPrompt.authenticate(promptInfo)
}
