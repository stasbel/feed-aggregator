package belaevstanislav.feedagregator.task;

import belaevstanislav.feedagregator.singleton.threads.TaskPriority;

public interface Task {
    TaskPriority getTaskPriority();
}
