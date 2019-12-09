package com.scrubele.scrubeleapp1.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.scrubele.scrubeleapp1.R
import kotlinx.android.synthetic.main.activity_entry.*


class EntryActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private companion object {
        const val PASSWORD_PATTERN = ".{8,}"
        const val PHONE_PATTERN = "^[+]?[(]?[0-9]{1,4}[)]?[0-9]{9}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        btnSignUp.setOnClickListener { signUpUser() }
        btnSignIn.setOnClickListener { launchSignInActivity() }
    }

    private fun signUpUser() {
        val email = emailTxt.text.toString()
        val password = passwordTxt.text.toString()
        val name = nameTxt.text.toString()
        val phone = phoneTxt.text.toString()
        val invalidData = findInvalidData(email, password, name, phone)
        if (invalidData.isEmpty()) {
            createUser(email, password, name, phone)
        } else {
            showDataErrors(invalidData)
        }
    }

    private fun findInvalidData(
        email: String,
        password: String,
        name: String,
        phone: String
    ): Map<String, Boolean> {
        return mapOf(
            "emailTxt" to (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()),
            "passwordTxt" to (password.isNotEmpty() && password.matches(PASSWORD_PATTERN.run { toRegex() })),
            "phoneTxt" to (phone.isNotEmpty() && phone.matches(PHONE_PATTERN.run { toRegex() })),
            "nameTxt" to (name.isNotEmpty())
        ).filter { !it.value }
    }

    private fun createUser(email: String, password: String, name: String, phone: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                handleUserCreating(task, email, name, phone)
            }
    }

    private fun handleUserCreating(
        task: Task<AuthResult>,
        email: String,
        name: String,
        phone: String
    ) {
        if (task.isSuccessful) {
            addUserToDB(auth.currentUser!!.uid, email, name, phone)
            authenticateUser(name)
        } else {
            showInputErrors()
        }
    }

    private fun authenticateUser(name: String) {
        val user = auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name).build()
        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                handleUserAuthentication(user, task)
            }
    }

    private fun handleUserAuthentication(user: FirebaseUser, task: Task<Void>) {
        if (task.isSuccessful) {
            Toast.makeText(
                this, getString(R.string.successfulSignUp) + "," + user.displayName,
                Toast.LENGTH_LONG
            ).show()
            launchWelcomeActivity()
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
        Toast.makeText(this, getString(R.string.errorSignUp), Toast.LENGTH_SHORT)
            .show()
        emailTxt.text?.clear()
        passwordTxt.text?.clear()
        emailTxt.error = getString(R.string.incorrectInput)
        passwordTxt.error = getString(R.string.incorrectInput)
        Toast.makeText(this, getString(R.string.incorrectInput), Toast.LENGTH_LONG).show()
    }

    private fun addUserToDB(uid: String, email: String, name: String, phone: String) {
        val user = hashMapOf<String, Any>(
            "email" to email,
            "name" to name,
            "phone" to phone
        )
        db.collection("users")
            .document(uid)
            .set(user)
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
            }
        Toast.makeText(this, getString(R.string.successfulSignUp), Toast.LENGTH_LONG).show()
    }

    private fun launchWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY and Intent.FLAG_ACTIVITY_CLEAR_TASK
                    and Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP
        )
        startActivity(intent)
        finish()
    }

    private fun launchSignInActivity() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY and Intent.FLAG_ACTIVITY_NEW_TASK and
                    Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP
        )
        startActivity(intent)
    }
}