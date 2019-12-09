package com.scrubele.scrubeleapp1.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.scrubele.scrubeleapp1.R
import com.scrubele.scrubeleapp1.activities.ChangePasswordActivity
import com.scrubele.scrubeleapp1.activities.MainActivity
import com.scrubele.scrubeleapp1.adapters.FirebaseAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.profile_fragment.*
import java.io.IOException

class ProfileFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private var user = auth.currentUser!!
    private var firebaseFirestore: FirebaseFirestore? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null

    private companion object {
        const val PHONE_PATTERN = "^[+]?[(]?[0-9]{1,4}[)]?[0-9]{9}"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        btnSignOut.setOnClickListener { signOut() }
        btnUpdateProfile.setOnClickListener { updateProfileData() }
        btnChangePassword.setOnClickListener { launchChangePasswordActivity() }
        profile_photo.setOnClickListener { launchGallery() }
        loadUserData()
        FirebaseAdapter.getPhoto { imagePath -> loadPhoto(imagePath) }
    }

    private fun loadUserData() {
        FirebaseAdapter.getPhoneNumber { phoneNumber -> profile_phone.setText(phoneNumber) }
        val userName = user.displayName.toString()
        val userEmail = user.email.toString()
        profile_name.setText(userName)
        profile_email.setText(userEmail)
    }

    private fun loadPhoto(photo: String) {
        val photoUrl = storageReference?.child(photo)
        photoUrl?.downloadUrl?.addOnSuccessListener { photoUrl ->
            Picasso
                .get()
                .load(photoUrl)
                .placeholder(R.drawable.common_google_signin_btn_icon_dark)
                .into(profile_photo)
        }
        Toast.makeText(this.activity, getString(R.string.photo_is_uploaded), Toast.LENGTH_LONG)
    }


    private fun addPhotoURL(uri: String) {
        val userData = hashMapOf<String, Any>(
            "photoURL" to uri
        )
        firebaseFirestore?.collection("users")?.document(auth.currentUser!!.uid)?.update(userData)
    }

    private fun isDataValid(): Boolean {
        val email = profile_email.text.toString()
        val name = profile_name.text.toString()
        val phone = profile_phone.text.toString()
        val invalidData = findInvalidData(email, name, phone)
        showDataErrors(invalidData)
        return invalidData.isEmpty()
    }

    private fun findInvalidData(
        email: String,
        name: String,
        phone: String
    ): Map<String, Boolean> {
        return mapOf(
            "emailTxt" to (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()),
            "phoneTxt" to (phone.isNotEmpty() && phone.matches(PHONE_PATTERN.run { toRegex() })),
            "nameTxt" to (name.isNotEmpty())
        ).filter { !it.value }
    }

    private fun showDataErrors(isDataValid: Map<String, Boolean>) {
        val res = resources
        for ((titleText: String) in isDataValid) {
            val field = activity?.findViewById<TextInputLayout>(
                res.getIdentifier(titleText, "id", activity!!.packageName)
            ) as EditText
            if (field.text.toString().isEmpty()) {
                field.error = getString(R.string.requiredField)
            } else {
                field.text?.clear()
                field.error = getString(R.string.incorrectField)
            }
            field.highlightColor = Color.RED
        }
    }

    private fun updateProfileData() {
        val email = profile_email.text.toString()
        val name = profile_name.text.toString()
        val phone = profile_phone.text.toString()
        val isDataValid = isDataValid()
        if (isDataValid) {
            FirebaseAdapter.run {
                updateEmail(activity, email)
                updateFirestoreData(email, name, phone)
                uploadProfilePhoto(activity) { imagePath ->
                    addPhotoURL(imagePath)
                }
                updateProfile(name)
            }
            Toast.makeText(
                this.activity,
                getString(R.string.successfully_update_profile),
                Toast.LENGTH_LONG
            )
        }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, filePath)
                profile_photo.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        launchMainActivity()
    }

    private fun launchMainActivity() {
        val intent = Intent(this.activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun launchChangePasswordActivity() {
        val intent = Intent(activity, ChangePasswordActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}