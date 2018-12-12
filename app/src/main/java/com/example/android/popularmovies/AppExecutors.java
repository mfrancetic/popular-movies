package com.example.android.popularmovies;

import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.os.Handler;


/**
 * Global executor pools for the whole application.
 */
class AppExecutors {

    /* Singleton instantiation */
    private static final Object LOCK = new Object();
    private static AppExecutors executors;
    private final Executor diskIO;
    private final Executor mainThread;
    private final Executor networkIO;

    private AppExecutors(Executor diskIO, Executor mainThread, Executor networkIO) {
        this.diskIO = diskIO;
        this.mainThread = mainThread;
        this.networkIO = networkIO;
    }

    static AppExecutors getExecutors() {
        if (executors == null) {
            synchronized (LOCK) {
                executors = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor());
            }
        }
        return executors;
    }

    Executor diskIO() {
        return diskIO;
    }

    Executor mainThread() {
        /* TODO check the unused methods in AppExecutors */
        return mainThread;
    }

    public Executor networkIO() {
        return networkIO;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}