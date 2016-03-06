package belaevstanislav.feedagregator.data.threadpool.task.cacher;

import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.data.threadpool.task.Task;
import belaevstanislav.feedagregator.data.threadpool.task.TaskPriority;
import belaevstanislav.feedagregator.feeditem.shell.FeedItem;

public abstract class CacherTask extends Task {
    private final FeedItem feedItem;

    protected CacherTask(Data data, FeedItem feedItem) {
        super(TaskPriority.CACHER_PRIORITY, data);
        this.feedItem = feedItem;
    }

    public FeedItem getFeedItem() {
        return feedItem;
    }
}
