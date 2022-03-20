package me.juhezi.sub.noxus.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer

class AcceptedLiveData<T> : MutableLiveData<T>, Consumer<T> {
    constructor(value: T) : super(value)
    constructor() : super()

    override fun accept(t: T) {
        value = t
    }

}