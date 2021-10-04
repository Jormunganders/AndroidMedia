package me.juhezi.mediademo.grafika

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Handler
import android.os.Message
import android.view.Surface
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import me.juhezi.mediademo.media.utils.TrackType
import me.juhezi.mediademo.loge
import me.juhezi.mediademo.logi
import me.juhezi.mediademo.media.utils.selectTrack
import java.io.FileDescriptor
import java.lang.Exception
import kotlin.coroutines.resume

/**
 * 简易版视频播放器
 */
class VideoPlayer(
    private val sourcePath: String?,
    private val outputSurface: Surface,
    private val frameCallback: FrameCallback?
) {

    constructor(
        sourceFileDescriptor: FileDescriptor,
        outputSurface: Surface,
        frameCallback: FrameCallback?
    ) : this(null, outputSurface, frameCallback) {
        this.sourceFD = sourceFileDescriptor
    }

    private var sourceFD: FileDescriptor? = null

    private val TIMEOUT_USEC = 10000L

    private val mBufferInfo = MediaCodec.BufferInfo()

    @Volatile
    private var isStopRequested = false
    var loop = false
    private var videoHeight = 0
    private var videoWidth = 0

    var sizeAvailable: ((Pair<Int, Int>) -> Unit)? = null

    interface PlayerFeedback {
        // TODO: 2020-04-17 调用时机
        fun playbackStopped()
    }

    interface FrameCallback {
        /**
         * Called immediately before the frame is rendered.
         *
         * @param presentationTimeUsec The desired presentation time, in microseconds.
         */
        fun preRender(presentationTimeUsec: Long)

        /**
         * Called immediately after the frame render call returns.  The frame may not have
         * actually been rendered yet.
         * TODO: is this actually useful?
         */
        fun postRender()

        /**
         * Called after the last frame of a looped movie has been rendered.  This allows the
         * callback to adjust its expectations of the next presentation time stamp.
         */
        fun loopReset()
    }

    companion object {
        private const val TAG = "VideoPlayer"
    }


    fun requestStop() {
        isStopRequested = true
    }

    suspend fun getVideoSize() = withTimeoutOrNull(5_000) {
        suspendCancellableCoroutine<Pair<Int, Int>> { cont ->
            if (videoHeight == 0 && videoWidth == 0) {
                sizeAvailable = {
                    cont.resume(it)
                    sizeAvailable = null
                }
            } else {
                cont.resume(videoWidth to videoHeight)
            }
        }
    }

    fun play() {
        var extractor: MediaExtractor? = null
        var decoder: MediaCodec? = null
        try {
            extractor = MediaExtractor()
            when {
                sourcePath != null -> {
                    extractor.setDataSource(sourcePath)
                }
                sourceFD != null -> {
                    extractor.setDataSource(sourceFD!!)
                }
                else -> {
                    throw Exception("Source is null!")
                }
            }
            val trackIndex = extractor.selectTrack(TrackType.Video)
            extractor.selectTrack(trackIndex)
            val format = extractor.getTrackFormat(trackIndex)
            videoWidth = format.getInteger(MediaFormat.KEY_WIDTH)
            videoHeight = format.getInteger(MediaFormat.KEY_HEIGHT)
            sizeAvailable?.invoke(videoWidth to videoHeight)
            logi(
                TAG,
                "Video size is $videoWidth x $videoHeight"
            )
            decoder = MediaCodec.createDecoderByType(
                format.getString(MediaFormat.KEY_MIME) ?: ""
            )
            decoder.configure(format, outputSurface, null, 0)
            decoder.start()
            doExtract(extractor, decoder)
        } finally {
            decoder?.stop()
            decoder?.release()
            extractor?.release()
        }
    }

    private fun doExtract(
        extractor: MediaExtractor,
        decoder: MediaCodec
    ) {
        var inputChunk = 0
        var firstInputTimeNsec = -1L

        var outputDone = false
        var inputDone = false

        while (!outputDone) {
            if (isStopRequested) {
                logi(TAG, "Stop Request")
                return
            }
            if (!inputDone) {
                val inputIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC)
                if (inputIndex >= 0) {
                    if (firstInputTimeNsec == -1L) {
                        firstInputTimeNsec = System.nanoTime()
                    }
                    var inputBuffer = decoder.getInputBuffer(inputIndex)
                    val chunkSize = extractor.readSampleData(
                        inputBuffer!!, 0
                    )
                    if (chunkSize < 0) {
                        // 输入流结尾，发送一个空的 Frame 和一个 EOS 标志
                        decoder.queueInputBuffer(
                            inputIndex,
                            0,
                            0,
                            0L,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        inputDone = true
                        logi(TAG, "Send input EOS")
                    } else {
                        val pts = extractor.sampleTime
                        decoder.queueInputBuffer(
                            inputIndex,
                            0,
                            chunkSize,
                            pts,
                            0
                        )
                        logi(
                            TAG, "Submitted Frame $inputChunk to decoder," +
                                    "size is $chunkSize."
                        )
                        inputChunk++
                        extractor.advance()
                    }
                } else {
                    loge(TAG, "Input buffer not available")
                }
            }
            if (!outputDone) {
                val decoderStatus = decoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC)
                if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) { // no output available yet
                    logi(
                        TAG,
                        "no output from decoder available"
                    )
                } else if (decoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) { // not important for us, since we're using Surface
                    logi(
                        TAG,
                        "decoder output buffers changed"
                    )
                } else if (decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    val newFormat: MediaFormat = decoder.outputFormat
                    logi(
                        TAG, "deocoder output format changed: " +
                                "$newFormat"
                    )
                } else if (decoderStatus < 0) {
                    loge(
                        TAG,
                        "Unexpected result from decoder.dequeueOutputBuffer: $decoderStatus"
                    )
                } else {    // decoderStatus >= 0
                    if (firstInputTimeNsec != 0L) {
                        // Log the delay from the first buffer of input to the first buffer
                        // of output.
                        val nowNsec = System.nanoTime()
                        logi(
                            TAG,
                            "startup lag " + (nowNsec - firstInputTimeNsec) / 1000000.0 +
                                    " ms"
                        )
                        firstInputTimeNsec = 0L
                    }
                    var doLoop = false
                    logi(
                        TAG,
                        "surface decoder given buffer" +
                                " $decoderStatus(size=${mBufferInfo.size})"
                    )
                    if ((mBufferInfo.flags and
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0
                    ) {
                        logi(TAG, "Output EOS")
                        if (loop) {
                            doLoop = true
                        } else {
                            outputDone = true
                        }
                    }

                    val doRender = (mBufferInfo.size != 0)
                    if (doRender) {
                        frameCallback?.preRender(mBufferInfo.presentationTimeUs)
                    }
                    // 调用 releaseOutputBuffer 的时候，
                    // Buffer 就会转发到 SurfaceTexture 并且转化为一个 Texture
                    decoder.releaseOutputBuffer(decoderStatus, doRender)
                    if (doRender) {
                        frameCallback?.postRender()
                    }
                    if (doLoop) {
                        logi(TAG, "Reached EOS，looping")
                        extractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
                        inputDone = false
                        decoder.flush() // 重置 decoder 状态
                        frameCallback?.loopReset()
                    }
                }
            }
        }
    }

}

const val MSG_PLAY_STOPPED = 0

class LocalHandler : Handler() {
    override fun handleMessage(msg: Message) {
        when (msg.what) {
            MSG_PLAY_STOPPED -> {
                val playerFeedback = msg.obj as VideoPlayer.PlayerFeedback
                playerFeedback.playbackStopped()
            }
        }
    }
}