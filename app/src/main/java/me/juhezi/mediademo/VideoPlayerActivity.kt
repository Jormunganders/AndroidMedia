package me.juhezi.mediademo

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.SurfaceTexture
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.demo_activity_video_play.*
import kotlinx.coroutines.*
import me.juhezi.mediademo.grafika.PlayTask
import me.juhezi.mediademo.grafika.SpeedControlCallback
import me.juhezi.mediademo.grafika.VideoPlayer
import java.io.FileDescriptor
import kotlin.coroutines.resume

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
                launch {
                    val st = video_play_texture_view.surfaceTexture
                    val surface = Surface(st)
                    val callback = SpeedControlCallback()
                    callback.setFps(60)
                    val fd = parsePath2FD(url ?: URL)
                    player = if (fd != null) {
                        VideoPlayer(fd, surface, callback)
                    } else {
                        VideoPlayer(url ?: URL, surface, callback)
                    }
                    playTask = PlayTask(player, this@VideoPlayerActivity)
                    playTask!!.setLoopMode(true)
                    playTask!!.execute()
                    adjustTextureView()
                    isPlaying = true
                }
            }
        }
        video_test_button.setOnClickListener {
            val start = System.currentTimeMillis()
            launch {
                val size = player?.getVideoSize()
                logi(
                    TAG,
                    "size is ${size?.first}x${size?.second}\ttime is ${System.currentTimeMillis() - start}"
                )
            }
        }
    }

    @Deprecated("这个方法不太行")
    private suspend fun getVideoFD() = withTimeoutOrNull(5_000) {
        suspendCancellableCoroutine<FileDescriptor> { cont ->
            MediaScannerConnection.scanFile(
                this@VideoPlayerActivity,
                arrayOf(url ?: URL),
                arrayOf("video/mp4")
            ) { path, uri ->
                logi(TAG, "path is $path")
                logi(TAG, "uri is $url")
                val parcelFileDescriptor: ParcelFileDescriptor =
                    contentResolver.openFileDescriptor(uri, "r")!!
                val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
                cont.resume(fileDescriptor)
            }
        }
    }

    private fun parsePath2FD(path: String) = getFDFromUri(
        this@VideoPlayerActivity,
        getVideoUriFromPath(
            this@VideoPlayerActivity,
            path
        )
    )

    /**
     * 调整 TextureView，恶心无比的逻辑
     */
    private fun adjustTextureView() =
        video_play_texture_view.post {
            launch {
                val pair = player?.getVideoSize()
                if (pair == null) {
                    loge(TAG, "Video Size is null")
                    return@launch
                }
                // 获取旋转角度
                val rotation = 0

                val viewWidth = video_play_texture_view.width
                val viewHeight = video_play_texture_view.height
                var videoWidth = pair.first
                var videoHeight = pair.second
                var initWidth = viewWidth // 初始宽高
                var initHeight = viewHeight

                if (viewHeight == 0 || viewWidth == 0) return@launch
                if (videoHeight == 0 || videoWidth == 0) return@launch
                if (rotation % 180 != 0) {  // 有旋转角的情况会执行这些逻辑
                    videoHeight = videoWidth + videoHeight
                    videoWidth = videoHeight - videoWidth
                    videoHeight = videoHeight - videoWidth
                    initWidth = viewHeight
                    initHeight = viewWidth
                }
                val viewRatio = viewWidth / viewHeight.toFloat()
                val videoRatio = videoWidth / videoHeight.toFloat()
                val targetWidth: Int
                val targetHeight: Int
                if (videoRatio >= viewRatio) {  // 宽的
                    targetWidth = viewWidth
                    targetHeight = if (videoRatio == 0f) {
                        viewHeight
                    } else {
                        (targetWidth / videoRatio).toInt()
                    }
                } else {    // 长的
                    targetHeight = viewHeight
                    targetWidth = (targetHeight * videoRatio).toInt()
                }
                val matrix = Matrix()
                matrix.postRotate(rotation.toFloat(), viewWidth / 2f, viewHeight / 2f)
                matrix.postScale(
                    targetWidth / viewWidth.toFloat(),
                    targetHeight / viewHeight.toFloat(),
                    viewWidth / 2f,
                    viewHeight / 2f
                )
                video_play_texture_view.setTransform(matrix)
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


