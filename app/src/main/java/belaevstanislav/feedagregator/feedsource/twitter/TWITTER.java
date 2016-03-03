package belaevstanislav.feedagregator.feedsource.twitter;

import belaevstanislav.feedagregator.feedsource.FeedSource;
import belaevstanislav.feedagregator.service.Latch;
import belaevstanislav.feedagregator.singleton.threads.ThreadsManager;
import belaevstanislav.feedagregator.task.getter.TWITTERGetterTask;

public class TWITTER implements FeedSource {
    public static void fetchFeedItems(Latch latch) {
        ThreadsManager.getInstance().submitRunnableTask(new TWITTERGetterTask(latch));
    }
}
