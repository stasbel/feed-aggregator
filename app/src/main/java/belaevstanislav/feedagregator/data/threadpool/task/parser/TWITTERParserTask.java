package belaevstanislav.feedagregator.data.threadpool.task.parser;


import android.util.Log;

import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.util.concurrent.Callable;

import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.data.threadpool.task.cacher.CacherTask;
import belaevstanislav.feedagregator.feeditem.core.FeedItemCore;
import belaevstanislav.feedagregator.feeditem.shell.FeedItem;
import belaevstanislav.feedagregator.feeditem.shell.TWITTERFeedItem;

public class TWITTERParserTask extends ParserTask implements Callable<FeedItem> {
    private final Tweet tweet;

    public TWITTERParserTask(Data data, FeedItemCore core, long id, Tweet tweet, boolean isNeedToCache) {
        super(data, core, id, isNeedToCache);
        this.tweet = tweet;
    }

    @Override
    public FeedItem call() {
        FeedItem feedItem = null;
        try {
            feedItem = new TWITTERFeedItem(core, id, tweet);
            if (isNeedToCache) {
                data.taskPool.submitRunnableTask(new CacherTask(data, feedItem));
            }
        } catch (ParseException parseException) {
            Log.e("TWITTER", "TWITTER_PARSE_EXCEPTION");
            parseException.printStackTrace();
        }
        return feedItem;
    }
}
