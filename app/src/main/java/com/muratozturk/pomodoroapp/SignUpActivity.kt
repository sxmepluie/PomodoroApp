package com.muratozturk.pomodoroapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.muratozturk.pomodoroapp.databinding.ActivitySignUpBinding
import java.sql.Timestamp
import java.util.UUID

class SignUpActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth :FirebaseAuth
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedImage : Uri? = null
    private lateinit var storage:FirebaseStorage
    private var downloadUrl = ""
    private var isAdmin = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage
        registerLauncher()




    }

    fun signUpped(view:View){
        val name = binding.editTextTextName.text.toString()
        val surname = binding.editTextTextSurname.text.toString()
        val email = binding.editTextTextEmailAddress.text.toString()
        val password = binding.editTextTextPassword.text.toString()
        val totalStudy = 0
        val pomodoroCounter = 0
        val profileMap = hashMapOf<String,Any>()

        val uuid = UUID.randomUUID()
        val imageId = "$uuid.jpg"
        val reference = storage.reference
        val imageReference = reference.child("images").child(imageId)
        if(selectedImage!=null){
            imageReference.putFile(selectedImage!!).addOnSuccessListener{
                val uploadImageReference = storage.reference.child("images").child(imageId)
                uploadImageReference.downloadUrl.addOnSuccessListener {
                    downloadUrl = it.toString()
                    if (!downloadUrl.isNullOrEmpty()){
                        profileMap.put("downloadUrl",downloadUrl)
                    }
                    saveData(email,password,name,surname,totalStudy,profileMap,pomodoroCounter)

                }
            }.addOnFailureListener{
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
        else{
            saveData(email,password,name,surname,totalStudy,profileMap,pomodoroCounter)
        }
    }

    fun selectImage(view: View){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Fotoğraf yüklemek için galeriye erişim izni vermelisiniz",Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver",View.OnClickListener {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }).show()
            }else{
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intent)
        }

    }
    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if (result.resultCode == RESULT_OK){
                val intentFromResult =result.data
                if (intentFromResult!=null){
                    selectedImage = intentFromResult.data
                    selectedImage?.let {
                        binding.imageView2.setImageURI(it)
                    }

                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if (result){
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)
            }else{
                Toast.makeText(this@SignUpActivity,"İzin vermediğiniz için fotoğraf yüklenemiyor !!",Toast.LENGTH_LONG).show()
            }

        }
    }
    fun saveData(email:String,password:String,name:String,surname:String,totalStudy:Int,profileMap:HashMap<String,Any>,pomodoroCounter:Int){

        if(email == "" || password == ""){
            Toast.makeText(this,"Please Enter Email and Password !!",Toast.LENGTH_LONG).show()
        }
        else if(name == ""||surname==""){
            Toast.makeText(this,"Please Enter Name and Surname !!",Toast.LENGTH_LONG).show()
        }
        else{
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                if (auth.currentUser!=null){

                    if (!downloadUrl.isNullOrEmpty()){
                        profileMap.put("downloadUrl",downloadUrl)
                    }
                    profileMap.put("email",auth.currentUser!!.email!!)
                    profileMap.put("name",binding.editTextTextName.text.toString())
                    profileMap.put("surname",binding.editTextTextSurname.text.toString())
                    profileMap.put("totalStudy",totalStudy)
                    profileMap.put("pomodoroCounter",pomodoroCounter)
                    profileMap.put("isAdmin",isAdmin)



                    firestore.collection("Profiles").document(auth.currentUser!!.uid).set(profileMap).addOnSuccessListener {
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this@SignUpActivity, it.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }


                val intent = Intent(this@SignUpActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@SignUpActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }

}