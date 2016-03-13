package belaevstanislav.feedagregator.data.threadpool.task.cacher;

import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.data.threadpool.task.Task;
import belaevstanislav.feedagregator.data.threadpool.task.TaskPriority;
import belaevstanislav.feedagregator.feeditem.shell.FeedItem;

public class CacherTask extends Task implements Runnable {
    private final FeedItem feedItem;

    public CacherTask(Data data, FeedItem feedItem) {
        super(TaskPriority.CACHER_PRIORITY, data);
        this.feedItem = feedItem;
    }

    public FeedItem getFeedItem() {
        return feedItem;
    }

    @Override
    public void run() {
        data.database.insertFeedItem(feedItem);
    }
}
