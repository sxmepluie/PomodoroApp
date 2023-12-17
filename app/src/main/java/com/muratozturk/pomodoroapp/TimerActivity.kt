package com.muratozturk.pomodoroapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.muratozturk.pomodoroapp.databinding.ActivityTimerBinding
import java.util.concurrent.TimeUnit


class TimerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimerBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var timeCountDown :CountDownTimer
    private lateinit var firestore: FirebaseFirestore
    lateinit var sharedPref : SharedPreferences
    private var isRunning = false
    private var miliSecondTime : Long = 0
    private var startTime:Timestamp? = null
    private var endTime:Timestamp? = null
    private var waitingStartTime:Timestamp? = null
    private var waitingEndTime:Timestamp? = null
    private var waitingTimeCounter:Long = 0
    private var isRunnedTimeCalc : Boolean? = null
    private var defaultTime : Long = 0
    private var isStudy = true
    private var studyCounter = 1
    private var studyMinute : Long = 0
    private var studySecond : Long = 0
    private var restingMinute : Long = 0
    private var restingSecond : Long = 0
    private var longRestingMinute : Long = 0
    private var longRestingSecond : Long = 0
    private val notificationId = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = Firebase.auth
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        firestore = Firebase.firestore
        sharedPref = this.getSharedPreferences("com.muratozturk.pomodoroapp", Context.MODE_PRIVATE)
        studyMinute = sharedPref.getLong("studyMinute",25)
        studySecond = sharedPref.getLong("studySecond",0)
        restingMinute = sharedPref.getLong("restingMinute",5)
        restingSecond = sharedPref.getLong("restingSecond",0)
        longRestingMinute = sharedPref.getLong("longRestingMinute",15)
        longRestingSecond = sharedPref.getLong("longRestingSecond",0)

        defaultTime = studyMinute*60000+studySecond*1000



    }
    fun startResumeTimer(view: View){

        if (isRunning){
            isRunning = false
            binding.buttonStart.text = "Resume"
            timeCountDown.cancel()
            if (isStudy){
                waitingStartTime = Timestamp.now()
                isRunnedTimeCalc = false
            }

        }
        else{
            binding.buttonReset.isEnabled = true
            isRunning = true
            binding.buttonStart.text = "Pause"
            if (!::timeCountDown.isInitialized || miliSecondTime==0L){
                startTime = Timestamp.now()
                isRunnedTimeCalc = false

                miliSecondTime = defaultTime

                timeCountDown = object : CountDownTimer(miliSecondTime,1000){
                    override fun onTick(millisUntilFinished: Long) {
                        miliSecondTime = millisUntilFinished
                        updateTimer()
                        updateNot(miliSecondTime)

                    }

                    override fun onFinish() {
                        binding.buttonReset.isEnabled = false
                        refreshTimer()

                    }

                }
                timeCountDown.start()
            }
            else{
                timeCountDown = object :CountDownTimer(miliSecondTime,1000){
                    override fun onTick(millisUntilFinished: Long) {
                        miliSecondTime = millisUntilFinished
                        updateTimer()
                        updateNot(miliSecondTime)
                        if (isStudy){
                            if(!isRunnedTimeCalc!!){
                                waitingEndTime = Timestamp.now()
                                var totalWaitingTime = waitingEndTime!!.toDate().time-waitingStartTime!!.toDate().time
                                waitingTimeCounter = waitingTimeCounter + totalWaitingTime
                                waitingEndTime = null
                                waitingStartTime = null
                                totalWaitingTime = 0
                                isRunnedTimeCalc = true
                            }
                        }


                    }

                    override fun onFinish() {
                        binding.buttonReset.isEnabled = false
                        refreshTimer()
                    }

                }
                timeCountDown.start()
            }


        }

    }
    fun resetTimer(view: View){
        refreshTimer()
    }
    fun updateTimer(){
        val minuteView = miliSecondTime/60000
        val secondView = (miliSecondTime/1000)%60
        val clockTime = String.format("%02d:%02d",minuteView,secondView)
        binding.textViewCountDown.text = clockTime
    }
    fun refreshTimer(){
        binding.buttonReset.isEnabled = false
        isRunning= false
        binding.buttonStart.text = "Start"
        miliSecondTime = 0
        updateTimer()
        updateNot(miliSecondTime)
        timeCountDown.cancel()
        if (isStudy) {
            if (startTime != null) {
                endTime = Timestamp.now()
                val totalTime = endTime!!.toDate().time - startTime!!.toDate().time
                if (waitingTimeCounter != 0L) {
                    val allTime = ((totalTime - waitingTimeCounter!!) / 1000)
                    firestore.collection("Profiles").document(auth.currentUser!!.uid)
                        .update("totalStudy", FieldValue.increment(allTime))
                } else if (waitingTimeCounter != 0L && waitingEndTime == null) {
                    waitingEndTime = Timestamp.now()
                    val totalWaitingTime =
                        waitingEndTime!!.toDate().time - waitingStartTime!!.toDate().time
                    waitingTimeCounter = waitingTimeCounter + totalWaitingTime
                    val allTime = ((totalTime - waitingTimeCounter!!) / 1000)
                    firestore.collection("Profiles").document(auth.currentUser!!.uid)
                        .update("totalStudy", FieldValue.increment(allTime))

                } else {
                    val allTime = totalTime / 1000
                    firestore.collection("Profiles").document(auth.currentUser!!.uid)
                        .update("totalStudy", FieldValue.increment(allTime))
                }
                startTime = null
                endTime = null
                waitingStartTime = null
                waitingEndTime = null
                waitingTimeCounter = 0
            }
        }

        if (isStudy && studyCounter%4==0){
            isStudy = false
            val changedTimeByUser = longRestingMinute*60000+longRestingSecond*1000
            defaultTime = changedTimeByUser

        }else if(isStudy){
            isStudy = false
            val changedTimeByUser = restingMinute*60000+restingSecond*1000
            defaultTime = changedTimeByUser
        }
        else{
            isStudy = true
            val changedTimeByUser = studyMinute*60000+studySecond*1000
            defaultTime = changedTimeByUser
            studyCounter++
            firestore.collection("Profiles").document(auth.currentUser!!.uid)
                .update("pomodoroCounter", FieldValue.increment(1))
        }



    }

    fun setTimer(view: View){
        val intent = Intent(this@TimerActivity,SetTimerActivity::class.java)
        startActivity(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.pomodoro_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.signout){
            auth.signOut()
            val intent = Intent(this@TimerActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else if(item.itemId==R.id.profile) {
            val intent = Intent(this@TimerActivity,ProfileActivity::class.java)
            startActivity(intent)

        }
        else if(item.itemId==R.id.editTimer) {
            val intent = Intent(this@TimerActivity,SetTimerActivity::class.java)
            startActivity(intent)

        }
        else if (item.itemId==R.id.toDoList){
            val intent = Intent(this@TimerActivity,ToDoListActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    fun createNotChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "com.muratozturk.pomodoroapp",
                "Pomodoro Timer Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Pomodoro Timer Notifications"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun updateNot(remainingTimeMillis:Long){
        val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTimeMillis) - TimeUnit.MINUTES.toSeconds(minutes)
        val formattedTime = String.format("%02d:%02d", minutes, seconds)
        val notificationText = "Time Remaining: $formattedTime"
        val intent = Intent(this, TimerActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        createNotChannel()
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, "com.muratozturk.pomodoroapp")
                .setContentTitle("Pomodoro Timer")
                .setContentText(notificationText)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(Notification.PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_ALARM)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build()
        } else {
            NotificationCompat.Builder(this, "com.muratozturk.pomodoroapp")
                .setContentTitle("Pomodoro Timer")
                .setContentText(notificationText)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build()
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

}