package com.sun.todo

import android.app.Dialog
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.Executor

open class BaseActivity : AppCompatActivity() {

    lateinit var executor: Executor
    lateinit var biometric_prompt: androidx.biometric.BiometricPrompt
    lateinit var prompt_information: androidx.biometric.BiometricPrompt.PromptInfo

    private var doubleBackToExitPressedOnce = false

    private lateinit var yProgressDialog: Dialog

    fun showErrorSnackbar(message: String, errorMessage: Boolean) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view

        if (errorMessage) {
            sbView.setBackgroundColor(ContextCompat.getColor(this,R.color.colorSnackBarError))
        }else{
            sbView.setBackgroundColor(ContextCompat.getColor(this,R.color.colorSnackBarSuccess))
        }
        snackbar.show()
    }

    fun showProgressDialog() {
        yProgressDialog = Dialog(this)
        yProgressDialog.setContentView(R.layout.dialog_progress)
        yProgressDialog.setCancelable(false)
        yProgressDialog.setCanceledOnTouchOutside(false)
        yProgressDialog.show()
    }

    fun hideProgressDialog() {
        yProgressDialog.dismiss()
    }

    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        // Handler code should be executed after 1 second.
        @Suppress("DEPRECATION") val mHandler = Handler()
        mHandler.postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onRestart() {
        super.onRestart()
        biometrics()
    }

    private fun biometrics(){
        checkBiometricSupport()

        executor = ContextCompat.getMainExecutor(this)

        biometric_prompt = androidx.biometric.BiometricPrompt(this@BaseActivity, executor, object: androidx.biometric.BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                // If any error comes...
                notifyUser("Error...! $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                notifyUser("Authenticated Successfully...!!!")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                notifyUser("Authentication Failed...!!!")
            }
        })

        //Setup title, subtitle, and description  on authentication dialog.
        prompt_information = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Please Login using Fingerprint or Face")
            .setNegativeButtonText("Cancel")
            .build()

        //Set click event on buttons.
        biometric_prompt.authenticate(prompt_information)

    }

    private fun checkBiometricSupport(): Boolean {

        val keyguardManager: KeyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keyguardManager.isKeyguardSecure){
            notifyUser("Fingerprint Authentication has not been enabled in settings.")
            return false
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED){
            notifyUser("Fingerprint Authentication permission is not enabled.")
            return false
        }

        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){
            true
        } else true
    }

    private fun notifyUser(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}