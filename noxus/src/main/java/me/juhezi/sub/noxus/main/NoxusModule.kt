package me.juhezi.sub.noxus.main

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object NoxusModule {

    @Provides
    fun provideLiteConfigAdapter() = LiteConfigAdapter()

}