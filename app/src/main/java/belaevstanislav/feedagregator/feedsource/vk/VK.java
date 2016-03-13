package belaevstanislav.feedagregator.feedsource.vk;

import android.app.Activity;

import com.vk.sdk.VKSdk;

import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.data.threadpool.task.getter.VKGetterTask;
import belaevstanislav.feedagregator.feedsource.FeedSource;
import belaevstanislav.feedagregator.service.util.Latch;
import belaevstanislav.feedagregator.util.Constant;

public class VK implements FeedSource {
    public static void fetchFeedItems(Data data, Latch latch, boolean isNeedToCache) {
        data.taskPool.submitRunnableTask(new VKGetterTask(data, latch, isNeedToCache));
    }

    public static void login(Activity activity, final LoginCallback callback) {
        if (!VKSdk.isLoggedIn()) {
            VKSdk.login(activity, Constant.VK_SCOPE);
        } else {
            callback.onAllreadyLoginIn();
        }
    }

    public interface LoginCallback {
        void onAllreadyLoginIn();
    }
}
