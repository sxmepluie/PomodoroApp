package com.muratozturk.pomodoroapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.muratozturk.pomodoroapp.databinding.ActivitySetTimerBinding
import com.muratozturk.pomodoroapp.databinding.ActivityTimerBinding
import java.lang.NumberFormatException

class SetTimerActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetTimerBinding
    lateinit var sharedPref:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetTimerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sharedPref = this.getSharedPreferences("com.muratozturk.pomodoroapp", Context.MODE_PRIVATE)
    }
    fun setTime(view: View){
       try {
           val studyMinute = binding.studyMinuteEditTextText.text.toString().toLong()
           val studySecond = binding.studySecondEditTextText.text.toString().toLong()
           val restingMinute = binding.restingMinuteEditTextText.text.toString().toLong()
           val restingSecond = binding.restingSecondEditTextText.text.toString().toLong()
           val longRestingMinute = binding.longRestingMinuteEditTextText.text.toString().toLong()
           val longRestingSecond = binding.longRestingSecondEditTextText.text.toString().toLong()
           if (studyMinute==0L||restingMinute==0L||longRestingMinute==0L){
               Toast.makeText(this@SetTimerActivity,"Your values are 0. Please enter a proper value",Toast.LENGTH_LONG).show()
           }
           else{
               sharedPref.edit().putLong("studyMinute",studyMinute).apply()
               sharedPref.edit().putLong("studySecond",studySecond).apply()
               sharedPref.edit().putLong("restingMinute",restingMinute).apply()
               sharedPref.edit().putLong("restingSecond",restingSecond).apply()
               sharedPref.edit().putLong("longRestingMinute",longRestingMinute).apply()
               sharedPref.edit().putLong("longRestingSecond",longRestingSecond).apply()

               val intent = Intent(this@SetTimerActivity,TimerActivity::class.java)
               startActivity(intent)
               finish()
           }

       }catch (e:NumberFormatException){
           Toast.makeText(this@SetTimerActivity,"Please enter the minutes and seconds",Toast.LENGTH_LONG).show()
       }

    }
}