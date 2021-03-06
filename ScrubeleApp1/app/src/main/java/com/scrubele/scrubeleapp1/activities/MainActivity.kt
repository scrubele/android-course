package com.scrubele.scrubeleapp1.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.scrubele.scrubeleapp1.R
import com.scrubele.scrubeleapp1.Utils.ErrorChecker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var auth = FirebaseAuth.getInstance()

    public override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnSignUp.setOnClickListener { launchSignUpActivity() }
        btnSignIn.setOnClickListener { signInUser() }
    }

    private fun signInUser() {
        val email = emailTxt.text.toString()
        val password = passwordTxt.text.toString()
        val invalidData = ErrorChecker.findInvalidData(email = email, password = password)
        if (invalidData.isEmpty()) {
            authenticateUser(email, password)
        } else {
            showDataErrors(invalidData)
        }
    }

    private fun authenticateUser(email: String, password: String) {
        this.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
            this
        ) { task ->
            handleUserAuthentication(task)
        }
    }

    private fun handleUserAuthentication(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            Toast.makeText(this, getString(R.string.successfulSignIn), Toast.LENGTH_LONG).show()
            launchTabActivity()
        } else {
            showInputErrors()
        }
    }

    private fun showDataErrors(isDataValid: Map<String, Boolean>) {
        val res = resources
        for ((titleText: String) in isDataValid) {
            val field = findViewById<TextInputLayout>(
                res.getIdentifier(titleText, "id", packageName)
            ) as EditText
            if (field.text.toString().isEmpty()) {
                field.error = getString(R.string.requiredField)
            } else {
                field.text?.clear()
                field.error = getString(R.string.incorrectField)
            }
            field.highlightColor = Color.RED
        }
        Toast.makeText(this, getString(R.string.fillUpCredentials), Toast.LENGTH_LONG).show()
    }

    private fun showInputErrors() {
        emailTxt.text?.clear()
        passwordTxt.text?.clear()
        emailTxt.error = getString(R.string.incorrectInput)
        passwordTxt.error = getString(R.string.incorrectInput)
        Toast.makeText(this, getString(R.string.incorrectInput), Toast.LENGTH_LONG).show()
    }

    private fun launchSignUpActivity() {
        val intent = Intent(this, EntryActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun launchTabActivity() {
        val intent = Intent(this, TabActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}