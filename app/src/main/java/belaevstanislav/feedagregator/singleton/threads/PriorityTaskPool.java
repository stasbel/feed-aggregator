package belaevstanislav.feedagregator.singleton.threads;

import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.Callable;

import belaevstanislav.feedagregator.feeditem.shell.FeedItem;
import belaevstanislav.feedagregator.task.Task;
import belaevstanislav.feedagregator.task.parser.ParserTask;

public class PriorityTaskPool extends PriorityThreadPool {
    private final HashMap<Long, TaskFuture<FeedItem>> taskMap;

    public PriorityTaskPool() {
        // TODO надо что-то сделать с capacity
        super(2 * Runtime.getRuntime().availableProcessors() + 1, 1000);
        this.taskMap = new HashMap<>();
    }

    private <V extends Task & Runnable, T> TaskFuture<T> newTaskFutureFor(V task, T value) {
        return new TaskFuture<>(task, value);
    }

    private <V extends Task & Callable<T>, T> TaskFuture<T> newTaskFutureFor(V task) {
        return new TaskFuture<>(task);
    }

    public <V extends Task & Runnable> TaskFuture<?> submitRunnableTask(V task) {
        if (task == null) throw new NullPointerException();
        TaskFuture<Void> taskf = newTaskFutureFor(task, null);
        execute(taskf);
        return taskf;
    }

    private  <V extends Task & Callable<T>, T> TaskFuture<T> submitCallableTask(V task) {
        if (task == null) throw new NullPointerException();
        TaskFuture<T> taskf = newTaskFutureFor(task);
        execute(taskf);
        return taskf;
    }

    public <V extends ParserTask & Callable<FeedItem>> void submitParseTask(long id, V task) {
        taskMap.put(id, submitCallableTask(task));
    }

    public boolean isFinished(long id) {
        return taskMap.get(id).isDone();
    }

    public FeedItem fetchParseTask(long id) {
        try {
            return taskMap.get(id).get();
        } catch (Exception exception) {
            Log.e("TASKS", "TASKS_FETCH_EXCEPTION");
            exception.printStackTrace();
        }
        return null;
    }
}
