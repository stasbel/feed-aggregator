package belaevstanislav.feedagregator.feedsource.twitter;

import android.app.Activity;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.data.threadpool.task.getter.TWITTERGetterTask;
import belaevstanislav.feedagregator.feedsource.FeedSource;
import belaevstanislav.feedagregator.service.util.Latch;

public class TWITTER implements FeedSource {
    public static void fetchFeedItems(Data data, Latch latch, boolean isNeedToCache) {
        data.taskPool.submitRunnableTask(new TWITTERGetterTask(data, latch, isNeedToCache));
    }

    public static void login(Activity activity, TwitterAuthClient authClient, final LoginCallback callback) {
        if (Twitter.getSessionManager().getActiveSession() == null
                || Twitter.getSessionManager().getActiveSession().getAuthToken().isExpired()) {
            authClient.authorize(activity, new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    callback.onSuccess(result);
                }

                @Override
                public void failure(TwitterException e) {
                    callback.onFail(e);
                }
            });
        } else {
            callback.onAllreadyLoginIn();
        }
    }

    public interface LoginCallback {
        void onSuccess(Result<TwitterSession> result);

        void onFail(TwitterException e);

        void onAllreadyLoginIn();
    }
}
