package ru.nsu.gsafonkin.lab4.my.concurent.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPool implements ExecutorService {
    private final int corePoolSize;
    private final int maximumPoolSize;
    private final long keepAliveTime;
    private final long breakTime;
    private final TimeUnit unit;
    private final BlockingQueue<Runnable> workQueue;
    private final List<Thread> threads = new ArrayList<>();
    private final ThreadFactory threadFactory = new MyThreadFactory();
    private final RejectedExecutionHandler handler;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private volatile int runningThreads = 0;
    private volatile boolean shutDownFlag = false;
    public ThreadPool(int corePoolSize, int maximumPoolSize) {
        this(corePoolSize, maximumPoolSize, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                new ThreadPoolExecutor.AbortPolicy());
    }
    public ThreadPool(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            RejectedExecutionHandler handler
    ) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.breakTime = keepAliveTime;
        this.unit = unit;
        this.workQueue = workQueue;
        this.handler = handler;
    }
    @Override
    public void shutdown() {
        lock.lock();
        try {
            int oldThreadNumber = runningThreads;
            for (int i = 0; i < oldThreadNumber; ++i) {
                workQueue.add(new Runnable() {
                    @Override
                    public void run() {
                        --runningThreads;
                    }
                });
            }
        } finally {
            shutDownFlag = true;
            lock.unlock();
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        List<Runnable> returnable = new ArrayList<>();
        lock.lock();
        try {
            while (!workQueue.isEmpty()) {
                returnable.add(workQueue.poll());
            }
            runningThreads = 0;
            lock.lock();
            try {
                while(threads.size() != 0) {
                    threads.remove(threads.size() - 1).interrupt();
                }
            } finally {
                lock.unlock();
            }
            return returnable;
        } finally {
            shutDownFlag = true;
            lock.unlock();
        }
    }

    @Override
    public boolean isShutdown() {
        return shutDownFlag;
    }

    @Override
    public boolean isTerminated() {
        return shutDownFlag && runningThreads == 0 && workQueue.isEmpty();
    }

    @Override
    public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
        long startMoment = System.currentTimeMillis();
        long stopMoment = startMoment + timeUnit.toMillis(l);
        lock.lock();
        long curMoment = System.currentTimeMillis();
        try {
            while(!workQueue.isEmpty() && curMoment < stopMoment) {
                condition.await((stopMoment - startMoment)/60, TimeUnit.MILLISECONDS);
                curMoment = System.currentTimeMillis();
            }
        } finally {
            lock.unlock();
        }
        return this.isTerminated();
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        if (null == callable) {
            throw new NullPointerException();
        }
        FutureTask<T> futureTask = new FutureTask<>(callable);
        if (!isShutdown()) {
            submitCore(futureTask);
        }
        return futureTask;
    }

    @Override
    public <T> Future<T> submit(Runnable runnable, T t) {
        if (null == runnable) {
            throw new NullPointerException();
        }
        FutureTask<T> futureTask = new FutureTask<>(runnable, t);
        if (!isShutdown()) {
            submitCore(futureTask);
        }
        return futureTask;
    }

    @Override
    public Future<?> submit(Runnable runnable) {
        if (null == runnable) {
            throw new NullPointerException();
        }
        FutureTask<?> futureTask = new FutureTask<>(runnable, null);
        if (!isShutdown()) {
            submitCore(futureTask);
        }
        return futureTask;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection) throws InterruptedException {
        if (null == collection) {
            throw new NullPointerException();
        }
        List<Future<T>> futures = new ArrayList<>(collection.size());
        for (Callable<T> task : collection) {
            Future<T> future = submit(task);
            futures.add(future);
        }
        boolean isNotDoneFlag = true;
        while(isNotDoneFlag) {
            isNotDoneFlag = false;
            for (Future<T> future : futures) {
                if (!future.isDone()) {
                    isNotDoneFlag = true;
                    break;
                }
            }
        }
        return futures;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) throws InterruptedException {
        if (null == collection) {
            throw new NullPointerException();
        }
        long time = System.currentTimeMillis();
        List<Future<T>> futures = new ArrayList<>(collection.size());
        for (Callable<T> task : collection) {
            if (System.currentTimeMillis() - time >= timeUnit.toMillis(l)) {
                break;
            }
            Future<T> future = submit(task);
            futures.add(future);
        }
        for (int i = 0; i < futures.size(); ++i) {
            if (!futures.get(i).isDone() && System.currentTimeMillis() - time >= timeUnit.toMillis(l)) {
                futures.get(i).cancel(true);
                futures.remove(i);
            }
        }
        return futures;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> collection) throws InterruptedException, ExecutionException {
        if (collection == null) {
            throw new NullPointerException();
        }
        List<Future<T>> futures = invokeAll(collection);
        for (Future<T> future : futures) {
            try {
                return future.get();
            } catch (CancellationException e) {
                continue;
            } catch (ExecutionException e) {
                throw e;
            }
        }
        throw new ExecutionException(new NoSuchElementException());
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        if (collection == null) {
            throw new NullPointerException();
        }
        long nanos = unit.toNanos(l);
        List<Future<T>> futures = invokeAll(collection);
        for (Future<T> future : futures) {
            try {
                return future.get(nanos, TimeUnit.NANOSECONDS);
            } catch (CancellationException e) {
                continue;
            } catch (ExecutionException e) {
                throw e;
            }
        }
        throw new ExecutionException(new NoSuchElementException());
    }

    @Override
    public void execute(Runnable runnable) {
        if (null == runnable) {
            throw new NullPointerException();
        }
        submitCore(runnable);
    }

    //auxiliary functions and classes below
    private void createAndStartThread(Runnable runnable) {
        ++runningThreads;
        lock.lock();
        try {
            threads.add(threadFactory.newThread(runnable));
            threads.get(threads.size() - 1).start();
        } finally {
            lock.unlock();
        }
    }
    private void submitCore(Runnable runnable) {
        lock.lock();
        try {
            if (runningThreads < maximumPoolSize) {
                createAndStartThread(runnable);
                return;
            }
            workQueue.put(runnable);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    private class RunnableWrapper implements Runnable {
        private final Runnable runnable;
        public RunnableWrapper(Runnable runnable) {
            this.runnable = runnable;
        }
        @Override
        public void run() {
            try {
                runnable.run();
            } finally {
                --runningThreads;
            }
        }
    }
    private class ThreadWrapper extends Thread {
        private final BlockingQueue<Runnable> taskQueue;
        private Runnable curTask;
        private long lastTaskTime;
        public ThreadWrapper() {
            this.taskQueue = workQueue;
            this.lastTaskTime = System.currentTimeMillis();
        }
        @Override
        public void run() {
            while(true) {
                if (this.isInterrupted() || workQueue.isEmpty()) {
                    break;
                }
                if (lastTaskTime - System.currentTimeMillis() >= keepAliveTime*1000) {
                    this.interrupt();
                    continue;
                }
                if ((curTask = taskQueue.poll()) != null) {
                    lastTaskTime = System.currentTimeMillis();
                    curTask.run();
                }
            }
            --runningThreads;
            lock.lock();
            try {
                threads.remove(this);
            } finally {
                lock.unlock();
            }
        }
    }
    private class MyThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable runnable) {
            workQueue.add(runnable);
            return new ThreadWrapper();
        }
    }
}