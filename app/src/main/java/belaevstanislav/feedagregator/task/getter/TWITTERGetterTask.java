package belaevstanislav.feedagregator.task.getter;

import android.os.AsyncTask;
import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import belaevstanislav.feedagregator.singleton.database.DatabaseManager;
import belaevstanislav.feedagregator.feeditem.core.TWITTERFeedItemCore;
import belaevstanislav.feedagregator.singleton.storage.StorageKeys;
import belaevstanislav.feedagregator.singleton.storage.StorageManager;
import belaevstanislav.feedagregator.singleton.threads.ThreadsManager;
import belaevstanislav.feedagregator.singleton.threads.PriorityTaskPool;
import belaevstanislav.feedagregator.task.parser.TWITTERParserTask;
import belaevstanislav.feedagregator.util.asynclatch.AsyncLatch;
import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.HighPriorityAsyncTask;

public class TWITTERGetterTask extends GetterTask implements Runnable {
    private final AsyncLatch asyncLatch;

    public TWITTERGetterTask(AsyncLatch asyncLatch) {
        this.asyncLatch = asyncLatch;
    }

    private class HandleItemTask extends GetterTask implements Runnable {
        private final Tweet tweet;

        public HandleItemTask(Tweet tweet) {
            this.tweet = tweet;
        }

        @Override
        public void run() {
            Log.e("1.3", "1.3");
            try {
                TWITTERFeedItemCore core = new TWITTERFeedItemCore(tweet);

                long id = DatabaseManager.getInstance().insertWithoutCaching(core);

                ThreadsManager.getInstance().submitParseTask(id, new TWITTERParserTask(core, tweet, id));
            } catch (ParseException parseException) {
                Log.e("TWITTER", "TWITTER_PARSE_EXCEPTION");
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
            Log.e("1.2", "1.2");
            List<Tweet> tweetList = result.data;
            int size = tweetList.size();
            Log.e("size", String.valueOf(size));
            if (size > 0) {
                StorageManager.getInstance().saveLong(StorageKeys.LAST_TWEET_ID, tweetList.get(0).getId());

                ArrayList<Future<?>> tasks = new ArrayList<>(size);

                PriorityTaskPool priorityTaskPool = ThreadsManager.getInstance();
                for (int index = 0; index < size; index++) {
                    tasks.add(priorityTaskPool.submitRunnableTask(new HandleItemTask(tweetList.get(index))));
                }

                try {
                    for (Future<?> task : tasks) {
                        task.get();
                    }
                } catch (Exception exception) {
                    Log.e("TWITTER", "TWITTER_THREADS_EXCEPTION");
                    exception.printStackTrace();
                }
            }
        }
    }

    private class HandleResultAsyncTask extends HighPriorityAsyncTask<Void, Void, Void> {
        private final Result<List<Tweet>> result;

        public HandleResultAsyncTask(Result<List<Tweet>> result) {
            this.result = result;
        }

        @Override
        protected Void inBackground(Void... params) {
            try {
                ThreadsManager.getInstance().submitRunnableTask(new HandleItemsTask(result)).get();
            } catch (Exception exception) {
                Log.e("TWITTER", "TWITTER_THREADS_EXCEPTION");
                exception.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("TW", "TW");
            asyncLatch.countDownAndTryInvoke();
        }
    }

    @Override
    public void run() {
        Log.e("1.1", "1.1");
        // TODO реализовать проход по страницам (результатов может быть больше, чем 200)
        Integer number;
        Long id;
        if (!StorageManager.getInstance().isInMemory(StorageKeys.LAST_TWEET_ID)) {
            number = Constant.FIRST_TWITTER_QUERY_PAGE_SIZE;
            id = null;
        } else {
            number = Constant.MAX_TWEETS_PER_PAGE;
            // TODO delete 10000000
            id = StorageManager.getInstance().getLong(StorageKeys.LAST_TWEET_ID) - 100000000;
        }
        TwitterCore.getInstance().getApiClient().getStatusesService().homeTimeline(
                number, null, null, null, null, null, null,
                new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        new HandleResultAsyncTask(result).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

                    @Override
                    public void failure(TwitterException twitterException) {
                        Log.e("TWITTER", "TWITTER_GET_EXCEPTION");
                        twitterException.printStackTrace();
                    }
                }
        );
    }
}
