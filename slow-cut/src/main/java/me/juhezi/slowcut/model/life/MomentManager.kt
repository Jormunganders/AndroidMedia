package me.juhezi.slowcut.model.life

import androidx.room.Room
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import me.juhezi.slow_cut_base.GlobalConfig
import me.juhezi.slowcut.model.SlowCutDatabase
import me.juhezi.slowcut.model.life.db.entry.Moment

object MomentManager {

    @JvmStatic
    fun getMoments(): Flowable<List<Moment>> =
        SlowCutDatabase
            .getInstance(GlobalConfig.getApplicationContext())
            .momentDao()
            .getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

}