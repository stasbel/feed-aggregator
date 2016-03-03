package belaevstanislav.feedagregator.singleton.threads;

import java.util.Comparator;

import belaevstanislav.feedagregator.task.Task;
import belaevstanislav.feedagregator.task.cacher.CacherTask;
import belaevstanislav.feedagregator.task.parser.ParserTask;

// TODO (#нерешаемое) от предупреждения не избавиться
@SuppressWarnings("unused")
public class TaskComparator<Runnable> implements Comparator<TaskFuture<?>> {
    @Override
    public int compare(TaskFuture<?> lhs, TaskFuture<?> rhs) {
        Task leftTask = lhs.getTask();
        Task rightTask = rhs.getTask();
        TaskPriority leftPriority = leftTask.getTaskPriority();
        TaskPriority rightPriority = rightTask.getTaskPriority();
        if (leftPriority.equals(rightPriority)) {
            if (leftPriority.equals(ParserTask.getPriority())) {
                return ((ParserTask)leftTask).getFeedItemCore().compareTo(((ParserTask)rightTask).getFeedItemCore());
            } else if (leftPriority.equals(CacherTask.getPriority())) {
                return ((CacherTask)rightTask).getFeedItem().compareTo(((CacherTask)leftTask).getFeedItem());
            } else return 0;
        } else {
            return leftPriority.compareTo(rightPriority);
        }
    }
}
