package belaevstanislav.feedagregator.feedlist;

import android.support.v7.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import belaevstanislav.feedagregator.FeedAgregator;
import belaevstanislav.feedagregator.singleton.images.ImagesManager;

public class FeedListOnScrollListener extends RecyclerView.OnScrollListener {
    private final Picasso picasso;
    private final SwipeCallback swipeCallback;

    public FeedListOnScrollListener(SwipeCallback swipeCallback) {
        this.picasso = ImagesManager.getInstance();
        this.swipeCallback = swipeCallback;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        // TODO возвращаться в исходное положение плавнее?
        FeedListCursorAdapter.FeedItemViewHolder lastSwiped = swipeCallback.getLastSwiped();
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING && lastSwiped != null) {
            lastSwiped.resetSwipeState();
        }

        // TODO нужно ли?
        if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            picasso.resumeTag(FeedAgregator.getContext());
        } else {
            picasso.pauseTag(FeedAgregator.getContext());
        }
    }
}
