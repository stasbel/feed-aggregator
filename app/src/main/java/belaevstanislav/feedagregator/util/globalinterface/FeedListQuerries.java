package belaevstanislav.feedagregator.util.globalinterface;

import belaevstanislav.feedagregator.feedsource.FeedSourceName;

public interface FeedListQuerries {
    void showAll();
    void showOnlySource(FeedSourceName name);
}
