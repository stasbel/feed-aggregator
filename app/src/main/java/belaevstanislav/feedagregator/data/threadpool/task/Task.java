package belaevstanislav.feedagregator.data.threadpool.task;

import belaevstanislav.feedagregator.data.Data;

public abstract class Task {
    private final TaskPriority taskPriority;
    private final Data data;

    protected Task(TaskPriority taskPriority, Data data) {
        this.taskPriority = taskPriority;
        this.data = data;
    }

    public TaskPriority getTaskPriority() {
        return taskPriority;
    }

    protected Data getData() {
        return data;
    }
}
