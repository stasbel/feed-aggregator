package belaevstanislav.feedagregator.feedlist;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.feeditem.shell.FeedItem;
import belaevstanislav.feedagregator.feedlist.baseadapter.CursorRecyclerViewAdapter;
import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.globalinterface.OnFeedItemActionListener;
import belaevstanislav.feedagregator.util.view.MenuDrawer;

public class FeedListCursorAdapter extends CursorRecyclerViewAdapter<FeedItemViewHolder> {
    private final Data data;
    private final Context context;
    private final OnFeedItemActionListener onFeedItemActionListener;
    private final LayoutInflater layoutInflater;
    private final int indexColumnId;
    private final MenuDrawer drawer;

    public FeedListCursorAdapter(Cursor cursor, Data data, Activity activity, MenuDrawer drawer) {
        super(cursor);
        this.data = data;
        this.context = activity;
        this.onFeedItemActionListener = (OnFeedItemActionListener) context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.indexColumnId = cursor.getColumnIndex(Constant.DATABASE_KEY_TABLE_ID);
        this.drawer = drawer;
    }

    public Cursor swapCursor(Cursor newCursor, boolean isNeedToUpdateBadges) {
        if (isNeedToUpdateBadges) {
            drawer.updateBadges();
        }
        return super.swapCursor(newCursor);
    }

    @Override
    public FeedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(Constant.LAYOUT_FEED_LIST_ROW, parent, false);
        return new FeedItemViewHolder(this, data, onFeedItemActionListener, view);
    }

    @Override
    public void onBindViewHolderCursor(FeedItemViewHolder holder, Cursor cursor) {
        if (cursor.getPosition() == 0) {
            holder.itemView.setBackground(null);
        }

        long id = cursor.getLong(indexColumnId);

        holder.setId(id);
        holder.resetSwipeState();

        // TODO отсюда можно запускать fetch некоторых item'ов далее
        if (data.taskPool.isFinishBuildFeedItem(id)) {
            data.taskPool.fetchFeedItem(cursor.getLong(indexColumnId))
                    .drawView(context, holder, true);
        } else {
            new GetFeedItemAndDrawViewAsyncTask(id, holder).execute();
        }
    }

    public void deleteFeedItem(int position, long id) {
        onFeedItemActionListener.onDelete(position, id);
    }

    private class GetFeedItemAndDrawViewAsyncTask extends AsyncTask<Void, Void, FeedItem> {
        private final long id;
        private final FeedItemViewHolder holder;

        public GetFeedItemAndDrawViewAsyncTask(long id, FeedItemViewHolder holder) {
            this.id = id;
            this.holder = holder;
        }

        @Override
        protected FeedItem doInBackground(Void... params) {
            return data.taskPool.fetchFeedItem(id);
        }

        @Override
        protected void onPostExecute(FeedItem feedItem) {
            feedItem.drawView(context, holder, true);
        }
    }
}
