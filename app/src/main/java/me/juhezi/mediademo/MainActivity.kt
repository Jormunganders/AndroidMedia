package me.juhezi.mediademo

import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.provider.OpenableColumns
import android.text.Html
import android.util.Log
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableSource
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.operators.observable.ObservableMergeWithCompletable
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.demo_activity_main.*
import kotlinx.coroutines.*
import me.juhezi.mediademo.kuaishou.AsyncCacheLayoutInflater
import me.juhezi.mediademo.media.camera.CaptureActivity
import me.juhezi.mediademo.reactivex.ObservableControlWithCompletable
import java.io.FileDescriptor
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var mNumberLiveDate: MutableLiveData<Int>
    private var mIsLightStatusBar = false

    @OptIn(ExperimentalStdlibApi::class)
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
//        button_v2ex.setOnClickListener { startActivity(Intent(this, V2EXActivity::class.java)) }
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
            publish.onNext(System.currentTimeMillis().toString())
        }
        button_rxjava_demo_1.setOnClickListener {
            disposable?.dispose()
            disposable = publish.customTimeout(3000).subscribe({
                Log.i(TAG, "onNext: $it")
            }, {
                Log.i(TAG, "onError: $it")
            }, {
                Log.i(TAG, "onComplete")
            })
        }
    }

    private var disposable: Disposable? = null
    private val publish = PublishSubject.create<String>()

    /**
     * 另一种意义上的超时机制，timeoutMs 时间内没有发送数据，则认为是超时。
     * timeoutMs 后如果发射过数据，那么则发射 onComplete，否则发射 onError
     * timeoutMs <= 0 没有超时机制，不会发射 onComplete 或者 onError，需要主动 disposed
     * @param T
     * @param timeoutMs 单位：ms
     * @return
     */
    private fun <T> Observable<T>.customTimeout(timeoutMs: Long): Observable<T> {

        if (timeoutMs <= 0) {
            return this
        }

        // 是否已经发射过数据了
        var hasData = false

        fun delayCheckCompletable() = Completable.create {
            // 和 Completable.fromAction 一样
            // 这个部分有点丑陋，看有没有什么更好的方法
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    if (it.isDisposed) {
                        return@postDelayed
                    }
                    if (hasData) {
                        it.onComplete()
                    } else {
                        it.onError(Exception("超时了！"))
                    }
                }, timeoutMs
            )
        }
        return doOnNext {
            hasData = true
        }.controlBy(delayCheckCompletable())
    }

    fun <T> Observable<T>.controlBy(other: CompletableSource): Observable<T> {
        Objects.requireNonNull(other, "other is null")
        return RxJavaPlugins.onAssembly(ObservableControlWithCompletable(this, other))
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
                    image_display.setImageBitmap(getBitmapFromUri(it))
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

        cursor?.use { it ->
            if (it.moveToFirst()) {
                val displayName: String =
                    it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME).let { num ->
                        if (num < 0) {
                            0
                        } else {
                            num
                        }
                    })
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
