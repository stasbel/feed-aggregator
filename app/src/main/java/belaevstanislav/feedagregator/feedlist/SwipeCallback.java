package belaevstanislav.feedagregator.feedlist;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import belaevstanislav.feedagregator.util.Constant;

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
        ((FeedListCursorAdapter.FeedItemViewHolder) viewHolder).delete();
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return Constant.SWIPE_TRASHHOLD;
    }

    private FeedListCursorAdapter.FeedItemViewHolder lastSwiped = null;

    public FeedListCursorAdapter.FeedItemViewHolder getLastSwiped() {
        return lastSwiped;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        // TODO рисовать промежуточное состояние в onchilddrawover для увеличения fps?

        FeedListCursorAdapter.FeedItemViewHolder feedItemViewHolder = (FeedListCursorAdapter.FeedItemViewHolder) viewHolder;

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
