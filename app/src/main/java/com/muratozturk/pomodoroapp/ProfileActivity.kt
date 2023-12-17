package com.muratozturk.pomodoroapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.muratozturk.pomodoroapp.databinding.ActivityProfileBinding
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        firestore = Firebase.firestore
        auth = Firebase.auth
        getData()
    }
    fun getData(){
        firestore.collection("Profiles").addSnapshotListener { value, error ->
            if(error!=null){
                Toast.makeText(this@ProfileActivity,error.localizedMessage,Toast.LENGTH_LONG).show()
            }
            else{
                if (value!=null){
                    if (!value.isEmpty){
                        val userProfile = firestore.collection("Profiles").document(auth.currentUser!!.uid)

                        userProfile.get().addOnSuccessListener {
                            if (it.exists()){

                                binding.profileNameText.text = "Name: "+it.getString("name")
                                binding.profileSurnameText.text = "Surname: "+it.getString("surname")
                                binding.profileEmailText.text = "Email: "+it.getString("email")
                                binding.totalStudy.text = "Total Time Studied: "+convertHour(it.getLong("totalStudy")!!)
                                binding.pomodoroCounter.text = "Finished Pomodoros: "+it.get("pomodoroCounter")
                                val imageUrl = it.getString("downloadUrl")
                                if (!imageUrl.isNullOrEmpty()){
                                    Glide.with(this@ProfileActivity).load(imageUrl).into(binding.imageView)
                                }
                                if(it.getBoolean("isAdmin")==true){
                                    binding.adminText.visibility = View.VISIBLE
                                    binding.usersButton.visibility = View.VISIBLE
                                }

                            }

                        }


                    }
                }
            }
        }

    }
    fun usersEdit(view: View){
        val intent = Intent(this@ProfileActivity,UsersActivity::class.java)
        startActivity(intent)
    }
    fun convertHour(value:Long):String{
        val studyWithSecond = value.toDouble()
        val studyWithHour:Double = (studyWithSecond/3600)
        val stringStudyWithHour = String.format("%.2f",studyWithHour)
        return stringStudyWithHour
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.profile_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.editAccount){
            val intent = Intent(this@ProfileActivity,EditProfileActivity::class.java)
            startActivity(intent)

        }
        else if(item.itemId==R.id.deleteAccount) {
            val firebaseAuth = FirebaseAuth.getInstance()
            val user = firebaseAuth.currentUser
            user?.delete()?.addOnSuccessListener {
                Toast.makeText(this@ProfileActivity,"Deleted successfully...",Toast.LENGTH_LONG).show()
                val intent = Intent(this@ProfileActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }?.addOnFailureListener {
                Toast.makeText(this@ProfileActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
            val userId = auth.currentUser!!.uid
            val userRef = firestore.collection("Profiles").document(userId)
            userRef.delete().addOnSuccessListener {

            }.addOnFailureListener {
                Toast.makeText(this@ProfileActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }

        }
        else if (item.itemId==R.id.changePassword){
            val intent = Intent(this@ProfileActivity,ResetPasswordActivity::class.java)
            startActivity(intent)
        }


        return super.onOptionsItemSelected(item)
    }


}