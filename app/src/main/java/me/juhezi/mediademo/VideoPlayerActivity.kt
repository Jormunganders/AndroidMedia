package me.juhezi.mediademo

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_video_play.*
import me.juhezi.mediademo.grafika.PlayTask
import me.juhezi.mediademo.grafika.SpeedControlCallback
import me.juhezi.mediademo.grafika.VideoPlayer

const val URL = "/storage/emulated/0/in.mp4"
const val OUT_URL = "/storage/emulated/0/out.mp4"
const val TAG = "Juhezi"

class VideoPlayerActivity : AppCompatActivity(), TextureView.SurfaceTextureListener,
    VideoPlayer.PlayerFeedback {

    var playTask: PlayTask? = null

    private var surfaceTextureReady = false
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        video_play_texture_view.surfaceTextureListener = this
        updateButtonState()
        video_play_button.setOnClickListener {
            if (isPlaying) {
                stopPlayback()
            } else {
                if (playTask != null) {
                    return@setOnClickListener
                }
                val st = video_play_texture_view.surfaceTexture
                val surface = Surface(st)
                val callback = SpeedControlCallback()
//                callback.setFps(60)
                val player = VideoPlayer(URL, surface, callback)
                playTask = PlayTask(player, this)
                playTask!!.setLoopMode(true)
                playTask!!.execute()
                isPlaying = true
            }
        }
    }

    private fun updateButtonState() {
        video_play_button.isEnabled = surfaceTextureReady
    }


    override fun onSurfaceTextureSizeChanged(
        surface: SurfaceTexture?, width: Int, height: Int
    ) {
    }

    override fun onSurfaceTextureUpdated(
        surface: SurfaceTexture?
    ) {
    }

    override fun onSurfaceTextureDestroyed(
        surface: SurfaceTexture?
    ): Boolean {
        surfaceTextureReady = false
        video_play_button.isEnabled = surfaceTextureReady
        return true
    }

    override fun onSurfaceTextureAvailable(
        surface: SurfaceTexture?, width: Int, height: Int
    ) {
        surfaceTextureReady = true
        video_play_button.isEnabled = surfaceTextureReady
    }

    override fun onStop() {
        super.onStop()
        if (playTask != null) {
            stopPlayback()
            playTask!!.waitForStop()
        }
    }


    private fun stopPlayback() {
        playTask?.requestStop()
    }

    override fun playbackStopped() {
        isPlaying = false
        playTask = null
    }

}


