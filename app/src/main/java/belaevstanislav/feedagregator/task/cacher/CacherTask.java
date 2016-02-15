package belaevstanislav.feedagregator.task.cacher;

import belaevstanislav.feedagregator.feeditem.shell.FeedItem;
import belaevstanislav.feedagregator.singleton.threads.TaskPriority;
import belaevstanislav.feedagregator.task.Task;

public abstract class CacherTask implements Task {
    // TODO (#нерешаемое) костыль, но ничего не поделать, надо ждать java 8
    public static TaskPriority getPriority() {
        return TaskPriority.THIRD_PRIOTITY;
    }

    private final FeedItem feedItem;

    protected CacherTask(FeedItem feedItem) {
        this.feedItem = feedItem;
    }

    public FeedItem getFeedItem() {
        return feedItem;
    }

    @Override
    public final TaskPriority getTaskPriority() {
        return CacherTask.getPriority();
    }
}
