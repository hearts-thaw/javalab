package main.java.ru.itis.pool;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {
    private final Queue<Runnable> tasks = new LinkedBlockingQueue<>();

    public PoolWorker[] pool;

    public static ThreadPool newPool(int threadsCount) {
        ThreadPool threadPool = new ThreadPool();
        threadPool.pool = new PoolWorker[threadsCount];

        for (int i = 0; i < threadPool.pool.length; i++) {
            threadPool.pool[i] = threadPool.new PoolWorker();
            threadPool.pool[i].start();
        }

        return threadPool;
    }

    public void submit(Runnable task) {
        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
    }

    public void stopAll() {
        for (PoolWorker worker : pool) {
            worker.stop();
        }
    }

    private class PoolWorker extends Thread {

        private boolean running = true;

        @Override
        public void run() {
            Runnable task;
            while (true) {
                synchronized (tasks) {
                    while (tasks.isEmpty()) {
                        try {
                            System.out.println("Waiting " + Thread.currentThread().getName());
                            tasks.wait();
                        } catch (InterruptedException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                    task = tasks.poll();
                }
                task.run();
            }
        }

        public void stopRunning() {
            this.running = false;
        }

        public void resumeRunning() {
            this.running = true;
        }
    }
}
