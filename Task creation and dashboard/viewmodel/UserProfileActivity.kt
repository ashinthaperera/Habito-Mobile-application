package com.sun.todo
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout

import com.sun.todo.firestore.FirestoreClass
import com.sun.todo.models.User
import com.sun.todo.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.sun.todo.utils.GlideLoader
import com.sun.todo.utils.YSButton
import com.sun.todo.utils.YSEditText
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class UserProfileActivity : BaseActivity() {
    private var mUserDetails: User = User()
    private var mSelectedImageFileUri: Uri? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if (intent.hasExtra(Constants.USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.USER_DETAILS)!!
        }

        val fname = findViewById<YSEditText>(R.id.fname_text)
        fname.setText(mUserDetails.firstname)
        val lname = findViewById<YSEditText>(R.id.et_username)
        lname.setText(mUserDetails.lastname)
        val email = findViewById<YSEditText>(R.id.et_email)
        email.setText(mUserDetails.email)
        email.isEnabled = false


        Glide.with(this)
            .load(mUserDetails.image)
            .into(findViewById(R.id.profile_image))


        //val profile_image = findViewById<android.widget.ImageView>(R.id.profile_image)

        profile_image.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                requestExternalStoragePermission()
            }
        }

        //val btn_save = findViewById<YSButton>(R.id.btn_save)
        btn_save.setOnClickListener {
            if (validateUserProfileDetails()){
                saveDetails()
            }
        }
    }

    @SuppressLint("UseSupportActionBar")
    private fun setActionBar(){
        val toolbar = ActivityCompat.requireViewById<Toolbar>(this, R.id.toolbar)
        setActionBar(toolbar)

        val actionBar = actionBar

        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_white_24)
        }

        toolbar.setNavigationOnClickListener{onBackPressed()}
    }

    private fun requestExternalStoragePermission(){
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            Constants.READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted.
                showErrorSnackbar("Read Storage Permission granted", false)
                Constants.showImageChooser(this)
            } else {
                // Permission is denied.
                showErrorSnackbar("Oops you just denied read storage permission. You can enable it from the settings", true)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data != null && data.data != null){
                try {
                    mSelectedImageFileUri = data.data!!
                    val profile_image = findViewById<android.widget.ImageView>(R.id.profile_image)
                    GlideLoader(this).loadUserImage(mSelectedImageFileUri!!, profile_image)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "User Image Selection Failed!", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "User Image Selection Failed!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "User Image Selection Failed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateUserProfileDetails(): Boolean{
        val fname = findViewById<TextInputLayout>(R.id.fname_text)
        val lname = findViewById<TextInputLayout>(R.id.et_username)


        if(fname.toString().isEmpty()){
            fname.error = "First Name is required"
            showErrorSnackbar(fname.error.toString(), true)
            return false
        }
        if(lname.toString().isEmpty()){
            lname.error = "Last Name is required"
            showErrorSnackbar(lname.error.toString(), true)
            return false
        }
        /*if(phone.text.toString().isEmpty()){
            phone.error = "Phone Number is required"
            showErrorSnackbar(phone.error.toString(), true)
            return false
        }
        if(phone.text.toString().length < 10 || phone.text.toString().length > 10){
            phone.error = "Phone Number should be 10 digits"
            showErrorSnackbar(phone.error.toString(), true)
            return false
        }*/
        return true
    }

    private fun saveDetails(){
        val userHashMap = HashMap<String, Any>()

        val fname = findViewById<TextInputLayout>(R.id.fname_text)
        val lname = findViewById<TextInputLayout>(R.id.et_username)



        userHashMap[Constants.FNAME] = fname.toString()
        userHashMap[Constants.LNAME] = lname.toString()

        userHashMap[Constants.PROFILE_COMPLETED] = 1

        showProgressDialog()

        if (mSelectedImageFileUri!=null){FirestoreClass().uploadImageToCloud(this, mSelectedImageFileUri)}
        FirestoreClass().updateUser(this, userHashMap)
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        showErrorSnackbar("Profile updated successfully", false)
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    fun profileUpdateFailed(){
        hideProgressDialog()
        showErrorSnackbar("Profile update failed", true)
        Log.e("Error", "Error updating user information")
    }
}