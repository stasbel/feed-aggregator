package belaevstanislav.feedagregator.data.threadpool.task.parser;


import android.util.Log;

import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.util.concurrent.Callable;

import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.feeditem.core.TWITTERFeedItemCore;
import belaevstanislav.feedagregator.feeditem.shell.FeedItem;
import belaevstanislav.feedagregator.feeditem.shell.TWITTERFeedItem;
import belaevstanislav.feedagregator.data.threadpool.task.cacher.TWITTERCacherTask;

public class TWITTERParserTask extends ParserTask implements Callable<FeedItem> {
    private final Tweet tweet;
    private final boolean isNeedToCache;

    public TWITTERParserTask(Data data, TWITTERFeedItemCore core, long id, Tweet tweet, boolean isNeedToCache) {
        super(data, core, id);
        this.tweet = tweet;
        this.isNeedToCache = isNeedToCache;
    }

    @Override
    public FeedItem call() {
        TWITTERFeedItem feedItem = null;
        try {
            feedItem = new TWITTERFeedItem(getFeedItemCore(), getId(), tweet);
            if (isNeedToCache) {
                getData().taskPool.submitRunnableTask(new TWITTERCacherTask(getData(), feedItem));
            }
        } catch (ParseException parseException) {
            Log.e("TWITTER", "TWITTER_PARSE_EXCEPTION");
            parseException.printStackTrace();
        }
        return feedItem;
    }
}
