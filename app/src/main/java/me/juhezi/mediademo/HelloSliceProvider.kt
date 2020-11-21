package me.juhezi.mediademo

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.SliceAction
import androidx.slice.builders.list
import androidx.slice.builders.row

class HelloSliceProvider : SliceProvider() {

    override fun onCreateSliceProvider(): Boolean {
        return true
    }

    override fun onBindSlice(sliceUri: Uri): Slice? {
        val context = context ?: return null
        val activityAction = createActivityAction() ?: return null
        return if (sliceUri.path == "/hello") {
            list(context, sliceUri, ListBuilder.INFINITY) {
                row {
                    primaryAction = activityAction
                    title = "HelloWorld."
                }
            }
        } else {
            list(context, sliceUri, ListBuilder.INFINITY) {
                row {
                    primaryAction = activityAction
                    title = "URI 没有找到"
                }
            }
        }
    }

    private fun createActivityAction(): SliceAction {
        return SliceAction.create(
            PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0),
            IconCompat.createWithResource(context, android.R.drawable.ic_media_ff),
            ListBuilder.ICON_IMAGE,
            "打开 App"
        )
    }
}
