package com.scrubele.scrubeleapp1

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.graphics.Color
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.util.Patterns
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance()

    public override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mBtnSignUp = findViewById<MaterialButton>(R.id.btnSignUp) as Button
        val mBtnSignIn = findViewById<MaterialButton>(R.id.btnSignIn) as Button

        mBtnSignUp.setOnClickListener { launchSignUpActivity() }
        mBtnSignIn.setOnClickListener { signIn() }

    }

    private fun signIn() {
        val emailTxt = findViewById<TextInputLayout>(R.id.emailTxt) as EditText
        val email = emailTxt.text.toString()
        val passwordTxt = findViewById<TextInputLayout>(R.id.passwordTxt) as EditText
        val password = passwordTxt.text.toString()

        val isDataValid = mapOf(
            "emailTxt" to (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()),
            "passwordTxt" to (password.isNotEmpty() && password.matches(".{8,}".toRegex()))
        ).filter { !it.value }

        Log.d("data", isDataValid.toString())

        if (isDataValid.isEmpty()) {
            Log.d("data", "success")
            this.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Successfully signed in :)", Toast.LENGTH_LONG).show()
                    launchWelcomeActivity()

                } else {

                    Toast.makeText(this, "Error signing in :(", Toast.LENGTH_SHORT).show()
                    emailTxt.error = "Input error!"
                    passwordTxt.error = "Input error!"
                }
            }

        } else {
            showDataErrors(isDataValid)
            Toast.makeText(this, "Please fill up the Credentials :|", Toast.LENGTH_SHORT).show()
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
    private fun launchSignUpActivity() {
        val intent = Intent(this, SignUpActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun launchWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

}


