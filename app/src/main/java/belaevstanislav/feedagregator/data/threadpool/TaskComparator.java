package belaevstanislav.feedagregator.data.threadpool;

import java.util.Comparator;

import belaevstanislav.feedagregator.data.threadpool.task.Task;
import belaevstanislav.feedagregator.data.threadpool.task.TaskPriority;
import belaevstanislav.feedagregator.data.threadpool.task.cacher.CacherTask;
import belaevstanislav.feedagregator.data.threadpool.task.deserializer.DeserializerTask;
import belaevstanislav.feedagregator.data.threadpool.task.parser.ParserTask;

// TODO (#нерешаемое) от предупреждения не избавиться
@SuppressWarnings("unused")
public class TaskComparator<Runnable> implements Comparator<FutureTaskWrapper<?>> {
    @Override
    public int compare(FutureTaskWrapper<?> lhs, FutureTaskWrapper<?> rhs) {
        Task leftTask = lhs.getTask();
        Task rightTask = rhs.getTask();
        TaskPriority leftPriority = leftTask.getTaskPriority();
        TaskPriority rightPriority = rightTask.getTaskPriority();
        if (leftPriority.equals(rightPriority)) {
            if (leftPriority.equals(TaskPriority.DESERIALIZER_PRIORITY)) {
                return ((DeserializerTask.DeserializeFeedItem) leftTask).getFeedItem()
                        .compareTo(((DeserializerTask.DeserializeFeedItem) rightTask).getFeedItem());
            } else if (leftPriority.equals(TaskPriority.PARSER_PRIORITY)) {
                return ((ParserTask) leftTask).getFeedItemCore()
                        .compareTo(((ParserTask) rightTask).getFeedItemCore());
            } else if (leftPriority.equals(TaskPriority.CACHER_PRIORITY)) {
                return ((CacherTask) rightTask).getFeedItem()
                        .compareTo(((CacherTask) leftTask).getFeedItem());
            } else return 0;
        } else {
            return leftPriority.compareTo(rightPriority);
        }
    }
}
