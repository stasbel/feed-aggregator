package belaevstanislav.feedagregator.service.feedsource.twitter;

import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.service.util.Latch;
import belaevstanislav.feedagregator.service.feedsource.FeedSource;
import belaevstanislav.feedagregator.data.threadpool.task.getter.TWITTERGetterTask;

public class TWITTER implements FeedSource {
    public static void fetchFeedItems(Data data, Latch latch) {
        data.taskPool.submitRunnableTask(new TWITTERGetterTask(data, latch));
    }
}
