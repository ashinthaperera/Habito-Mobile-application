package com.sun.todo

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.datepicker.MaterialDatePicker

import com.sun.todo.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    lateinit var executor: Executor
    lateinit var biometric_prompt: androidx.biometric.BiometricPrompt
    lateinit var prompt_information: androidx.biometric.BiometricPrompt.PromptInfo

    private lateinit var toolbar:Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        bottomNavigation.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }
    override fun onSupportNavigateUp(): Boolean {
        val navCon= findNavController(R.id.fragmentContainer)
        return navCon.navigateUp()||super.onSupportNavigateUp()
    }

    override fun onRestart() {
        super.onRestart()
        biometrics()
    }

    private fun biometrics(){
        checkBiometricSupport()

        executor = ContextCompat.getMainExecutor(this)

        biometric_prompt = androidx.biometric.BiometricPrompt(this@MainActivity, executor, object: androidx.biometric.BiometricPrompt.AuthenticationCallback(){
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