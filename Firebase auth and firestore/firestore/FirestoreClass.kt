package com.sun.todo.firestore
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.sun.todo.RegisterActivity
import com.sun.todo.models.User
import com.sun.todo.utils.Constants

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sun.todo.LoginActivity
import com.sun.todo.UserProfileActivity

class FirestoreClass {
    private val db = FirebaseFirestore.getInstance()

    //fun getDb() = db

    fun registerUser(activity: RegisterActivity, userInfo: User){
        db.collection(Constants.USERS).document(userInfo.id).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener {
                activity.userRegistrationFailed()
            }
    }

    fun updateUser(activity: UserProfileActivity, userHashMap: HashMap<String, Any>){
        db.collection(Constants.USERS).document(FirebaseAuth.getInstance().currentUser!!.uid).update(userHashMap)
            .addOnSuccessListener {
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener {
                activity.profileUpdateFailed()
            }
    }

        fun getCurrentUserID(): String {
            val currentUser = FirebaseAuth.getInstance().currentUser

            var currentUserID = ""
            if (currentUser != null) {
                currentUserID = currentUser.uid
            }
            return currentUserID
        }

        @SuppressLint("CommitPrefEdits")
        fun getUserInfo(activity: Activity) {
            db.collection(Constants.USERS).document(getCurrentUserID()).get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java)!!

                    val sharedPreferences = activity.getSharedPreferences(
                        Constants.YSHOPPING_PREFERENCES,
                        Context.MODE_PRIVATE
                    )
                    val editor = sharedPreferences.edit()
                    editor.putString(
                        Constants.LOGGED_IN_USERNAME,
                        "${user.firstname} ${user.lastname}"
                    )
                    editor.apply()

                    when (activity) {
                        is LoginActivity -> {
                            activity.userLoginSuccess(user)
                        }
                    }
                }
                .addOnFailureListener {
                    when (activity) {
                        is LoginActivity -> {
                            activity.userLoginFailed()
                        }
                    }
                }
        }

        fun uploadImageToCloud(activity: UserProfileActivity, imageFileURI: Uri?) {
            val imageRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                Constants.PROFILE_IMAGE + System.currentTimeMillis() + "." + Constants.getFileExtension(
                    activity,
                    imageFileURI
                )
            )

            imageRef.putFile(imageFileURI!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener {
                        val userHashMap = HashMap<String, Any>()
                        userHashMap[Constants.PROFILE_IMAGE_LOC] = it.toString()

                        updateUser(activity, userHashMap)
                    }
                }
                .addOnFailureListener {
                    activity.profileUpdateFailed()
                }
        }

        fun logoutUser() {
            FirebaseAuth.getInstance().signOut()
        }

    }
