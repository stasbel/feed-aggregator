package belaevstanislav.feedagregator.data.threadpool.task.getter;

import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.data.threadpool.task.Task;
import belaevstanislav.feedagregator.data.threadpool.task.TaskPriority;

public abstract class GetterTask extends Task {
    protected GetterTask(Data data) {
        super(TaskPriority.GETTER_PRIORITY, data);
    }
}
