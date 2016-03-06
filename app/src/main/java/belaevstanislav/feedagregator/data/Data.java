package belaevstanislav.feedagregator.data;

import android.content.Context;

import belaevstanislav.feedagregator.data.database.FeedItemDatabaseHelper;
import belaevstanislav.feedagregator.data.storage.Storage;
import belaevstanislav.feedagregator.data.threadpool.PriorityTaskPool;

public class Data {
    public final FeedItemDatabaseHelper database;
    public final Storage storage;
    public final PriorityTaskPool taskPool;

    public Data(Context context) {
        // database
        database = new FeedItemDatabaseHelper(context);
        database.getWritableDatabase();
        // TODO ???
        //databaseHelper.setWriteAheadLoggingEnabled(true);

        // storage
        storage = new Storage(context);

        // taskpool
        taskPool = new PriorityTaskPool();
        // TODO ???
        taskPool.prestartAllCoreThreads();
    }
}
