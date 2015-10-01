package com.mobile.nhut.firebase.offline.threading;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadExecutor implements Executor {

  private static final int INITIAL_POOL_SIZE = 1;

  private static final int MAX_POOL_SIZE = 7;

  private static final int THREAD_ALIVE_TIME = 15;

  private static final TimeUnit ALIVE_TIME_UNIT = TimeUnit.SECONDS;

  private static ThreadExecutor sInstance;

  private final BlockingQueue<Runnable> mBlockingQueue;

  private final ThreadPoolExecutor mThreadPoolExecutor;

  private final ThreadFactory mThreadFactory;

  public ThreadExecutor() {
    this.mBlockingQueue = new LinkedBlockingQueue<>();
    this.mThreadFactory = new WorkerThreadFactory();
    this.mThreadPoolExecutor = new ThreadPoolExecutor(INITIAL_POOL_SIZE, MAX_POOL_SIZE,
            THREAD_ALIVE_TIME, ALIVE_TIME_UNIT, this.mBlockingQueue, this.mThreadFactory);
  }

  public static ThreadExecutor getInstance() {
    if (sInstance == null) {
      synchronized (ThreadExecutor.class) {
        if (sInstance == null) {
          sInstance = new ThreadExecutor();
        }
      }
    }
    return sInstance;
  }

  @Override
  public void execute(Runnable runnable) {
    if (runnable == null) {
      throw new IllegalArgumentException("Runnable cannot be null");
    }
    this.mThreadPoolExecutor.execute(runnable);
  }

  private static class WorkerThreadFactory implements ThreadFactory {

    private static final String THREAD_NAME = "rxjava-executor-thread-";

    private int counter = 0;

    @Override
    public Thread newThread(Runnable runnable) {
      return new Thread(runnable, THREAD_NAME + counter);
    }
  }
}