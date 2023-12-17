package com.muratozturk.pomodoroapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.muratozturk.pomodoroapp.databinding.ActivityAddToDoBinding
import java.util.HashMap
import java.util.UUID

class AddToDoActivity : AppCompatActivity() {
    private lateinit var firestore : FirebaseFirestore
    private lateinit var binding: ActivityAddToDoBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddToDoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        firestore = Firebase.firestore
        auth = Firebase.auth
    }
    fun addToDo(view: View){
        val userProfile = firestore.collection("Profiles").document(auth.currentUser!!.uid)
        userProfile.get().addOnSuccessListener { documentSnapshot ->
            val userName = documentSnapshot.getString("name")
            val userSurname = documentSnapshot.getString("surname")
            val uuid = UUID.randomUUID()
            val documentId = "$uuid"
            if (!userName.isNullOrEmpty()&&!userSurname.isNullOrEmpty()) {
                val toDoMap = HashMap<String, Any>().apply {
                    put("toDoTitle", binding.toDoTitle.text.toString())
                    put("toDoDetails", binding.toDoDetails.text.toString())
                    put("date", Timestamp.now())
                    put("name", userName)
                    put("surname",userSurname)
                    put("documentId", documentId)
                    put("userId", FirebaseAuth.getInstance().currentUser?.uid ?: "")
                }

                firestore.collection("ToDos").document(documentId).set(toDoMap).addOnSuccessListener {
                    Toast.makeText(this@AddToDoActivity, "To-Do added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this@AddToDoActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this@AddToDoActivity, "User name is null or empty", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this@AddToDoActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
        }



    }
}