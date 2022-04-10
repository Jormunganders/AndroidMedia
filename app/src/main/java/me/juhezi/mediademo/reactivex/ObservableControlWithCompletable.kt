package me.juhezi.mediademo.reactivex

import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.disposables.DisposableHelper
import io.reactivex.rxjava3.internal.util.AtomicThrowable
import io.reactivex.rxjava3.internal.util.HalfSerializer
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class ObservableControlWithCompletable<T>(
    private val source: Observable<T>,
    private val other: CompletableSource
) : Observable<T>() {

    override fun subscribeActual(observer: Observer<in T>) {
        val parent = MergeWithObserver(observer)
        observer.onSubscribe(parent)
        source.subscribe(parent)
        other.subscribe(parent.otherObserver)
    }

    // TODO: 改名
    /**
     * downstream 下游
     * 即是观察者又是被观察者
     * 继承 AtomicInteger 的作用？
     */
    class MergeWithObserver<T>(val downstream: Observer<in T>) :
        AtomicInteger(), Observer<T>, Disposable {
        private val mainDisposable = AtomicReference<Disposable>()
        val otherObserver = OtherObserver(this)
        private val errors = AtomicThrowable()

        @Volatile
        var mainDone = false

        @Volatile
        var otherDone = false

        override fun onSubscribe(d: Disposable) {
            // 作用？
            DisposableHelper.setOnce(mainDisposable, d)
        }

        // 上游来的数据
        override fun onNext(t: T) {
            HalfSerializer.onNext(downstream, t, this, errors)
        }

        override fun onError(ex: Throwable) {
            DisposableHelper.dispose(otherObserver)
            HalfSerializer.onError(downstream, ex, this, errors)
        }

        override fun onComplete() {
            /*mainDone = true
            if (otherDone) {
                HalfSerializer.onComplete(downstream, this, errors)
            }*/
            HalfSerializer.onComplete(downstream, this, errors)
        }

        override fun isDisposed(): Boolean {
            return DisposableHelper.isDisposed(mainDisposable.get())
        }

        override fun dispose() {
            DisposableHelper.dispose(mainDisposable)
            DisposableHelper.dispose(otherObserver)
            errors.tryTerminateAndReport()
        }

        fun otherError(ex: Throwable?) {
            DisposableHelper.dispose(mainDisposable)
            HalfSerializer.onError(downstream, ex, this, errors)
        }

        fun otherComplete() {
            /*otherDone = true
            if (mainDone) {
                HalfSerializer.onComplete(downstream, this, errors)
            }*/
            HalfSerializer.onComplete(downstream, this, errors)
        }

        class OtherObserver(val parent: MergeWithObserver<*>) :
            AtomicReference<Disposable?>(),
            CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                DisposableHelper.setOnce(this, d)
            }

            override fun onError(e: Throwable) {
                parent.otherError(e)
            }

            override fun onComplete() {
                parent.otherComplete()
            }
        }

        override fun toByte(): Byte {
            TODO("Not yet implemented")
        }

        override fun toChar(): Char {
            TODO("Not yet implemented")
        }

        override fun toShort(): Short {
            TODO("Not yet implemented")
        }
    }

}