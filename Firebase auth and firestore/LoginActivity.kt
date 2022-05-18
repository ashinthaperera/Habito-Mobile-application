package com.sun.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.sun.todo.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.sun.todo.firestore.FirestoreClass
import com.sun.todo.fragments.UserProfileFragment
import com.sun.todo.models.User
import com.sun.todo.utils.Constants
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity()  {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)



       tv_havent_account.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)

        }

        btn_login.setOnClickListener {
            if(validateLoginDetails()){
                showProgressDialog()
                logUser()
            }
        }


    }

    private fun validateLoginDetails(): Boolean {
        val email = findViewById<TextInputEditText>(R.id.et_email)
        val password = findViewById<TextInputEditText>(R.id.et_password)
        if(email.text.toString().isEmpty()){
            email.error = "Email is required"
            showErrorSnackbar(email.error as String, true)
            return false
        }
        if(password.text.toString().isEmpty()){
            password.error = "Password is required"
            showErrorSnackbar(password.error as String, true)
            return false
        }
        return true
    }


    private fun logUser() {
        val email = findViewById<TextInputEditText>(R.id.et_email)
        val password = findViewById<TextInputEditText>(R.id.et_password)

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    FirestoreClass().getUserInfo(this)
                }else {
                    hideProgressDialog()
                    showErrorSnackbar(task.exception!!.message.toString(), true)
                }
            }
    }

    fun userLoginSuccess(user: User){
        hideProgressDialog()

        Toast.makeText(this, "User login successful", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra(Constants.USER_DETAILS, user)
            startActivity(intent)
            this.finish()

    }


    fun userLoginFailed(){
        hideProgressDialog()
        Toast.makeText(this, "User login failed", Toast.LENGTH_SHORT).show()
    }
}