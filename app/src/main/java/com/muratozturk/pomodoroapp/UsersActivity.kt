package com.muratozturk.pomodoroapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.muratozturk.pomodoroapp.databinding.ActivityProfileBinding
import com.muratozturk.pomodoroapp.databinding.ActivityToDoListBinding
import com.muratozturk.pomodoroapp.databinding.ActivityUsersBinding

class UsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersBinding
    private lateinit var usersRecyclerAdapter: UsersRecycleAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var profileList: ArrayList<HashMap<String, Any>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        firestore = Firebase.firestore
        profileList = ArrayList()
        auth = Firebase.auth
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        usersRecyclerAdapter = UsersRecycleAdapter(profileList)
        binding.recyclerView.adapter = usersRecyclerAdapter

        getData()

    }
    fun getData(){
        firestore.collection("Profiles").addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(this@UsersActivity, exception.localizedMessage, Toast.LENGTH_LONG).show()
                } else {
                    if (snapshot != null) {
                        profileList.clear()
                        for (document in snapshot.documents) {
                            val name = document.getString("name")
                            val surname = document.getString("surname")
                            val email = document.getString("email")


                            if (name != null && surname != null && email != null ) {
                                val profileMap = HashMap<String, Any>().apply {
                                    put("name", name)
                                    put("surname", surname)
                                    put("email", email)
                                    put("userId",auth.currentUser!!.uid)

                                }
                                println(profileList)
                                profileList.add(profileMap)
                            } else {

                            }
                        }
                        usersRecyclerAdapter.notifyDataSetChanged()
                    }
                }
            }
    }

}