package me.juhezi.sub.noxus.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trello.rxlifecycle4.components.support.RxFragment
import dagger.hilt.android.AndroidEntryPoint
import me.juhezi.slow_cut_base.adaptermanager.BaseAdapter
import me.juhezi.sub.noxus.R
import me.juhezi.sub.noxus.databinding.NoxusFragmentBinding
import org.apache.ftpserver.FtpServer
import org.apache.ftpserver.FtpServerFactory
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class NoxusFragment : RxFragment() {

    companion object {
        fun newInstance() = NoxusFragment()

        private const val TAG = "NoxusFragment"

    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: NoxusFragmentBinding

    private lateinit var mFtpServer: FtpServer
    private val dirname = "/storage/emulated/0/noxus"
    private val configFile = "$dirname/users.properties"

    private lateinit var mLiteConfigList: RecyclerView

    @Inject
    lateinit var mAdapter: BaseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NoxusFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        initLiteConfigList()
        viewModel.liteConfigsLiveData.observe(viewLifecycleOwner) {
            mAdapter.edit().notifyDataChanged(it)
        }

    }

    private fun initLiteConfigList() {
        mLiteConfigList = binding.liteConfigList
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