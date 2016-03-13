package belaevstanislav.feedagregator.feedlist.baseadapter;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class SwipeRefreshLayoutToggleScrollListener extends RecyclerView.OnScrollListener {
    private SwipeRefreshLayout mSwipeLayout;

    public SwipeRefreshLayoutToggleScrollListener(SwipeRefreshLayout swipeLayout) {
        mSwipeLayout = swipeLayout;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstVisible = llm.findFirstCompletelyVisibleItemPosition();
        if (firstVisible != RecyclerView.NO_POSITION)
            mSwipeLayout.setEnabled(firstVisible == 0);

    }
}