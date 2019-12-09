package com.scrubele.scrubeleapp1.adapters

import android.app.ProgressDialog
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

object FirebaseAdapter {
    val auth = FirebaseAuth.getInstance()
    var user = auth.currentUser!!
    var firebaseFirestore = FirebaseFirestore.getInstance()
    var storageInstance = FirebaseStorage.getInstance()
    var filePath: Uri? = null

    fun getPhoneNumber(onSuccessListener: (phoneNumber: String) -> Unit): String {
        val dataRef = firebaseFirestore.collection("users").document(auth.currentUser!!.uid)
        var phoneNumber = ""
        dataRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    phoneNumber = document.getString("phone") as String
                    onSuccessListener(phoneNumber)
                }
            }
        return phoneNumber
    }

    fun getPhoto(onSuccessListener: (imagePath: String) -> Unit) {
        val dataRef = firebaseFirestore.collection("users").document(auth.currentUser!!.uid)
        var photo: String
        dataRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    photo = document.getString("photoURL") as String
                    onSuccessListener(photo)
                }
            }
    }

    fun getPhotoUrl(): String {
        val dataRef = firebaseFirestore.collection("users").document(auth.currentUser!!.uid)
        var photo = ""
        dataRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    photo = document.getString("photoURL") as String
                }
            }
        return photo
    }

    fun updateEmail(activity: FragmentActivity?, newEmail: String) {
        user.updateEmail(newEmail)
            .addOnCompleteListener { task1 ->
                if (task1.isSuccessful) {
                }
            }
    }

    fun updateProfile(newName: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()
        user.updateProfile(profileUpdates)
    }

    fun updateFirestoreData(email: String, name: String, phone: String) {
        val userData = hashMapOf<String, Any>(
            "email" to email,
            "name" to name,
            "phone" to phone
        )
        firebaseFirestore.collection("users").document(auth.currentUser!!.uid).update(userData)
    }

    fun uploadProfilePhoto(
        activity: FragmentActivity?,
        onSuccessListener: (imagePath: String) -> Unit
    ) {
        if (filePath != null) {
            val progress = ProgressDialog(activity).apply {
                setTitle("Uploading Picture....")
                setCancelable(false)
                setCanceledOnTouchOutside(false)
                show()
            }
            var value: Double
            val fileName = "profilePictures/" + UUID.randomUUID().toString()
            val ref = storageInstance.reference.child(fileName)
            ref.putFile(filePath!!)
                .addOnProgressListener { taskSnapshot ->
                    value = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                    progress.setMessage("Uploaded.. " + value.toInt() + "%")
                }
                .addOnSuccessListener {
                    progress.dismiss()
                    onSuccessListener(ref.path)
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                }
        }
    }
}