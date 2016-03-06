package belaevstanislav.feedagregator.data.threadpool;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PriorityThreadPool extends ThreadPoolExecutor {
    // TODO multiple nthreads by 2?
    // TODO (#нерешаемое) гарантия того, что в очередь мы передаем только наследников Task (фактически, мы передаем только FutureTaskWrapper)
    @SuppressWarnings("all")
    protected PriorityThreadPool(int numberOfThreads, int queueInitialCapacity) {
        super(numberOfThreads, numberOfThreads, 0L, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<Runnable>(queueInitialCapacity, new TaskComparator()),
                new BackgroundThreadFactory());
    }
}
