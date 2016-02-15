package belaevstanislav.feedagregator.feedsource.twitter;

import belaevstanislav.feedagregator.feedsource.FeedSource;
import belaevstanislav.feedagregator.singleton.threads.ThreadsManager;
import belaevstanislav.feedagregator.task.getter.TWITTERGetterTask;
import belaevstanislav.feedagregator.util.AsyncLatch;

public class TWITTER implements FeedSource {
    public static void fetchFeedItems(AsyncLatch asyncLatch) {
        ThreadsManager.getInstance().submitRunnableTask(new TWITTERGetterTask(asyncLatch));
    }
}
