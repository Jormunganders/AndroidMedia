package me.juhezi.mediademo;

import org.junit.Test;

import java.util.Comparator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.Transformer;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Predicate;
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

    @Test
    public void function2() {
        Observable<Integer> observable = Observable.fromArray(1, 0, 3, 9, 4, 6, 5, 9, 8, 2, 10, 22, 31)
                .filter(integer -> {
                    System.out.println("过滤");
                    return integer < 0;
                }).sorted((o1, o2) -> {
                    System.out.println("排序");
                    return o1 - o2;
                }).cache();
        System.out.println("列表 " + observable.toList().blockingGet());
        int result = observable.first(-2).blockingGet();
        System.out.println("Result " + result);
    }

    @Test
    public void function3() {
        Disposable disposable = Observable.create((ObservableOnSubscribe<String>) emitter -> {
//            emitter.onNext("*");
            for (int i = 0; i < 10; i++) {
//                System.out.println("Nice " + i + "\t" + Thread.currentThread().isInterrupted());
                try {
                    Thread.sleep(500);
                } catch (Exception e) {

                }
                emitter.onNext("onNext " + i);
            }
            emitter.onNext("?");
            System.out.println("Emit " + Thread.currentThread().getName());
        }).subscribeOn(Schedulers.newThread())
                .zipWith(Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
                    for (int i = 0; i < 4; i++) {
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {

                        }
                        emitter.onNext(1000 + i);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                    }
                    emitter.onError(new Exception("洗吧"));
                }).subscribeOn(Schedulers.newThread()), (BiFunction<String, Integer, Object>) (s, integer) -> {
                    System.out.println("zip " + s + "\t" + integer);
                    return "Hello " + s;
                })
                .observeOn(Schedulers.io())
//                .timeout(2, TimeUnit.SECONDS)
                .subscribe(s -> {
                    System.out.println("Subscribe " + s + "\t" + Thread.currentThread().getName());
                }, e -> {
                    System.out.println("Error! " + e.getMessage() + "\t" + e);
                });
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        disposable.dispose();
//        System.out.println("disposable " + Thread.currentThread().getName());
//        Thread t = new Thread();
//        t.interrupt();
    }

}
