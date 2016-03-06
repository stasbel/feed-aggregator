package belaevstanislav.feedagregator.data.threadpool.task.cacher;

import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.feeditem.shell.TWITTERFeedItem;

public class TWITTERCacherTask extends CacherTask implements Runnable {
    public TWITTERCacherTask(Data data, TWITTERFeedItem feedItem) {
        super(data, feedItem);
    }

    @Override
    public void run() {
        getData().database.insertFeedItem(getFeedItem());
    }
}
