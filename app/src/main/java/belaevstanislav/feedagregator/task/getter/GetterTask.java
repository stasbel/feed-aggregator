package belaevstanislav.feedagregator.task.getter;

import belaevstanislav.feedagregator.singleton.threads.TaskPriority;
import belaevstanislav.feedagregator.task.Task;

public abstract class GetterTask implements Task {
    // TODO (#нерешаемое) костыль, но ничего не поделать, надо ждать java 8
    public static TaskPriority getPriority() {
        return TaskPriority.FIRST_PRIORITY;
    }

    @Override
    public TaskPriority getTaskPriority() {
        return GetterTask.getPriority();
    }
}
