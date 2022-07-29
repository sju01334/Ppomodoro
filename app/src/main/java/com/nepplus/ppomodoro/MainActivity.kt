package com.nepplus.ppomodoro

import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val remainMinutesTextView: TextView by lazy {
        findViewById(R.id.remainMinutesTextView)
    }

    private val remainSecondsTextView : TextView by lazy {
        findViewById(R.id.remainSecondsTextView)
    }

    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)
    }

    private val soundPool = SoundPool.Builder().build()

    private var tickingSoundId : Int? = null
    private var bellSoundId : Int? = null


    private var currentCountDownTimer : CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initSounds()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundPool
            .autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun bindViews() {
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if(fromUser){
                        updateRemainTime(progress*60*1000L)

                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    currentCountDownTimer?.cancel()
                    currentCountDownTimer = null
                    soundPool.autoPause()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar ?: return
                    if(seekBar.progress == 0){
                        currentCountDownTimer?.cancel()
                        currentCountDownTimer = null
                        soundPool.autoPause()
                    }else{
                        currentCountDownTimer = createCountDownTimer(seekBar.progress*60*1000L)
                        currentCountDownTimer?.start()
                        tickingSoundId?.let {
                            soundPool.play(it, 1F, 1F, 0,-1, 1F)
                        }
                    }


                }
            }
        )
    }


    private fun createCountDownTimer(initialMillis : Long) =
         object :  CountDownTimer(initialMillis, 1000L){
            override fun onTick(millisUntilFinished: Long) {
                updateRemainTime(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {

                updateRemainTime(0)
                updateSeekBar(0)
                soundPool.autoPause()
                bellSoundId?.let { soundPool.play(it, 1F,1F,0,0,1F) }
            }
        }

    private fun updateRemainTime(remailMillis : Long){
        val remainSeconds = remailMillis/1000

        remainMinutesTextView.text = "%02d'".format(remainSeconds / 60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis : Long) {
        seekBar.progress = (remainMillis/1000/60).toInt()
    }

    private fun initSounds(){
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }
}