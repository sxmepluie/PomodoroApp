package com.muratozturk.pomodoroapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.muratozturk.pomodoroapp.databinding.ActivityToDoListBinding

class ToDoListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityToDoListBinding
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var toDoList: ArrayList<HashMap<String, Any>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToDoListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        firestore = Firebase.firestore
        toDoList = ArrayList()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = RecyclerAdapter(toDoList)
        binding.recyclerView.adapter = recyclerAdapter

        getData()
    }

    private fun getData() {
        firestore.collection("ToDos").orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(this@ToDoListActivity, exception.localizedMessage, Toast.LENGTH_LONG).show()
                } else {
                    if (snapshot != null) {
                        toDoList.clear()
                        for (document in snapshot.documents) {
                            val name = document.getString("name")
                            val surname = document.getString("surname")
                            val toDoTitle = document.getString("toDoTitle")
                            val toDoDetails = document.getString("toDoDetails")
                            val documentId = document.getString("documentId")

                            if (name != null && surname != null && toDoTitle != null && toDoDetails != null) {
                                val toDoMap = HashMap<String, Any>().apply {
                                    put("name", name)
                                    put("surname", surname)
                                    put("toDoTitle", toDoTitle)
                                    put("toDoDetails", toDoDetails)
                                    put("documentId",documentId!!)
                                }

                                toDoList.add(toDoMap)
                            } else {

                            }
                        }
                        recyclerAdapter.notifyDataSetChanged()
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.todo_list_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.addToDo) {
            val intent = Intent(this@ToDoListActivity, AddToDoActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
