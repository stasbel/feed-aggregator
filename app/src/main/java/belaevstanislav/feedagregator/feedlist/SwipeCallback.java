package belaevstanislav.feedagregator.feedlist;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

public class SwipeCallback extends ItemTouchHelper.Callback {
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        FeedItemViewHolder feedItemViewHolder = (FeedItemViewHolder) viewHolder;
        if (direction == ItemTouchHelper.LEFT) {
            feedItemViewHolder.delete();
        } else {
            feedItemViewHolder.open();
        }
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return ((FeedItemViewHolder) viewHolder).getSwipeTrashold();
    }

    private FeedItemViewHolder lastSwiped = null;

    public FeedItemViewHolder getLastSwiped() {
        return lastSwiped;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        // TODO рисовать промежуточное состояние в onchilddrawover для увеличения fps?

        FeedItemViewHolder feedItemViewHolder = (FeedItemViewHolder) viewHolder;

        if (feedItemViewHolder != lastSwiped) {
            if (lastSwiped != null) {
                lastSwiped.resetSwipeState();
            }
            lastSwiped = feedItemViewHolder;
        }

        feedItemViewHolder.onSwipeChildDraw(dX, isCurrentlyActive);
        feedItemViewHolder.drawSwipeBackground(c);
    }
}
