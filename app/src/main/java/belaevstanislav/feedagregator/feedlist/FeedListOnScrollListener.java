package belaevstanislav.feedagregator.feedlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class FeedListOnScrollListener extends RecyclerView.OnScrollListener {
    private final Context context;
    private final Picasso picasso;
    private final SwipeCallback swipeCallback;

    public FeedListOnScrollListener(Context context, SwipeCallback swipeCallback) {
        this.context = context;
        this.picasso = Picasso.with(context);
        this.swipeCallback = swipeCallback;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        // TODO возвращаться в исходное положение плавнее?
        FeedItemViewHolder lastSwiped = swipeCallback.getLastSwiped();
        if (lastSwiped != null) {
            lastSwiped.resetSwipeState();
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        // TODO нужно ли?
        if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            picasso.resumeTag(context);
        } else {
            picasso.pauseTag(context);
        }
    }
}
