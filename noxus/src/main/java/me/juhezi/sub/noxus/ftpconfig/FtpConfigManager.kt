package me.juhezi.sub.noxus.ftpconfig

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.room.Room
import com.google.gson.Gson
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import me.juhezi.sub.noxus.R
import me.juhezi.sub.noxus.ftpconfig.database.NoxusDatabase
import me.juhezi.sub.noxus.ftpconfig.database.userconfig.UserConfigDao
import me.juhezi.sub.noxus.ftpconfig.database.userconfig.UserConfigModel
import me.juhezi.sub.noxus.ftpconfig.model.FtpConfigGroup
import me.juhezi.sub.noxus.utils.readFromFile

@SuppressLint("StaticFieldLeak")
object FtpConfigManager {

    private const val TAG = "FtpConfigManager"

    private const val DEFAULT_CONFIG_FILE_NAME = "default_config.json"

    // 配置项前缀
    private const val CONFIG_PREFIX = "ftpserver.user"

    // 访客
    private const val USER_ANONYMOUS = "anonymous"

    // admin 用户
    private const val USER_ADMIN = "admin"

    // 从 json 文件中读取默认配置项
    private lateinit var defaultFtpConfigGroupFromJson: FtpConfigGroup
    private lateinit var context: Context
    private val dao: UserConfigDao by lazy {
        Room.databaseBuilder(
            context,
            NoxusDatabase::class.java,
            "ftp_server_config.db"
        )
            .build()
            .userConfigDao()
    }

    private val gson = Gson()

    /*
        * 1. 文件中读取
        * 2. 写入到数据库
        * 3. 从数据库中读取
        * 4. 写入文件中
        * */
    fun init(application: Application) {
        this.context = application
        asyncInit()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Boolean> {
                override fun onSubscribe(d: Disposable) {
                    Log.i(TAG, "onSubscribe: ")
                }

                override fun onNext(t: Boolean) {
                    Log.i(TAG, "onNext: $t")
                }

                override fun onError(e: Throwable) {
                    Log.i(TAG, "onError: ")
                    e.printStackTrace()
                }

                override fun onComplete() {
                    Log.i(TAG, "onComplete: ")
                }

            })
    }

    private fun asyncInit(): Observable<Boolean> = dao.getConfigCount().flatMap {
        Log.i(TAG, "asyncInit: configCount is $it")
        if (it > 0) {
            Log.i(TAG, "asyncInit: 数据库已经有数据了，不需要从 json 文件中读取数据了")
        } else {
            Log.i(TAG, "asyncInit: 从 json 文件中读取数据")
            loadDefaultConfigFromJson()
            saveDefaultConfigToDB()
        }
        Observable.just(false)
    }

    /**
     * 从 Json 文件中读取配置
     */
    @WorkerThread
    private fun loadDefaultConfigFromJson() {
        val stringFromJson = context.assets.readFromFile(DEFAULT_CONFIG_FILE_NAME)
        defaultFtpConfigGroupFromJson = gson.fromJson(stringFromJson, FtpConfigGroup::class.java)
    }

    /**
     * 保存 Json 文件中读取出的默认配置（访客配置和公共配置）
     */
    @WorkerThread
    private fun saveDefaultConfigToDB() {

        fun saveCommonConfig() {
            if (!::defaultFtpConfigGroupFromJson.isInitialized) {
                return
            }
            dao.insertAll(defaultFtpConfigGroupFromJson.commonConfigProperties
                .filter { it.key.isNotEmpty() }
                .map {
                    UserConfigModel(
                        it.key,
                        it.value,
                        it.desc,
                        it.type,
                        false
                    )
                })
        }

        fun saveAnonymousUserConfig() {
            saveUserConfig(USER_ANONYMOUS)
        }
        // 存储公共配置
        saveCommonConfig()
        // 存储访客配置
        saveAnonymousUserConfig()
        saveUserConfig(USER_ADMIN)
    }

    @WorkerThread
    private fun saveUserConfig(user: String) {
        if (!::defaultFtpConfigGroupFromJson.isInitialized) {
            return
        }
        dao.insertAll(defaultFtpConfigGroupFromJson.userConfigProperties
            .filter { it.key.isNotEmpty() }
            .map {
                UserConfigModel(
                    "$CONFIG_PREFIX.$user.${it.key}".apply {
                        // TODO: WILL REMOVE
//                        Log.i(TAG, "saveUserConfig: $this")
                    },
                    it.value,
                    it.desc,
                    it.type,
                    true,
                    user
                )
            })
    }

    /**
     * 生成配置文件
     * todo
     */
    private fun generatePropertyFile() {

    }

    // todo temp
    private fun getAllUser(): Observable<List<Pair<String, String>>> = dao.getAllUser().map {
        it.map { user ->
            if (USER_ANONYMOUS == user) {
                // TODO: read string res need opt
                user to context.resources.getString(R.string.anonymous)
            } else {
                user to user
            }
        }
    }

    private fun getCommonConfigs(): Observable<List<UserConfigModel>> = dao.getCommonConfigs()

    /**
     * 返回简单的配置信息
     * 1. 公共配置
     * 2. 用户信息
     */
    fun getLiteConfig(): Observable<List<Any>> =
        Observable.zip(getAllUser(), getCommonConfigs()) { userList, commonConfigs ->
            buildList {
                addAll(commonConfigs)
                // TODO: read string res need opt
                add(context.resources.getString(R.string.user_info))
                addAll(userList)
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

}
