package me.juhezi.mediademo

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.File
import java.nio.ByteBuffer

const val URL = "/storage/emulated/0/in.mp4"
const val OUT_URL = "/storage/emulated/0/out.mp4"
const val TAG = "Juhezi"

class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private var videoPlayerThread: VideoPlayerThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val surfaceView = SurfaceView(this)
        surfaceView.holder.addCallback(this)
        setContentView(surfaceView)
        Thread {
            process()
        }.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        /*if (videoPlayerThread == null) {
            videoPlayerThread = VideoPlayerThread(holder!!.surface)
            videoPlayerThread!!.start()
        }*/
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
    }

    private class VideoPlayerThread(var surface: Surface) : Thread() {

        private lateinit var decoder: MediaCodec

        override fun run() {
            val extractor = MediaExtractor()
            extractor.setDataSource(URL)
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime.startsWith("video/")) {
                    extractor.selectTrack(i)
                    decoder = MediaCodec.createDecoderByType(mime)
                    decoder.configure(format, surface, null, 0)
                    break
                }
            }

            decoder.start()
            val inputBuffers = decoder.getInputBuffers()
            var outputBuffers = decoder.getOutputBuffers()
            val info = MediaCodec.BufferInfo()
            var eos = false
            val startMs = System.currentTimeMillis()
            while (!interrupted()) {
                if (!eos) {
                    val inIndex = decoder.dequeueInputBuffer(1000)
                    if (inIndex >= 0) {
                        val buffer = inputBuffers[inIndex]
                        val sampleSize = extractor.readSampleData(buffer, 0)    // 读取数据到 buffer 中
                        if (sampleSize < 0) {
                            decoder.queueInputBuffer(
                                inIndex,
                                0,
                                0,
                                0,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM
                            )
                            eos = true
                        } else {
                            decoder.queueInputBuffer(
                                inIndex,
                                0,
                                sampleSize,
                                extractor.sampleTime,
                                0
                            )
                            extractor.advance() // 读取下一帧数据
                        }
                    }
                }

                when (val outIndex = decoder.dequeueOutputBuffer(info, 1000)) {
                    MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED ->
                        outputBuffers = decoder.outputBuffers
                    MediaCodec.INFO_OUTPUT_FORMAT_CHANGED ->
                        Unit
                    MediaCodec.INFO_TRY_AGAIN_LATER ->
                        Unit
                    else -> {
                        val buffer = outputBuffers[outIndex]
                        while (info.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
                            sleep(10)
                        }
                        decoder.releaseOutputBuffer(outIndex, true)
                    }
                }
                if ((info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    // END OF STREAM
                    break
                }
            }

            decoder.stop()
            decoder.release()
            extractor.release()
        }

    }


    //----- 把 MP4 文件中的视频轨提取出来

    private fun process(): Boolean {
        var extractor = MediaExtractor()
        var muxer: MediaMuxer? = null
        extractor.setDataSource(URL)

        var videoIndex = -1
        var frameRate = 0
        for (i in 0 until extractor.trackCount) {
            var format = extractor.getTrackFormat(i)
            var mime = format.getString(MediaFormat.KEY_MIME)
            if (!mime.startsWith("video/")) {
                continue
            }
            frameRate = format.getInteger(MediaFormat.KEY_FRAME_RATE)
            extractor.selectTrack(i)
            File(OUT_URL).delete()
            muxer = MediaMuxer(OUT_URL, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            videoIndex = muxer.addTrack(format)
            muxer.start()
        }

        if (muxer == null) {
            return false
        }

        var info = MediaCodec.BufferInfo()
        info.presentationTimeUs = 0 // pts
        var buffer = ByteBuffer.allocate(500 * 1024)
        var sampleSize = extractor.readSampleData(buffer, 0)
        while (sampleSize > 0) {
            info.offset = 0
            info.size = sampleSize
            info.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME
            info.presentationTimeUs += 1000 * 1000 / frameRate

            muxer.writeSampleData(videoIndex, buffer, info)
            extractor.advance()

            // 这是一个比较丑陋的循环，Kotlin 不支持
            sampleSize = extractor.readSampleData(buffer, 0)
        }

        extractor.release()
        muxer.stop()
        muxer.release()
        return true
    }

}


