

package com.luck.picture.lib.rxbus2;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class RxUtils {

    public static <T> DisposableObserver computation(final RxUtils.RxSimpleTask task, Object... objects) {
        return computation(0, task, objects);
    }

    public static <T> DisposableObserver computation(long delayMilliseconds, final RxUtils.RxSimpleTask task, Object... objects) {
        Observable observable = Observable.create((e) -> {
            Object obj = task.doSth(objects);
            if (obj == null) {
                obj = new Object();
            }
            e.onNext(obj);
            e.onComplete();
        }).delay(delayMilliseconds, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
        DisposableObserver disposableObserver = new DisposableObserver<T>() {
            @Override
            public void onNext(T o) {
                if (!this.isDisposed()) {
                    task.onNext(o);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (!this.isDisposed()) {
                    task.onError(e);
                }
            }

            @Override
            public void onComplete() {
                if (!this.isDisposed()) {
                    task.onComplete();
                }
            }
        };
        observable.subscribe(disposableObserver);
        return disposableObserver;
    }


    public static <T> void newThread(final RxUtils.RxSimpleTask task, Object... objects) {
        newThread(0, task, objects);
    }

    public static <T> void newThread(long delayMilliseconds, final RxUtils.RxSimpleTask task, Object... objects) {
        Observable observable = Observable.create((e) -> {
//            LogUtils.i("newThread subscribe");
            Object obj = task.doSth(objects);
            if (obj == null) {
                obj = new Object();
            }
            e.onNext(obj);
            e.onComplete();
        }).delay(delayMilliseconds, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new DisposableObserver<T>() {
            @Override
            public void onNext(T o) {
                if (!this.isDisposed()) {
                    task.onNext(o);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (!this.isDisposed()) {
                    task.onError(e);
                }
            }

            @Override
            public void onComplete() {
                if (!this.isDisposed()) {
                    task.onComplete();
                }
            }
        });
    }

    public static <T> void io(final RxUtils.RxSimpleTask task) {
        io(0, task);
    }

    public static <T> void io(long delayMilliseconds, final RxUtils.RxSimpleTask task) {

        Observable observable = Observable.create((e) -> {
            Object obj = task.doSth(new Object[0]);
            if (obj == null) {
                obj = new <T>Object();
            }

            e.onNext(obj);
            e.onComplete();
        }).delay(delayMilliseconds, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new DisposableObserver<T>() {
            @Override
            public void onNext(T o) {
                if (!this.isDisposed()) {
                    task.onNext(o);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (!this.isDisposed()) {
                    task.onError(e);
                }
            }

            @Override
            public void onComplete() {
                if (!this.isDisposed()) {
                    task.onComplete();
                }
            }
        });
    }

    private RxUtils() {
    }


    public abstract static class RxSimpleTask<T> {

        public T getDefault() {
            return null;
        }

        public @NonNull
        T doSth(Object... objects) {
            return getDefault();
        }

        public void onNext(T returnData) {
        }

        public void onError(Throwable e) {
        }

        public void onComplete() {
        }
    }
}
