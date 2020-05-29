package me.juhezi.mediademo

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.demo_activity_main.*
import kotlinx.coroutines.*
import java.io.FileDescriptor
import java.io.IOException

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    val TAG = "Juhezi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_activity_main)
        button_video_player.setOnClickListener {
            startActivity(Intent(this, VideoPlayerActivity::class.java))
        }
        button_pick.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            startActivityForResult(intent, 123)
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            data?.data?.also {
                logi(TAG, "Uri $it")
                dumpImageMetaData(it)
                launch {
                    image_display.setImageBitmap(getBitmapFromUri(it))
                }
                logw(
                    TAG,
                    "host:${it.host}\tpath:${it.path}\tscheme:${it.scheme}"
                )
            }
        }
    }

    private fun dumpImageMetaData(uri: Uri) {

        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val displayName: String =
                    it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                Log.i(TAG, "Display Name: $displayName")
                val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
                val size: String = if (!it.isNull(sizeIndex)) {
                    it.getString(sizeIndex)
                } else {
                    "Unknown"
                }
                Log.i(TAG, "Size: $size")
            }
        }
    }

    @Throws(IOException::class)
    private suspend fun getBitmapFromUri(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        logi(
            TAG,
            "getBitmapFromUri Thread: ${Thread.currentThread()}"
        )
        val parcelFileDescriptor: ParcelFileDescriptor =
            contentResolver.openFileDescriptor(uri, "r")!!
        val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
        logi(TAG, "fd is $fileDescriptor")
        val image: Bitmap? = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return@withContext image
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }



}

/*
Image: content://com.android.providers.media.documents/document/image%3A150999
Video：content://com.android.providers.media.documents/document/video%3A149619
Audio：content://com.android.providers.media.documents/document/audio%3A16
Other：content://com.android.providers.downloads.documents/document/raw%3A%2Fstorage%2Femulated%2F0%2FDownload%2F%E5%BC%80%E8%A8%80%E8%8B%B1%E8%AF%AD.apk
 */

/*
class ScopedActivity : AppCompatActivity(), CoroutineScope {

    lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 当 Activity 销毁的时候取消该 Scope 管理的 job。
        // 这样在该 Scope 内创建的子 Coroutine 都会被自动的取消
        job.cancel()
    }

    */
/*
    * 注意 coroutine builder 的 scope， 如果 activity 被销毁了或者该函数内创建的 Coroutine
    * 抛出异常了，则所有子 Coroutines 都会被自动取消。不需要手工去取消。
    *//*

    fun loadDataFromUI() = launch { // 继承当前 activity 的 scope context，所以可以在主线程运行
        val ioData = async(Dispatchers.IO) {
            delay(10000)
            "HelloWorld"
        }
        val data = ioData.await()   // 等待阻塞 I/O 操作的返回结果
//        show(data)
    }

}
*/

/**
 * 更简单的写法
 */
/*
class ScopedActivity : AppCompatActivity(),
    CoroutineScope by MainScope()   // 指定代理实现
{

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

}
*/
