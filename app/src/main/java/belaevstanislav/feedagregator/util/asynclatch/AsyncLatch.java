package belaevstanislav.feedagregator.util.asynclatch;

public class AsyncLatch {
    private int count;
    private belaevstanislav.feedagregator.util.asynclatch.onShowFeedListListener onShowFeedListListener;

    public AsyncLatch(int count, onShowFeedListListener onShowFeedListListener) {
        this.count = count;
        this.onShowFeedListListener = onShowFeedListListener;
    }

    public synchronized void countDownAndTryInvoke() {
        count--;
        if (count == 0) {
            onShowFeedListListener.onShowFeedList();
        }
    }
}
