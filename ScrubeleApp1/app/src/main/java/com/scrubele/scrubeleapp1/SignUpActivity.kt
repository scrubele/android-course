package com.scrubele.scrubeleapp1

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Patterns
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class SignUpActivity : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()

    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        var mBtnSignUp = findViewById<MaterialButton>(R.id.btnSignUp) as Button
        mBtnSignUp.setOnClickListener { signUpUser() }

    }

    private fun signUpUser() {


        val emailTxt = findViewById<TextInputLayout>(R.id.emailTxt) as EditText
        val passwordTxt = findViewById<TextInputLayout>(R.id.passwordTxt) as EditText
        val nameTxt = findViewById<TextInputLayout>(R.id.nameTxt) as EditText
        val phoneTxt = findViewById<TextInputLayout>(R.id.phoneTxt) as EditText

        val email = emailTxt.text.toString()
        val password = passwordTxt.text.toString()
        val name = nameTxt.text.toString()
        val phone = phoneTxt.text.toString()

//        val notEmpty : (TextView) -> Boolean = {it.text.isNotEmpty()}
        var isDataValid = mapOf(
            "emailTxt" to (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()),
            "passwordTxt" to (password.isNotEmpty() && password.matches(".{8,}".run { toRegex() })),
            "phoneTxt" to (phone.isNotEmpty() && phone.matches("^[+]?[(]?[0-9]{1,4}[)]?[0-9]{9}".run { toRegex() })),
            "nameTxt" to (name.isNotEmpty())
        ).filter { !it.value }

        Log.d("data", isDataValid.toString())

        if (isDataValid.isEmpty()) {

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        val uid = user!!.uid
                        createUser(uid, email, name, phone)
                        Toast.makeText(this, "Successfully registered :)", Toast.LENGTH_LONG).show()
                        signIn(email, password)

                    } else {
                        Toast.makeText(this,"Email is already used.", Toast.LENGTH_LONG).show()

                        emailTxt.error = "Email is already used."
                        passwordTxt.error = "Email is already used."
                        emailTxt.setText("")
                        passwordTxt.setText("")

                    }
                }
        } else {
            showDataErrors(isDataValid)
            Toast.makeText(this, "Please fill up the Credentials :|", Toast.LENGTH_LONG).show()
        }
    }

    private fun signIn(email:String, password:String){
        this.mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Successfully signed in :)",
                        Toast.LENGTH_LONG
                    ).show()
                    launchWelcomeActivity()

                } else {
                    Toast.makeText(this, "Error signing in :(", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
    private fun createUser(
        uid: String,
        email: String,
        name: String,
        phone: String
    ) {
        // Create a new user with a first and last name
        val user = hashMapOf<String, Any>(
            "email" to email,
            "name" to name,
            "phone" to phone
        )
        Log.d("user", user.toString())
        // Add a new document with a generated ID
        db.collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d("user", "DocumentSnapshot added with ID: $uid")
            }
            .addOnFailureListener { e ->
                Log.w("user", "Error adding document", e)
            }


    }

    private fun showDataErrors(isDataValid: Map<String, Boolean>){
        val res = resources
        for ((titleText, v) in isDataValid) {
//                println("$titleText = $v")
            val field =
                findViewById<TextInputLayout>(
                    res.getIdentifier(
                        titleText,
                        "id",
                        packageName
                    )
                ) as EditText
            if (field.text.toString() == "") {
                field.error = "Field is required"
            } else {
                field.error = "Field is incorrect"
                field.setText("")
            }

            field.highlightColor = Color.RED
        }
    }
    private fun launchWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY and Intent.FLAG_ACTIVITY_CLEAR_TASK
                and Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }


}

