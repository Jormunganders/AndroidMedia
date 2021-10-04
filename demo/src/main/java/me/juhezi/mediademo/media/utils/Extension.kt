package me.juhezi.mediademo.media.utils

import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import me.juhezi.mediademo.isTrue

enum class TrackType(val mime: String) {
    Video("video/"),
    Audio("audio/")
}

fun MediaExtractor.selectTrack(type: TrackType = TrackType.Video): Int {
    for (i in 0 until trackCount) {
        val format = getTrackFormat(i)
        val mime = format.getString(MediaFormat.KEY_MIME)
        if (mime?.startsWith(type.mime) == true) {
            return i
        }
    }
    return -1
}