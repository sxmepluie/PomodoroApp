package com.muratozturk.pomodoroapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.muratozturk.pomodoroapp.databinding.ActivityProfileBinding
import com.muratozturk.pomodoroapp.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = Firebase.auth
    }
    fun resetPassword(view: View){
        val userEmail = binding.resetPasswordEmailEditText.text.toString()
        if (!userEmail.isEmpty()) {
            auth.sendPasswordResetEmail(userEmail).addOnSuccessListener {
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@ResetPasswordActivity, it.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            }
        }
        else{
            Toast.makeText(this@ResetPasswordActivity,"Lütfen emailinizi giriniz !!",Toast.LENGTH_LONG).show()
        }
    }
}