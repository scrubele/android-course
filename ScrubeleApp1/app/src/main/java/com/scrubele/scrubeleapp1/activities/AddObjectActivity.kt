package com.scrubele.scrubeleapp1.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.scrubele.scrubeleapp1.R
import com.scrubele.scrubeleapp1.models.ProtectedObjectModel
import com.scrubele.scrubeleapp1.retrofit.ApiClient
import kotlinx.android.synthetic.main.activity_add_object.*
import kotlinx.android.synthetic.main.app_bar.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException


class AddObjectActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_object)
        setToolbar()
        addObjectPhoto.setOnClickListener { launchGallery() }
        addBtn.setOnClickListener { postData() }
        progressBar.visibility = View.INVISIBLE
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.setNavigationOnClickListener {
            launchTabActivity()
        }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                getString(R.string.select_picture)
            ), PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                addObjectPhoto.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun Uri.getPathString(context: Context): String {
        var path = ""
        context.contentResolver.query(
            this, arrayOf(MediaStore.Images.Media.DATA),
            null, null, null
        )?.apply {
            val columnIndex = getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            moveToFirst()
            path = getString(columnIndex)
            close()
        }
        return path
    }


    private fun postData() {
        val fullPath = filePath?.getPathString(applicationContext)
        val file = File(fullPath)
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val image = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        val name =
            RequestBody.create(MediaType.parse("text/plain"), addName.text.toString())
        val description =
            RequestBody.create(MediaType.parse("text/plain"), addDescription.text.toString())
        val size =
            RequestBody.create(MediaType.parse("text/plain"), addSize.text.toString())
        val call = ApiClient.getClient.addProtectedObject(name, description, size, image)
        call.enqueue(object : Callback<ProtectedObjectModel> {
            override fun onFailure(call: Call<ProtectedObjectModel>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<ProtectedObjectModel>,
                response: Response<ProtectedObjectModel>
            ) {
                setProgressBar()
                Toast.makeText(
                    this@AddObjectActivity,
                    getString(R.string.sucessfully_posted),
                    Toast.LENGTH_LONG
                )
                launchTabActivity()
            }

        })к
        setProgressBar()

    }

    private fun setProgressBar() = when {
        progressBar.visibility == View.VISIBLE -> progressBar.visibility = View.INVISIBLE
        else -> progressBar.visibility = View.VISIBLE
    }

    private fun launchTabActivity() {
        startActivity(Intent(applicationContext, TabActivity::class.java))
        finish()
    }
}