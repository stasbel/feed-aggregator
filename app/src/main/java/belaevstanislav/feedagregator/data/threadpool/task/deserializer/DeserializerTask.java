package belaevstanislav.feedagregator.data.threadpool.task.deserializer;

import android.database.Cursor;

import java.util.concurrent.Callable;

import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.data.threadpool.task.GetId;
import belaevstanislav.feedagregator.data.threadpool.task.Task;
import belaevstanislav.feedagregator.data.threadpool.task.TaskPriority;
import belaevstanislav.feedagregator.feeditem.shell.FeedItem;
import belaevstanislav.feedagregator.feeditem.shell.TWITTERFeedItem;
import belaevstanislav.feedagregator.feedsource.FeedSourceName;
import belaevstanislav.feedagregator.service.Notificator;
import belaevstanislav.feedagregator.service.util.ParcelableMethod;
import belaevstanislav.feedagregator.util.Constant;

public class DeserializerTask extends Task implements Runnable {
    private final Notificator notificator;

    public DeserializerTask(Data data, Notificator notificator) {
        super(TaskPriority.DESERIALIZER_PRIORITY, data);
        this.notificator = notificator;
    }

    @Override
    public void run() {
        Data data = getData();
        Cursor cursor = data.database.getAllWithBytecode();
        cursor.moveToPosition(0);
        int count = cursor.getCount();
        CursorInfo cursorInfo = new CursorInfo(cursor);
        for (int index = 0; index < count; index++) {
            data.taskPool.submitFeedItemBuilderTask(new DeserializeFeedItem(data, cursor, cursorInfo));
            cursor.moveToNext();
        }
        notificator.notifyReadyToShow();
    }

    public class DeserializeFeedItem extends Task implements GetId, Callable<FeedItem> {
        private final long id;
        private final FeedItem feedItem;

        public DeserializeFeedItem(Data data, Cursor cursor, CursorInfo cursorInfo) {
            super(TaskPriority.DESERIALIZER_PRIORITY, data);
            this.id = cursor.getLong(cursorInfo.getIndexColumnId());
            FeedSourceName sourceName = FeedSourceName.valueOf(cursor.getString(cursorInfo.getIndexColumnSource()));
            switch (sourceName) {
                case TWITTER:
                    feedItem = ParcelableMethod.unmarshall(cursor.getBlob(cursorInfo.getIndexColumnBytecode()),
                            TWITTERFeedItem.CREATOR);
                    break;
                default:
                    feedItem = null;
                    break;
            }
        }

        public FeedItem getFeedItem() {
            return feedItem;
        }

        @Override
        public long getId() {
            return id;
        }

        @Override
        public FeedItem call() {
            return feedItem;
        }
    }

    private class CursorInfo {
        private final int indexColumnId;
        private final int indexColumnSource;
        private final int indexColumnBytecode;

        public CursorInfo(Cursor cursor) {
            this.indexColumnId = cursor.getColumnIndex(Constant.DATABASE_KEY_TABLE_ID);
            this.indexColumnSource = cursor.getColumnIndex(Constant.DATABASE_KEY_TABLE_SOURCE);
            this.indexColumnBytecode = cursor.getColumnIndex(Constant.DATABASE_KEY_TABLE_BYTECODE);
        }

        public int getIndexColumnId() {
            return indexColumnId;
        }

        public int getIndexColumnSource() {
            return indexColumnSource;
        }

        public int getIndexColumnBytecode() {
            return indexColumnBytecode;
        }
    }
}
