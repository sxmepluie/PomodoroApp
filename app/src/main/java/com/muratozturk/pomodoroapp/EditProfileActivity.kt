package com.muratozturk.pomodoroapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.muratozturk.pomodoroapp.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()
    }

    fun editProfile(view: View) {
        val newName = binding.nameEditTextText.text.toString()
        val newSurname = binding.surnameEditTextText.text.toString()
        val newEmail = binding.emailEditTextText.text.toString()

        val user = auth.currentUser
        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName("$newName $newSurname").build()

            user.updateProfile(profileUpdates).addOnSuccessListener {

                    val userId = auth.currentUser!!.uid
                    val userRef = FirebaseFirestore.getInstance().collection("Profiles").document(userId)

                    userRef.update(mapOf("name" to newName, "surname" to newSurname, "email" to newEmail)).addOnSuccessListener {
                        Toast.makeText(this@EditProfileActivity,"Profile Edited",Toast.LENGTH_LONG).show()
                        finish()

                    }.addOnFailureListener {
                        Toast.makeText(this@EditProfileActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                    }
            }.addOnFailureListener {
                Toast.makeText(this@EditProfileActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }
}
