package me.juhezi.mediademo

import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Test


class AngleTest {

    @Test
    fun hello() {
        val anim = Observable.create<String> {

            try {
                Thread.sleep(3000)
            } catch (e : Exception) {

            }
            println("Anim Next")
//            it.onError(Exception("启动动画失败"))
            it.onNext("Anim")
        }.subscribeOn(Schedulers.newThread())
        val request = Observable.create<String> {
            try {
                Thread.sleep(2900)
            } catch (e : Exception) {

            }
            println("Request Next")
//            it.onNext("Response")
            it.onError(Exception("网络请求失败"))
        }.startWithItem("Request Init")
            .onErrorReturnItem("Request Init")
            .subscribeOn(Schedulers.newThread())
        /*o1.join(o2, {

        },{

        },{

        }).observeOn(Schedulers.io())
        .subscribe {
            println("subscribe $it")
        }*/

        /*anim.join(request, {   // 接收从源发射的数据
//            println("from source is $it")
            Observable.just("Anim again")
        }, {    // 接收从目标发射的数据
//            println("from target is $it")
            Observable.just("Response again")
        }, { a, b ->    // 同时接收两者的数据
            "?"
        }).observeOn(Schedulers.io())
            .subscribe {
                println("subscribe $it")
            }*/
        Observable.combineLatest(anim, request) { fromAnim: String, fromRequest: String ->
            "$fromAnim == $fromRequest"
        }.observeOn(Schedulers.io())
            .take(1)
            .subscribe({
                println("subscribe $it")
            }, {
                println("error " + it)
            }, {
                println("Complete")
            })
        Thread.sleep(10000)
    }

}