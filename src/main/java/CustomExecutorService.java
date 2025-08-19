import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public class CustomExecutorService implements ExecutorService {
    private int corePoolSize;
    private boolean useVirtualThreads;
    private BlockingQueue<Runnable> workQueue;
    private List<Thread> poolWorkers;
    private AtomicBoolean shutdown;
    private CountDownLatch shutdownLatch;
    private ThreadFactory threadFactory;

    public CustomExecutorService(int corePoolSize, boolean useVirtualThreads) {
        if (corePoolSize <= 0) {
            throw new IllegalArgumentException("corePoolSize should be greater than 0");
        }
        setCorePoolSize(corePoolSize);
        setUseVirtualThreads(useVirtualThreads);
        setWorkQueue(new LinkedBlockingQueue<>());
        this.poolWorkers = new ArrayList<>();
        this.shutdown = new AtomicBoolean(false);
        this.shutdownLatch = new CountDownLatch(corePoolSize);

        if (useVirtualThreads) {
            this.threadFactory = Thread.ofVirtual()
                    .name("custom-virtual-worker-", 0)
                    .factory();
        } else {
            this.threadFactory = Thread.ofPlatform()
                    .name("custom-platform-worker-", 0)
                    .daemon(false)
                    .factory();
        }
        initializeWorkers();
    }

    private void initializeWorkers() {
        for (int i = 0; i < corePoolSize; i++) {
            Thread worker = threadFactory.newThread(new WorkerRunnable());
            poolWorkers.add(worker);
            worker.start();
        }
    }

    private class WorkerRunnable implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    Runnable task = null;

                    try {
                        task = workQueue.poll(1, TimeUnit.SECONDS);

                        if (task != null) {
                            try {
                                task.run();
                            } catch (Throwable t) {
                                System.err.println("Error: " + t.getMessage());
                                t.printStackTrace();
                            }
                        } else {
                            if (shutdown.get() && workQueue.isEmpty()) {
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                        if (shutdown.get()) {
                            break;
                        }
                        Thread.currentThread().interrupt();
                    }
                }
            } finally {
                shutdownLatch.countDown();
            }
        }
    }


    @Override
    public void shutdown() {
        shutdown.set(true);
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdown.set(true);

        for(Thread worker : poolWorkers) {
            worker.interrupt();
        }
        List<Runnable> result = new ArrayList<>();
        workQueue.drainTo(result);

        return result;
    }

    @Override
    public boolean isShutdown() {
        return shutdown.get();
    }

    @Override
    public boolean isTerminated() {
        return shutdown.get() && shutdownLatch.getCount() == 0;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        if (!shutdown.get()) {
            return false;
        }
        return shutdownLatch.await(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if(task == null) {
            throw new NullPointerException();
        }
        FutureTask<T> futureTask = new FutureTask<>(task);
        execute(futureTask);

        return futureTask;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        if(task == null) {
            throw new NullPointerException();
        }
        FutureTask<T> futureTask = new FutureTask<>(task, result);
        execute(futureTask);

        return futureTask;
    }

    @Override
    public Future<?> submit(Runnable task) {
        return submit(task,null);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return List.of();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return List.of();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> collection) throws InterruptedException, ExecutionException {
        List<Future<T>> tasks = new ArrayList<>();
        for (var task : collection) {
            var future = submit(task);
            tasks.add(future);
        }
        while (true) {
            for (var future : tasks) {
                if (future.isDone()) {
                    return future.get();
                }
            }
        }
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    @Override
    public void execute(Runnable command) {
        if(command == null) {
            throw new NullPointerException("Command is null");
        }
        if(shutdown.get()) {
            throw new RejectedExecutionException("Executor shutdown");
        }

        workQueue.offer(command);
    }

    public static CustomExecutorService newVirtualThreadPerTaskExecutor(){
        return new  VirtualThreadPerTaskExecutor();
    }

    private static class VirtualThreadPerTaskExecutor extends CustomExecutorService {
        private final AtomicBoolean shutdown = new AtomicBoolean(false);

        public VirtualThreadPerTaskExecutor() {
            super(1, true);
        }

        @Override
        public void execute(Runnable command) {
            if(command == null) {
                throw new NullPointerException("command can't be null");
            }
            if(shutdown.get()) {
                throw new RejectedExecutionException("Executor shutdown");
            }
            //create new virtual thread for each task
            Thread.ofVirtual()
                    .name("Virtual task thread ")
                    .start(command);
        }

        @Override
        public void shutdown() {
            shutdown.set(true);
        }

        @Override
        public boolean isShutdown(){
            return shutdown.get();
        }

        @Override
        public boolean isTerminated() {
            return shutdown.get();
        }
    }
}
