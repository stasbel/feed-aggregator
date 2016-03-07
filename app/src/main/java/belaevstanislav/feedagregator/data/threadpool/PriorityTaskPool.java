package belaevstanislav.feedagregator.data.threadpool;

import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.Callable;

import belaevstanislav.feedagregator.data.threadpool.task.GetId;
import belaevstanislav.feedagregator.data.threadpool.task.Task;
import belaevstanislav.feedagregator.feeditem.shell.FeedItem;

public class PriorityTaskPool extends PriorityThreadPool {
    private final HashMap<Long, FutureTaskWrapper<FeedItem>> taskMap;

    public PriorityTaskPool() {
        // TODO надо что-то сделать с capacity
        super(2 * Runtime.getRuntime().availableProcessors() + 1, 1000);
        this.taskMap = new HashMap<>();
    }

    private <V extends Task & Runnable, T> FutureTaskWrapper<T> newTaskFutureFor(V task, T value) {
        return new FutureTaskWrapper<>(task, value);
    }

    private <V extends Task & Callable<T>, T> FutureTaskWrapper<T> newTaskFutureFor(V task) {
        return new FutureTaskWrapper<>(task);
    }

    public <V extends Task & Runnable> FutureTaskWrapper<?> submitRunnableTask(V task) {
        if (task == null) throw new NullPointerException();
        FutureTaskWrapper<Void> taskf = newTaskFutureFor(task, null);
        execute(taskf);
        return taskf;
    }

    private  <V extends Task & Callable<T>, T> FutureTaskWrapper<T> submitCallableTask(V task) {
        if (task == null) throw new NullPointerException();
        FutureTaskWrapper<T> taskf = newTaskFutureFor(task);
        execute(taskf);
        return taskf;
    }

    public <V extends Task & GetId & Callable<FeedItem>> void submitFeedItemBuilderTask(V task) {
        taskMap.put(task.getId(), submitCallableTask(task));
    }

    public boolean isFinishBuildFeedItem(long id) {
        return taskMap.get(id).isDone();
    }

    public FeedItem fetchFeedItem(long id) {
        try {
            return taskMap.get(id).get();
        } catch (Exception exception) {
            Log.e("TASKS", "TASKS_FETCH_EXCEPTION");
            exception.printStackTrace();
        }
        return null;
    }
}
