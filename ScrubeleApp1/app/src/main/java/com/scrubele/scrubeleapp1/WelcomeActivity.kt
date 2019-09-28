package com.scrubele.scrubeleapp1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    private var user = FirebaseAuth.getInstance().currentUser
    private var collectionReference = FirebaseFirestore.getInstance().collection("users")

    public override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        showWelcome()
        btnSignOut.setOnClickListener { launchSignInActivity() }
    }

    private fun showWelcome() {
        val userData = collectionReference.document(user!!.uid)
        userData.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val welcome = "Welcome, " + document.get("name")
                    welcomeTxt.text = welcome
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
            }
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