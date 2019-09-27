package com.scrubele.scrubeleapp1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btnSignUp.setOnClickListener { signUpUser() }
        btnSignIn.setOnClickListener { launchSignInActivity() }

    }

    private fun signUpUser() {

        val email = emailTxt.text.toString()
        val password = passwordTxt.text.toString()
        val name = nameTxt.text.toString()
        val phone = phoneTxt.text.toString()

        Log.d("user", email)
        val isDataValid = mapOf(
            "emailTxt" to (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()),
            "passwordTxt" to (password.isNotEmpty() && password.matches(".{8,}".run { toRegex() })),
            "phoneTxt" to (phone.isNotEmpty() && phone.matches("^[+]?[(]?[0-9]{1,4}[)]?[0-9]{9}".run { toRegex() })),
            "nameTxt" to (name.isNotEmpty())
        ).filter { !it.value }
        if (isDataValid.isEmpty()) {
            createUser(email, password, name, phone)
        } else {
            showDataErrors(isDataValid)
        }
    }


    private fun createUser(email: String, password: String, name: String, phone: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDB(auth.currentUser!!.uid, email, name, phone)
                    signInUser(email, password)

                } else {
                    showInputErrors()
                }
            }
    }

    private fun signInUser(email: String, password: String) {
        this.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.successfulSignIn), Toast.LENGTH_LONG)
                        .show()
                    launchWelcomeActivity()

                } else {
                    Toast.makeText(this, getString(R.string.errorSignIn), Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun showDataErrors(isDataValid: Map<String, Boolean>) {
        val res = resources
        for ((titleText, v) in isDataValid) {

            val field = findViewById<TextInputLayout>(
                res.getIdentifier(titleText, "id", packageName)
            ) as EditText
            if (field.text.toString() == "") {
                field.error = getString(R.string.requiredField)
            } else {
                field.error = getString(R.string.incorrectField)
                field.setText("")
            }

            field.highlightColor = Color.RED
        }
        Toast.makeText(this, getString(R.string.fillUpCredentials), Toast.LENGTH_LONG).show()
    }

    private fun showInputErrors() {
        Toast.makeText(this, getString(R.string.emailIsUsed), Toast.LENGTH_LONG).show()
        emailTxt.error = getString(R.string.emailIsUsed)
        passwordTxt.error = getString(R.string.emailIsUsed)
        emailTxt.setText("")
        passwordTxt.setText("")
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
            .addOnSuccessListener {
                Log.d("user", "DocumentSnapshot added with ID: $uid")
            }
            .addOnFailureListener { e ->
                Log.w("user", "Error adding document", e)
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
//        finish()
    }

}

