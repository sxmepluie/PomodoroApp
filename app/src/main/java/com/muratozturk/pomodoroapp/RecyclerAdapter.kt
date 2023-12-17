package com.muratozturk.pomodoroapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.muratozturk.pomodoroapp.databinding.RecyclerRowBinding

class RecyclerAdapter(val toDoList : ArrayList<HashMap<String, Any>>) :RecyclerView.Adapter<RecyclerAdapter.ToDoHolder>(){
    class ToDoHolder(val binding : RecyclerRowBinding):RecyclerView.ViewHolder(binding.root){
        val deleteButton: Button = binding.deleteButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ToDoHolder {
        val binding =RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ToDoHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ToDoHolder, position: Int) {
        val currentItem = toDoList[position]
        holder.binding.recyclerNameText.text = currentItem["name"].toString()
        holder.binding.recyclerSurnameText.text = currentItem["surname"].toString()
        holder.binding.recyclerToDoTitleText.text = currentItem["toDoTitle"].toString()
        holder.binding.recyclerToDoDetailsText.text = currentItem["toDoDetails"].toString()
        holder.deleteButton.setOnClickListener {
            deleteItem(position)
        }
    }

    override fun getItemCount(): Int {
        return toDoList.size
    }
    fun deleteItem(position: Int) {
        val firestore = FirebaseFirestore.getInstance()
        val documentId = toDoList[position]["documentId"].toString()

        firestore.collection("ToDos").document(documentId).delete().addOnSuccessListener {

                toDoList.removeAt(position)
                notifyDataSetChanged()
            }
            .addOnFailureListener {

            }
    }
}