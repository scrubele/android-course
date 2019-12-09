package com.scrubele.scrubeleapp1.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.scrubele.scrubeleapp1.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.list_item_detailed.*

class ObjectDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_item_detailed)
        setToolbar()
        setObject()
    }

    private fun setToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(applicationContext, TabActivity::class.java))
            finish()
        }
    }
    private fun setObject() {
        if (intent.hasExtra("name")) {
            detailed_object_name.text = intent.getStringExtra("name")
            detailed_object_description.text = intent.getStringExtra("description")
            detailed_object_size.text = intent.getStringExtra("size")
            val photo = intent.getStringExtra("photo")
            Picasso
                .get()
                .load(photo)
                .into(detailed_object_photo)
        }
    }
}