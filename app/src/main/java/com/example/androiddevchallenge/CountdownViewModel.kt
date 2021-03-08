package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CountdownViewModel : ViewModel() {


    private val _ticker = MutableStateFlow(0L)
    val ticker: StateFlow<Long> = _ticker

    private var timer: CountDownTimer? = null

    fun startTimer(millis: Long) {

        timer?.cancel()
        timer = object : CountDownTimer(millis, 10) {
            override fun onTick(millis: Long) {
                _ticker.value = millis
            }

            override fun onFinish() {
                _ticker.value = 0
            }
        }
        timer?.start()
    }

    fun cancelTimer() {
        timer?.cancel()
        timer = null
    }
}