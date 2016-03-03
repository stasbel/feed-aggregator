package belaevstanislav.feedagregator.task.parser;


import android.util.Log;

import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.util.concurrent.Callable;

import belaevstanislav.feedagregator.feeditem.core.TWITTERFeedItemCore;
import belaevstanislav.feedagregator.feeditem.shell.FeedItem;
import belaevstanislav.feedagregator.feeditem.shell.TWITTERFeedItem;
import belaevstanislav.feedagregator.singleton.threads.ThreadsManager;
import belaevstanislav.feedagregator.task.cacher.TWITTERCacherTask;

public class TWITTERParserTask extends ParserTask implements Callable<FeedItem> {
    private final Tweet tweet;
    private final long id;

    public TWITTERParserTask(TWITTERFeedItemCore core, Tweet tweet, long id) {
        super(core);
        this.tweet = tweet;
        this.id = id;
    }

    @Override
    public FeedItem call() {
        TWITTERFeedItem feedItem = null;
        try {
            feedItem = new TWITTERFeedItem((TWITTERFeedItemCore)getFeedItemCore(), tweet);
            ThreadsManager.getInstance().submitRunnableTask(new TWITTERCacherTask(id, feedItem));
        } catch (ParseException parseException) {
            Log.e("TWITTER", "TWITTER_PARSE_EXCEPTION");
            parseException.printStackTrace();
        }
        return feedItem;
    }
}
