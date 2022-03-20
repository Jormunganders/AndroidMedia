package me.juhezi.sub.noxus.ui.main

import android.graphics.Rect
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trello.rxlifecycle4.components.support.RxFragment
import me.juhezi.sub.noxus.R
import me.juhezi.sub.noxus.ftpconfig.FtpConfigManager
import org.apache.ftpserver.FtpServer
import org.apache.ftpserver.FtpServerFactory
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory
import java.io.File

class NoxusFragment : RxFragment() {

    companion object {
        fun newInstance() = NoxusFragment()

        private const val TAG = "NoxusFragment"

    }

    private lateinit var viewModel: MainViewModel
    private lateinit var mFtpServer: FtpServer
    private val dirname = "/storage/emulated/0/noxus"
    private val configFile = "$dirname/users.properties"

    private lateinit var mLiteConfigList: RecyclerView
    private val mAdapter: LiteConfigAdapter by lazy {
        LiteConfigAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.noxus_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        /*FtpConfigManager.getAllUser().compose(bindToLifecycle()).subscribe {
            Log.i(TAG, "onActivityCreated: ${it.size}")
            it.forEach { pair ->
                Log.i(TAG, "onActivityCreated: $pair")
            }

        }*/
        FtpConfigManager.getLiteConfig().compose(bindToLifecycle()).subscribe {
            /*Log.i(TAG, "onActivityCreated: ${it.size}")
            it.forEach { config ->
                Log.i(TAG, "onActivityCreated: $config")
            }*/
            mAdapter.setDataList(it)
        }
        initLiteConfigList()
    }

    private fun initLiteConfigList() {
        mLiteConfigList = requireView().findViewById(R.id.lite_config_list)
        mLiteConfigList.layoutManager = LinearLayoutManager(context)
        mLiteConfigList.adapter = mAdapter
        mLiteConfigList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun startFtpServer() {
        val serverFactory = FtpServerFactory()
        val userManagerFactory = PropertiesUserManagerFactory()
        userManagerFactory.file = File(configFile)
        serverFactory.userManager = userManagerFactory.createUserManager()
        val factory = ListenerFactory()
        factory.port = 12138
        factory.serverAddress = "192.168.31.164"
        serverFactory.addListener("default", factory.createListener())
        mFtpServer = serverFactory.createServer()
        /*Thread {
            mFtpServer.start()
        }.start()*/
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mFtpServer.isInitialized) {
            mFtpServer.stop()
        }
    }

}