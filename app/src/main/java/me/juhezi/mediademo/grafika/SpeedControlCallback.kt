package me.juhezi.mediademo.grafika

import android.util.Log
import me.juhezi.mediademo.media.utils.logd
import me.juhezi.mediademo.media.utils.loge
import me.juhezi.mediademo.media.utils.logi
import kotlin.math.abs

private const val ONE_MILLION = 1000000L
private const val TAG = "SpeedControlCallback"

/**
 * 控制视频播放 FPS
 *
 */
class SpeedControlCallback : VideoPlayer.FrameCallback {

    private var prevPresentUsec: Long = 0   // 上一帧的 PTS
    private var prevMonoUsec: Long = 0
    private var fixedFrameDurationUsec: Long = 0
    private var loopReset = false

    /**
     * 设置特定的 FPS，如果设置了，那么将会忽略 pts
     */
    fun setFps(fps: Int) {
        fixedFrameDurationUsec = ONE_MILLION / fps
    }

    /**
     * 解码线程
     *
     * 丢帧或者 Sleep
     */
    override fun preRender(presentationTimeUsec: Long) {
        // 第一帧
        if (prevMonoUsec == 0L) {
            prevMonoUsec = System.nanoTime() / 1000
            prevPresentUsec = presentationTimeUsec
        } else {
            // 此帧和上一帧的时间差
            if (loopReset) {
                prevPresentUsec = presentationTimeUsec - ONE_MILLION / 30
                loopReset = false
            }
            var frameDelta = if (fixedFrameDurationUsec != 0L) {
                // 忽略 PTS
                fixedFrameDurationUsec
            } else {
                presentationTimeUsec - prevPresentUsec
            }
            when {
                frameDelta < 0 -> {
                    loge(TAG, "frameDelta < 0")
                    frameDelta = 0
                }
                frameDelta == 0L -> {
                    loge(TAG, "frameDelta = 0")
                }
                frameDelta > 10 * ONE_MILLION -> {
                    logi(
                        TAG,
                        "Inter-frame pause was ${frameDelta / ONE_MILLION} sec, capping at 5 sec"
                    )
                    frameDelta = 5 * ONE_MILLION
                }
            }

            val desireUsec = prevMonoUsec + frameDelta  // 期望唤醒的时间
            var nowUsec = System.nanoTime() / 1000
            while (nowUsec < (desireUsec - 100)) {

                var sleepTimeUsec = desireUsec - nowUsec

                // sleep 时间太长，每半秒唤醒一次
                if (sleepTimeUsec > 500000) {
                    sleepTimeUsec = 500000
                }
                try {
                    val startNesc = System.nanoTime()
                    Thread.sleep(sleepTimeUsec / 1000, ((sleepTimeUsec % 1000) * 1000).toInt())
                    val actualSleepNesc = System.nanoTime() - startNesc
                    logd(
                        TAG,
                        "sleep=$sleepTimeUsec actual=${actualSleepNesc / 1000} diff=${(abs(
                            actualSleepNesc / 1000 - sleepTimeUsec
                        ))}(usec)"
                    )
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                nowUsec = System.nanoTime() / 1000
            }
            prevMonoUsec += frameDelta
            prevPresentUsec += frameDelta
        }
    }

    override fun postRender() {
    }

    override fun loopReset() {
        loopReset = true
    }

}