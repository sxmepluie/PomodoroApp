package com.muratozturk.pomodoroapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.muratozturk.pomodoroapp.databinding.ActivityProfileBinding
import com.muratozturk.pomodoroapp.databinding.ActivityUsersBinding

class UsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

}