package me.juhezi.mediademo

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView

const val URL = "/storage/emulated/0/in.mp4"
const val TAG = "Juhezi"

class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private var videoPlayerThread: VideoPlayerThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val surfaceView = SurfaceView(this)
        surfaceView.holder.addCallback(this)
        setContentView(surfaceView)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        if (videoPlayerThread == null) {
            videoPlayerThread = VideoPlayerThread(holder!!.surface)
            videoPlayerThread!!.start()
        }
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
                        val sampleSize = extractor.readSampleData(buffer, 0)
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
                            extractor.advance()
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

}


