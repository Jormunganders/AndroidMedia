package me.juhezi.mediademo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.demo_activity_main.*
import kotlinx.coroutines.*
import me.juhezi.mediademo.media.utils.logi
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_activity_main)
        button_video_player.setOnClickListener {
            startActivity(Intent(this, VideoPlayerActivity::class.java))
        }
        button_capture.setOnClickListener {
            startActivity(Intent(this, CaptureActivity::class.java))
        }
    }

}

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
