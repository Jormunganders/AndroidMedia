package me.juhezi.mediademo.media.utils

import android.media.MediaExtractor
import android.media.MediaFormat

enum class TrackType(val mime: String) {
    Video("video/"),
    Audio("audio/")
}

fun MediaExtractor.selectTrack(type: TrackType = TrackType.Video): Int {
    for (i in 0 until trackCount) {
        val format = getTrackFormat(i)
        val mime = format.getString(MediaFormat.KEY_MIME)
        if (mime?.startsWith(type.mime).isTrue()) {
            return i
        }
    }
    return -1
}

fun Boolean?.isTrue() = this ?: false