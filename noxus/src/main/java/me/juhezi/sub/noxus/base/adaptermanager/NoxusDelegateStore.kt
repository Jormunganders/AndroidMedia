package me.juhezi.sub.noxus.base.adaptermanager

import me.juhezi.slow_cut_base.adaptermanager.AdapterDelegateStore
import me.juhezi.slow_cut_base.adaptermanager.GlobalDelegateStore
import me.juhezi.sub.noxus.main.delegates.ConfigDelegate
import me.juhezi.sub.noxus.main.delegates.UserInfoDelegate
import me.juhezi.sub.noxus.main.delegates.UserInfoTitleDelegate

object NoxusDelegateStore : AdapterDelegateStore(GlobalDelegateStore) {

    init {
        registerAdapterDelegate(UserInfoTitleDelegate())
        registerAdapterDelegate(UserInfoDelegate())
        registerAdapterDelegate(ConfigDelegate())
    }

}