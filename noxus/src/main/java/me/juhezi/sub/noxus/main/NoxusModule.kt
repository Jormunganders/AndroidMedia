package me.juhezi.sub.noxus.main

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import me.juhezi.slow_cut_base.adaptermanager.BaseAdapter
import me.juhezi.sub.noxus.base.adaptermanager.NoxusDelegateStore

@Module
@InstallIn(FragmentComponent::class)
object NoxusModule {

    @Provides
    fun provideLiteConfigAdapter() = BaseAdapter.Builder().apply {
        delegateStore = NoxusDelegateStore
    }.build()

}