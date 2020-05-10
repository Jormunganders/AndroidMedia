package me.juhezi.mediademo

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.demo_activity_video_play.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.juhezi.mediademo.grafika.PlayTask
import me.juhezi.mediademo.grafika.SpeedControlCallback
import me.juhezi.mediademo.grafika.VideoPlayer
import me.juhezi.mediademo.media.utils.logi

const val URL = "/storage/emulated/0/in.mp4"
const val OUT_URL = "/storage/emulated/0/out.mp4"
const val TAG = "Juhezi"

const val KEY_URL = "key_url"

class VideoPlayerActivity : AppCompatActivity(), TextureView.SurfaceTextureListener,
    VideoPlayer.PlayerFeedback, CoroutineScope by MainScope() {

    var playTask: PlayTask? = null
    var player: VideoPlayer? = null

    private var surfaceTextureReady = false
    private var isPlaying = false

    var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_activity_video_play)
        url = intent.getStringExtra(KEY_URL)

        // 申请权限
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                123
            )
        }
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
                player = VideoPlayer(if (url == null) URL else url!!, surface, callback)
                playTask = PlayTask(player, this)
                playTask!!.setLoopMode(true)
                playTask!!.execute()
                isPlaying = true
            }
        }
        video_test_button.setOnClickListener {
            logi(TAG, "Click Test")
            val start = System.currentTimeMillis()
            launch {
                val size = player?.getVideoSize()
                logi(
                    TAG,
                    "size is ${size?.first}x${size?.second}\ttime is ${System.currentTimeMillis() - start}"
                )
                // TODO: 2020/5/10 更新 TextureView 的缩放
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

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }


    private fun stopPlayback() {
        playTask?.requestStop()
    }

    override fun playbackStopped() {
        isPlaying = false
        playTask = null
    }

}


