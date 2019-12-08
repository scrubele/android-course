package com.scrubele.scrubeleapp1.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.scrubele.scrubeleapp1.R
import com.scrubele.scrubeleapp1.activities.ChangePasswordActivity
import com.scrubele.scrubeleapp1.activities.MainActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.profile_fragment.*
import java.io.IOException
import java.util.*

class ProfileFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private var user = auth.currentUser!!
    private val db = FirebaseFirestore.getInstance()
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private companion object {
        const val PASSWORD_PATTERN = ".{8,}"
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
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        btnSignOut.setOnClickListener { signOut() }
        btnUpdateProfile.setOnClickListener { updateProfileData() }
        btnChangePassword.setOnClickListener { launchChangePasswordActivity() }
        profile_photo.setOnClickListener { launchGallery() }
        loadUserData()
    }

    private fun loadUserData() {
        getPhoneNumber()
        val userName = user.displayName.toString()
        val userEmail = user.email.toString()
        profile_name.setText(userName)
        profile_email.setText(userEmail)
        setPhoto()
    }

    private fun getPhoneNumber(): String {
        val dataRef = db.collection("users").document(auth.currentUser!!.uid)
        var phoneNumber = ""
        dataRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    phoneNumber = document.getString("phone") as String
                    profile_phone.setText(phoneNumber)
                }
            }
            .addOnFailureListener { exception ->
            }
        return phoneNumber
    }

    private fun getPhotoURL(): String {
        val dataRef = db.collection("users").document(auth.currentUser!!.uid)
        var photo = ""
        dataRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    photo = document.getString("photoURL") as String
                }
            }
            .addOnFailureListener { exception ->
            }
        return photo
    }

    private fun setPhoto(){
        val photoUrl = firebaseStore?.getReference(getPhotoURL())!!.downloadUrl.toString()
        if (photoUrl.isNotEmpty()) {
            Picasso
                .get()
                .load(photoUrl)
                .into(profile_photo)m

            Toast.makeText(this.activity, getString(R.string.photo_is_uploaded), Toast.LENGTH_LONG)
        }
    }

    private fun addPhotoURL(uri: String){
        val userData = hashMapOf<String, Any>(
            "photoURL" to uri
        )
        db.collection("users").document(auth.currentUser!!.uid).update(userData)
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
        Toast.makeText(this.activity, getString(R.string.fillUpCredentials), Toast.LENGTH_LONG)
            .show()
    }

    private fun updateProfileData() {
        val email = profile_email.text.toString()
        val name = profile_name.text.toString()
        val phone = profile_phone.text.toString()
        val isDataValid = isDataValid()
        if (isDataValid) {
            updateEmail(email)
            updateFirestoreData(email, name, phone)
            uploadProfilePhoto{ imagePath ->
                addPhotoURL(imagePath) }
            updateProfile(name, "")
        }
    }

    private fun updateEmail(newEmail: String) {
        user.updateEmail(newEmail)
            .addOnCompleteListener { task1 ->
                if (task1.isSuccessful) {
                    Toast.makeText(
                        context, getString(R.string.email_is_updated),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun updateProfile(newName: String, uri: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()
        user.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("data", "User profile updated.")
            }
        }
    }

    private fun updateProfilePhoto (uri: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(Uri.parse(uri))
            .build()
        user.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("data", "User profile updated.")
            }
        }
    }

    private fun updateFirestoreData(email: String, name: String, phone: String) {
        val userData = hashMapOf<String, Any>(
            "email" to email,
            "name" to name,
            "phone" to phone
        )
        db.collection("users").document(auth.currentUser!!.uid).update(userData)
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
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

    private fun uploadProfilePhoto(onSuccessListener: (imagePath: String) -> Unit) {
        val progress = ProgressDialog(this.activity).apply {
            setTitle("Uploading Picture....")
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }
        val data = FirebaseStorage.getInstance()
        var value =0.0
        var fileName = UUID.randomUUID().toString()
        val ref = data.reference.child(fileName)
            ref.putFile(filePath!!)
            .addOnProgressListener { taskSnapshot ->
                value = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                Log.v("value", "value==$value")
                progress.setMessage("Uploaded.. " + value.toInt() + "%")
            }
            .addOnSuccessListener {
                progress.dismiss()
                onSuccessListener(ref.path)

            }
            .addOnFailureListener{
                    exception -> exception.printStackTrace()
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