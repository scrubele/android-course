package com.scrubele.scrubeleapp1.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.scrubele.scrubeleapp1.R
//import com.scrubele.scrubeleapp1.fragments.ListActivity
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    private var user = FirebaseAuth.getInstance().currentUser!!

    public override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        showWelcome()
        launchSignInActivity()
        btnSignOut.setOnClickListener { launchSignInActivity() }
    }

    private fun showWelcome() {
        val welcome = resources.getString(R.string.welcome) + user.displayName
        welcomeTxt.text = welcome
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