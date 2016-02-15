package belaevstanislav.feedagregator.singleton.threads;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import belaevstanislav.feedagregator.task.Task;

public class TaskFuture<T> extends FutureTask<T> {
    private final Task task;

    public <V extends Task & Runnable> TaskFuture(V task, T result) {
        super(task, result);
        this.task = task;
    }

    public <V extends Task & Callable<T>> TaskFuture(V task) {
        super(task);
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
