package belaevstanislav.feedagregator.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.data.storage.StorageKey;
import belaevstanislav.feedagregator.data.threadpool.task.deserializer.DeserializerTask;
import belaevstanislav.feedagregator.feedsource.twitter.TWITTER;
import belaevstanislav.feedagregator.main.FeedAgregator;
import belaevstanislav.feedagregator.service.util.Latch;
import belaevstanislav.feedagregator.util.Constant;

public class DataService extends Service {
    private Data data;
    private Notificator notificator;

    @Override
    public void onCreate() {
        data = ((FeedAgregator) getApplication()).getData();
        notificator = new Notificator(LocalBroadcastManager.getInstance(this));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DataServiceCommand command = intent.getParcelableExtra(DataServiceCommand.COMMAND_KEY);
        switch (command) {
            case FETCH_NEW_ITEMS:
                fetchNewItems();
                break;
            case DESEREALIZE_ITEMS:
                deserializeItems();
                break;
        }

        return START_NOT_STICKY;
    }

    private void fetchNewItems() {
        boolean isNeedToCache = data.storage.isInMemory(StorageKey.IS_SAVE_NEWS);
        Latch latch = new Latch(Constant.FEEDSOURCE_COUNT, notificator);
        TWITTER.fetchFeedItems(data, latch, isNeedToCache);
    }

    private void deserializeItems() {
        data.taskPool.submitRunnableTask(new DeserializerTask(data, notificator));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
