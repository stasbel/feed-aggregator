package belaevstanislav.feedagregator.task.cacher;

import android.util.Log;

import belaevstanislav.feedagregator.feeditem.shell.TWITTERFeedItem;
import belaevstanislav.feedagregator.singleton.database.DatabaseManager;

public class TWITTERCacherTask extends CacherTask implements Runnable {
    private final long id;

    public TWITTERCacherTask(long id, TWITTERFeedItem feedItem) {
        super(feedItem);
        this.id = id;
    }

    @Override
    public void run() {
        Log.e("3", "3");
        DatabaseManager.getInstance().insertWithCaching(id, getFeedItem());
    }
}
