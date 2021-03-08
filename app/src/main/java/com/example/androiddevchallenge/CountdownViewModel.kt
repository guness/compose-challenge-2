/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CountdownViewModel : ViewModel() {

    var seconds = 0
        set(value) {
            field = value
            _ticker.value = calculateTicker()
        }

    var minutes = 0
        set(value) {
            field = value
            _ticker.value = calculateTicker()
        }

    var hours = 0
        set(value) {
            field = value
            _ticker.value = calculateTicker()
        }

    private val _ticker = MutableStateFlow(0L)
    val ticker: StateFlow<Long> = _ticker

    private val _counting = MutableStateFlow(false)
    val counting: StateFlow<Boolean> = _counting

    private var timer: CountDownTimer? = null

    fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(calculateTicker(), 10) {
            override fun onTick(millis: Long) {
                _ticker.value = millis
            }

            override fun onFinish() {
                _ticker.value = 0
                _counting.value = false
            }
        }
        _counting.value = true
        timer?.start()
    }

    private fun calculateTicker(): Long {
        return 1000L * (seconds + minutes * 60 + hours * 3600)
    }

    fun cancelTimer() {
        timer?.cancel()
        timer = null
        _ticker.value = 0
        seconds = 0
        minutes = 0
        hours = 0
        _counting.value = false
    }
}
