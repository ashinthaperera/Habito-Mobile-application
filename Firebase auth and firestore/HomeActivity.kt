package com.sun.todo

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sun.todo.databinding.ActivityHomeBinding
import java.util.concurrent.Executor

class HomeActivity : AppCompatActivity() {

    lateinit var executor: Executor
    lateinit var biometric_prompt: androidx.biometric.BiometricPrompt
    lateinit var prompt_information: androidx.biometric.BiometricPrompt.PromptInfo

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

    override fun onRestart() {
        super.onRestart()
        biometrics()
    }

    private fun biometrics(){
        checkBiometricSupport()

        executor = ContextCompat.getMainExecutor(this)

        biometric_prompt = androidx.biometric.BiometricPrompt(this@HomeActivity, executor, object: androidx.biometric.BiometricPrompt.AuthenticationCallback(){
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