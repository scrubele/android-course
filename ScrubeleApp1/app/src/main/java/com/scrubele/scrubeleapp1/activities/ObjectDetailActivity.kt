package com.scrubele.scrubeleapp1.activities

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.scrubele.scrubeleapp1.R
import com.scrubele.scrubeleapp1.models.ProtectedObjectModel
import com.scrubele.scrubeleapp1.retrofit.ApiClient
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.list_item_detailed.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        if(intent.hasExtra("message")){
            var messages = intent.getStringExtra("message")
            var values = messages.split(" ")
            var id = values[0].toInt()
            var message = values[1]

            val call = ApiClient.getClient.getProtectedObject(id)
            call.enqueue(object : Callback<ProtectedObjectModel> {
                override fun onFailure(call: Call<ProtectedObjectModel>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<ProtectedObjectModel>,
                    response: Response<ProtectedObjectModel>
                ) {
                    val protectedObject = response.body()
                    setObject(protectedObject)
                    Toast.makeText(
                        this@ObjectDetailActivity,
                        getString(R.string.sucessfully_posted),
                        Toast.LENGTH_LONG)
                }

            })
            showDialog(message)
        }
    }

    private fun setObject(protectedObjectModel: ProtectedObjectModel?){
        detailed_object_name.text = protectedObjectModel?.name
        detailed_object_description.text = protectedObjectModel?.description
        detailed_object_size.text = protectedObjectModel?.size
        val photo = protectedObjectModel?.photo
        Picasso
            .get()
            .load(photo)
            .into(detailed_object_photo)
    }

    private fun showDialog(message: String) {
        val dialog = AlertDialog.Builder(this).create()

        dialog.setTitle(message)

        dialog.setButton(
            DialogInterface.BUTTON_POSITIVE, "Okay"
        ) { _, _ -> }
        dialog.show()
    }
}