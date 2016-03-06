package belaevstanislav.feedagregator.data.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import belaevstanislav.feedagregator.data.threadpool.task.Task;

public class FutureTaskWrapper<T> extends FutureTask<T> {
    private final Task task;

    public <V extends Task & Runnable> FutureTaskWrapper(V task, T result) {
        super(task, result);
        this.task = task;
    }

    public <V extends Task & Callable<T>> FutureTaskWrapper(V task) {
        super(task);
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
