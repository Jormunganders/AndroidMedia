package me.juhezi.sub.noxus.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.juhezi.sub.noxus.R
import org.apache.ftpserver.FtpServer
import org.apache.ftpserver.FtpServerFactory
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory
import java.io.File

class NoxusFragment : Fragment() {

    companion object {
        fun newInstance() = NoxusFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var mFtpServer: FtpServer
    private val dirname = "/storage/emulated/0/noxus"
    private val configFile = "$dirname/users.properties"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.noxus_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
        startFtpServer()
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