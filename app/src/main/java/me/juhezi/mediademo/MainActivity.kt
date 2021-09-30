package me.juhezi.mediademo

import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.text.Html
import android.util.Log
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.zipWith
import kotlinx.android.synthetic.main.demo_activity_main.*
import kotlinx.coroutines.*
import me.juhezi.mediademo.broom.activity.V2EXActivity
import me.juhezi.mediademo.kuaishou.AsyncCacheLayoutInflater
import me.juhezi.mediademo.media.camera.CaptureActivity
import java.io.FileDescriptor
import java.io.IOException

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var mNumberLiveDate: MutableLiveData<Int>
    private var mIsLightStatusBar = false

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_activity_main)
        mNumberLiveDate = MutableLiveData()
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
        button_litho.setOnClickListener {
            startActivity(Intent(this, LithoActivity::class.java))
        }
        button_capture.setOnClickListener {
            startActivity(Intent(this, CaptureActivity::class.java))
        }
        button_async_inflate.setOnClickListener {
            AlertDialog.Builder(this)
                .setView(
                    AsyncCacheLayoutInflater.getCacheOrInflate(
                        this,
                        R.layout.layout_dialog_test,
                        null
                    )
                )
                .setCancelable(true)
                .show()
        }
        button_v2ex.setOnClickListener { startActivity(Intent(this, V2EXActivity::class.java)) }
        AsyncCacheLayoutInflater.createAndCacheViewAsync(
            this,
            R.layout.layout_dialog_test,
            null
        )
        AsyncCacheLayoutInflater.createAndCacheViewAsync(
            this,
            R.layout.layout_activity_v2ex,
            null
        )
        text_live_data.paint.isFakeBoldText = true
        lifecycle.addObserver(DemoObserver())
        mNumberLiveDate.observe(this,
            Observer<Int> {
                logi(TAG, "LiveData Change: $it")
                text_live_data.text = Html.fromHtml("S<font color='#FF5000'>$it</font>")
            })
        button_v2ex.setOnLongClickListener {
            launch {
                updateNumber()
            }
            true
        }
        button_status_bar.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // 启动浅色状态栏
                button_v2ex.windowInsetsController?.setSystemBarsAppearance(
                    if (mIsLightStatusBar) {
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    } else {
                        0
                    }, // value
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS // mask
                )
                mIsLightStatusBar = !mIsLightStatusBar
            }
        }
        button_rxjava_demo.setOnClickListener {
            testRxJava()
        }
        button_hw_window_test.setOnClickListener {
            with(window.decorView as ViewGroup) {
                val root = get(0)
                removeView(root)
                val wrapper = FrameLayout(context)
                wrapper.addView(root)
                addView(wrapper)
            }

        }
    }

    private suspend fun updateNumber() = withContext(Dispatchers.IO) {
        var number = 0
        while (number < 13) {
            delay(3000)
            number++
            mNumberLiveDate.postValue(number)
            logi(TAG, "Thread: ${Thread.currentThread().name}\tNumber: $number")
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            data?.data?.also {
                logi(Companion.TAG, "Uri $it")
                dumpImageMetaData(it)
                launch {
                    val bitmap = getBitmapFromUri(it)
                    image_display.setImageBitmap(bitmap)
                    val bitmapDrawable = BitmapDrawable(bitmap)
                    root.background = bitmapDrawable
                    delay(3000)
                    bitmap?.recycle()
                    Log.i("Juhezi", "onActivityResult: 图片回收 ${bitmap?.isRecycled}")
                    root.invalidate()
                }
                logw(
                    Companion.TAG,
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
                Log.i(Companion.TAG, "Display Name: $displayName")
                val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
                val size: String = if (!it.isNull(sizeIndex)) {
                    it.getString(sizeIndex)
                } else {
                    "Unknown"
                }
                Log.i(Companion.TAG, "Size: $size")
            }
        }
    }

    @Throws(IOException::class)
    private suspend fun getBitmapFromUri(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        logi(
            Companion.TAG,
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

    companion object {
        const val TAG = "Juhezi"
    }

    fun testRxJava() {
        val originObservable: Observable<String> = Observable.create<String> {
            logi(TAG, "执行！")
            it.onNext("Nice")
        }

        val checkObservable = Observable.create<Boolean> {
            if (System.currentTimeMillis() % 2L == 0L) {
                it.onNext(true)
            } else {
                it.onError(Exception("中断"))
            }
        }
        checkObservable.flatMap { originObservable }
            .subscribe(
                {
                    logi(TAG, "执行结果：$it")
                }, {
                    it.printStackTrace()
                }
            )
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
