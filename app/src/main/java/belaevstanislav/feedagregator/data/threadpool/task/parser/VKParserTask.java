package belaevstanislav.feedagregator.data.threadpool.task.parser;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.data.threadpool.task.cacher.CacherTask;
import belaevstanislav.feedagregator.feeditem.core.FeedItemCore;
import belaevstanislav.feedagregator.feeditem.shell.FeedItem;
import belaevstanislav.feedagregator.feeditem.shell.VKFeedItem;
import belaevstanislav.feedagregator.feedsource.vk.VKDataStore;

public class VKParserTask extends ParserTask implements Callable<FeedItem> {
    private final JSONObject item;
    private final VKDataStore dataStore;

    public VKParserTask(Data data, FeedItemCore core, long id, JSONObject item,
                        VKDataStore dataStore, boolean isNeedToCache) {
        super(data, core, id, isNeedToCache);
        this.item = item;
        this.dataStore = dataStore;
    }

    @Override
    public FeedItem call() throws Exception {
        FeedItem feedItem = null;
            try {
                VKFeedItem.AuthorInfoWrapper authorInfoWrapper = new VKFeedItem.AuthorInfoWrapper(item, dataStore);
                feedItem = new VKFeedItem(core, id, item, authorInfoWrapper, dataStore);
                if (isNeedToCache) {
                    data.taskPool.submitRunnableTask(new CacherTask(data, feedItem));
                }
            } catch (JSONException exception) {
                Log.e("VK", "VK_PARSE_EXCEPTION");
                exception.printStackTrace();
            }
        return feedItem;
    }
}
