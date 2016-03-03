package belaevstanislav.feedagregator.task.getter;

import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import belaevstanislav.feedagregator.feeditem.core.TWITTERFeedItemCore;
import belaevstanislav.feedagregator.service.Latch;
import belaevstanislav.feedagregator.singleton.database.DatabaseManager;
import belaevstanislav.feedagregator.singleton.database.UnReadFeedItemDatabaseHelper;
import belaevstanislav.feedagregator.singleton.storage.StorageKey;
import belaevstanislav.feedagregator.singleton.storage.StorageManager;
import belaevstanislav.feedagregator.singleton.threads.PriorityTaskPool;
import belaevstanislav.feedagregator.singleton.threads.ThreadsManager;
import belaevstanislav.feedagregator.task.parser.TWITTERParserTask;
import belaevstanislav.feedagregator.util.Constant;

public class TWITTERGetterTask extends GetterTask implements Runnable {
    private final Latch latch;
    private final UnReadFeedItemDatabaseHelper databaseHelper;
    private final PriorityTaskPool priorityTaskPool;

    public TWITTERGetterTask(Latch latch) {
        this.latch = latch;
        this.databaseHelper = DatabaseManager.getInstance();
        this.priorityTaskPool = ThreadsManager.getInstance();
    }

    private class HandleItemTask extends GetterTask implements Runnable {
        private final Tweet tweet;
        private final CountDownLatch countDownLatch;

        public HandleItemTask(Tweet tweet, CountDownLatch countDownLatch) {
            this.tweet = tweet;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                TWITTERFeedItemCore core = new TWITTERFeedItemCore(tweet);
                long id = databaseHelper.insertWithoutCaching(core);
                countDownLatch.countDown();

                priorityTaskPool.submitParseTask(id, new TWITTERParserTask(core, tweet, id));
            } catch (ParseException parseException) {
                Log.e("TWITTER", "TWITTER_PARSECORE_EXCEPTION");
                parseException.printStackTrace();
            }
        }
    }

    private class HandleItemsTask extends GetterTask implements Runnable {
        private final Result<List<Tweet>> result;

        public HandleItemsTask(Result<List<Tweet>> result) {
            this.result = result;
        }

        @Override
        public void run() {
            List<Tweet> tweetList = result.data;
            int size = tweetList.size();
            if (size > 0) {
                StorageManager.getInstance().saveLong(StorageKey.LAST_TWEET_ID, tweetList.get(0).getId());

                CountDownLatch countDownLatch = new CountDownLatch(size);
                for (int index = 0; index < size; index++) {
                    priorityTaskPool.submitRunnableTask(new HandleItemTask(tweetList.get(index), countDownLatch));
                }

                try {
                    countDownLatch.await();
                } catch (InterruptedException exception) {
                    Log.e("TWITTER", "TWITTER_THREADS_EXCEPTION");
                    exception.printStackTrace();
                }
                latch.countDownAndTryNotify();
            }
        }
    }

    @Override
    public void run() {
        // TODO реализовать проход по страницам (результатов может быть больше, чем 200) + id
        Integer number;
        //Long id;
        if (!StorageManager.getInstance().isInMemory(StorageKey.LAST_TWEET_ID)) {
            number = Constant.FIRST_TWITTER_QUERY_PAGE_SIZE;
            //id = null;
        } else {
            number = Constant.MAX_TWEETS_PER_PAGE;
            // TODO delete 10000000
            //id = StorageManager.getInstance().getLong(StorageKey.LAST_TWEET_ID) - 100000000;
        }

        TwitterCore
                .getInstance()
                .getApiClient()
                .getStatusesService()
                .homeTimeline(number, null, null, null, null, null, null, new TWITTERCallback());
    }

    private class TWITTERCallback extends Callback<List<Tweet>> {
        @Override
        public void success(Result<List<Tweet>> result) {
            priorityTaskPool.submitRunnableTask(new HandleItemsTask(result));
        }

        @Override
        public void failure(TwitterException twitterException) {
            Log.e("TWITTER", "TWITTER_GET_EXCEPTION");
            twitterException.printStackTrace();
        }
    }
}

