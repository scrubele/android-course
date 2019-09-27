package com.scrubele.scrubeleapp1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WelcomeActivity : AppCompatActivity() {

    var mAuth = FirebaseAuth.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    var db = FirebaseFirestore.getInstance()

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
//        updateUI(currentUser)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        UpdateUI()
        var mBtnSignOut = findViewById<MaterialButton>(R.id.btnSignOut) as Button
        mBtnSignOut.setOnClickListener { launchSignInActivity() }
    }

    private fun UpdateUI(){
        val welcomeTxt = findViewById<TextView>(R.id.welcomeTxt) as TextView
        getUserData(welcomeTxt)
    }

    private fun getUserData(welcomeTxt:TextView){
        val userData = db.collection("users").document(user!!.uid)
        userData.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    welcomeTxt.text = "Welcome, "+document.get("name")
                    Log.d("user", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("user", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("user", "get failed with ", exception)
            }

    }
    private fun launchSignInActivity(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY and Intent.FLAG_ACTIVITY_NEW_TASK  and
                Intent.FLAG_ACTIVITY_CLEAR_TASK)

        startActivity(intent)
        finish()
    }
}