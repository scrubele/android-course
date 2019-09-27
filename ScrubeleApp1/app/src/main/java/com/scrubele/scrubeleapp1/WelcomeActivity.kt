package com.scrubele.scrubeleapp1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    var user = FirebaseAuth.getInstance().currentUser
    var db = FirebaseFirestore.getInstance()

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
        val userData = db.collection("users").document(user!!.uid)

        userData.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val welcome = "Welcome, " + document.get("name")
                    welcomeTxt.text = welcome

                    Log.d("user", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("user", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("user", "get failed with ", exception)
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
//        finish()
    }
}