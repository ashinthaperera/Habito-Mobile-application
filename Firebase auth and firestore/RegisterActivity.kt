package com.sun.todo
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.material.textfield.TextInputEditText
import com.sun.todo.firestore.FirestoreClass
import com.sun.todo.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_register)

        //val existing_user_text = findViewById<android.widget.TextView>(R.id.tv_have_account)
        tv_have_account.setOnClickListener {
            val intent = android.content.Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // val reg_button = findViewById<android.widget.Button>(R.id.btn_register)
         btn_register.setOnClickListener{
            if(validateRegisterDetails()){
                showProgressDialog()
                registerUser()
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
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_white_24)
        }

        toolbar.setNavigationOnClickListener{onBackPressed()}
    }

    private fun validateRegisterDetails(): Boolean {
        val fname = findViewById<TextInputEditText>(R.id.et_fullname)
        val uname = findViewById<TextInputEditText>(R.id.et_username)
        val email = findViewById<TextInputEditText>(R.id.et_email)
        val password = findViewById<TextInputEditText>(R.id.et_password)
        val confirm_password = findViewById<TextInputEditText>(R.id.et_confirm_password)


        if(fname.text!!.isEmpty()) {
            fname.error = "First Name is required"
            showErrorSnackbar(fname.error as String, true)
            return false
        }

        if(uname.text!!.isEmpty()) {
            uname.error = "Last Name is required"
            showErrorSnackbar(uname.error as String, true)
            return false
        }

        if(email.text!!.isEmpty()) {
            email.error = "Email is required"
            showErrorSnackbar(email.error as String, true)
            return false
        }

        if(password.text!!.isEmpty()) {
            password.error = "Password is required"
            showErrorSnackbar(password.error as String, true)
            return false
        }

        if(confirm_password.text!!.isEmpty()) {
            confirm_password.error = "Confirm password is required"
            showErrorSnackbar(confirm_password.error as String, true)
            return false
        }

        if(password.text.toString() != confirm_password.text.toString()) {
            confirm_password.error = "Passwords does not match"
            showErrorSnackbar(confirm_password.error as String, true)
            return false
        }


        return true
    }

    private fun registerUser() {
        val fname = findViewById<TextInputEditText>(R.id.et_fullname)
        val uname = findViewById<TextInputEditText>(R.id.et_username)
        val email = findViewById<TextInputEditText>(R.id.et_email)
        val password = findViewById<TextInputEditText>(R.id.et_password)

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!

                    val user = User(
                        firebaseUser.uid,
                        fname.text.toString(),
                        uname.text.toString(),
                        firebaseUser.email!!,
                    )

                    FirestoreClass().registerUser(this,user)

                    FirebaseAuth.getInstance().signOut()
                    finish()
                }else {
                    hideProgressDialog()
                    showErrorSnackbar(task.exception!!.message.toString(), true)
                }
            }
    }

    fun userRegistrationSuccess(){
        hideProgressDialog()
        Toast.makeText(this, "User registration successful", Toast.LENGTH_SHORT).show()
    }

    fun userRegistrationFailed(){
        hideProgressDialog()
        Toast.makeText(this, "User registration failed", Toast.LENGTH_SHORT).show()
    }
}