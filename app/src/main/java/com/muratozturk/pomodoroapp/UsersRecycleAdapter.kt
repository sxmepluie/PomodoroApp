package com.muratozturk.pomodoroapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.muratozturk.pomodoroapp.databinding.RecyclerRowBinding
import com.muratozturk.pomodoroapp.databinding.UsersRecyclerRowBinding

class UsersRecycleAdapter(val profileList : ArrayList<HashMap<String, Any>>):RecyclerView.Adapter<UsersRecycleAdapter.Profileholder>() {
    class Profileholder(val binding: UsersRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){
        val deleteButton: Button = binding.deleteProfileButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Profileholder {
        val binding = UsersRecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UsersRecycleAdapter.Profileholder(binding)
    }

    override fun getItemCount(): Int {
        return profileList.size
    }

    override fun onBindViewHolder(holder: Profileholder, position: Int) {
        val currentItem = profileList[position]
        holder.binding.profileNameText.text = currentItem["name"].toString()
        holder.binding.profileSurnameText.text = currentItem["surname"].toString()
        holder.binding.profileEmailText.text = currentItem["email"].toString()
        holder.deleteButton.setOnClickListener {
            deleteItem(position)
        }
    }
    fun deleteItem(position: Int) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = profileList[position]["userId"].toString()

        firestore.collection("Profiles").document(userId).delete().addOnSuccessListener {

            profileList.removeAt(position)
            notifyDataSetChanged()
        }
            .addOnFailureListener {

            }
    }
}