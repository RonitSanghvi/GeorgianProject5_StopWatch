package com.example.georgianproject5_stopwatch

import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.Chronometer
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var stopwatch: Chronometer // Stopwatch
    // You need to initialize a variable when declaring. "lateinit" will let you initialize it later

    private var running = false         // Is stopwatch running ?
    private var offset: Long = 0        // The base offset for the stopwatch.


    private var addedSeconds = 0
    var firstTime = true

    private val OFFSETKEY = "offset"
    private val RUNNINGKEY = "running"
    private val BASEKEY = "base"

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_main)
        stopwatch = findViewById(R.id.stopwatch)

        // Restore the previous state.
        // When you rotate your phone, everything restarts so to save the current data we have to save data in bundle.
        // This checks if there is some data in bundle.
        // If there are the values, it will take those.
        if (bundle != null) {
            offset = bundle.getLong(OFFSETKEY)
            running = bundle.getBoolean(RUNNINGKEY)
            if (running) {
                stopwatch.base = bundle.getLong(BASEKEY)
                stopwatch.start()
            } else {
                updateBaseTime()
            }
        }


        // To add 5,10 or 20 Seconds in Timer.
        val sec5 = findViewById<Button>(R.id.button5)
        sec5.setOnClickListener {
            addedSeconds = addedSeconds + 5
            updateBaseTime(firstTimeSet = true)
        }
        val sec10 = findViewById<Button>(R.id.button10)
        sec10.setOnClickListener {
            addedSeconds = addedSeconds + 10
            updateBaseTime(firstTimeSet = true)
        }
        val sec20 = findViewById<Button>(R.id.button20)
        sec20.setOnClickListener {
            addedSeconds = addedSeconds + 20
            updateBaseTime(firstTimeSet = true)
        }

        // When time comes to 00:00, it will stop the stopwatch so it won't go negetive.
        stopwatch.setOnChronometerTickListener {
            if (it.text.contentEquals("00:00", true)) {
                stopwatch.stop()
                addedSeconds = 0
            }
        }


        val startButton = findViewById<Button>(R.id.start_button)
        startButton.setOnClickListener {
            if (!running) {
                updateBaseTime(firstTimeSet = firstTime)
                if (firstTime) {
                    firstTime = false
                }
            }
            stopwatch.start()
            running = true
        }
        val pauseButton = findViewById<Button>(R.id.pause_button)
        pauseButton.setOnClickListener {
            if (running) {
                saveOffset()
                stopwatch.stop()
                running = false
            }
        }
        val resetButton = findViewById<Button>(R.id.reset_button)
        resetButton.setOnClickListener {
            offset = 0
            addedSeconds = 0
            updateBaseTime(firstTimeSet = true)
        }
    }

    // Saving the data in bundle so we can get when the screen rotates.
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong(OFFSETKEY, offset)
        outState.putBoolean(RUNNINGKEY, running)
        outState.putLong(BASEKEY, stopwatch.base)
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        if (running) {
            saveOffset()
            stopwatch.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        if (running) {
            updateBaseTime()
            stopwatch.start()
            offset = 0
        }
    }

    private fun updateBaseTime(firstTimeSet: Boolean = false) {
        if (firstTimeSet) {
            stopwatch.base = SystemClock.elapsedRealtime() + (addedSeconds * 1000) - offset
        } else {
            stopwatch.base = SystemClock.elapsedRealtime() - offset
        }
    }

    private fun saveOffset() {
        offset = SystemClock.elapsedRealtime() - stopwatch.base
    }

}
