package me.juhezi.mediademo;

import org.junit.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import javax.xml.transform.Transformer;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.schedulers.IoScheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class RxJavaExampleUnitTest {

    @Test
    public void function() {
        // 被观察者
        /*Observable.create((ObservableOnSubscribe<String>)
                emitter -> {
                    for (int i = 0; i < 5; i++) {
                        emitter.onNext("index " + i);
                    }
                    emitter.onNext("HelloWorld, I am ready.");
                    emitter.onNext("Juhezi");
                    emitter.onNext("End");
                    emitter.onComplete();
                    emitter.onNext("More");     // 无用
                })
                .filter(string -> !"Juhezi".equals(string))
                .subscribe(string -> System.out.println("Content: " + string));*/
        /*Observable.fromArray(0, 1, 2, 3, 4, 5, 3, 1)
                .distinct()
                .scan(10, (first, second) -> first + second)
                .subscribe(num -> System.out.println(num + ""));*/
        /*Observable.merge(Observable.just(1, 3, 5), Observable.just(2, 4, 6))
                .subscribeOn(new IoScheduler());*/
        PublishSubject<Integer> subject = PublishSubject.create();
        subject.subscribe(num -> {
            System.out.println(num);
            System.out.println("Subscribe Thread: " + Thread.currentThread().getName());
        });

        Executor executor = Executors.newFixedThreadPool(5);
        Observable.range(1, 5)
                .compose(applySchedulers())
                .subscribe(i -> {
                    System.out.println("Outter Thread: " + Thread.currentThread().getName());
                    executor.execute(() -> {
                        try {
                            Thread.sleep(i * 200);
                            System.out.println("Inner Thread: " + Thread.currentThread().getName());
                            subject.onNext(i);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    });
                });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private <T> ObservableTransformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.single())    // 消费
                .observeOn(Schedulers.newThread()); // 生产
    }

}
