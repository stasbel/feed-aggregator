package belaevstanislav.feedagregator.feedlist;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.materialdrawer.Drawer;

import belaevstanislav.feedagregator.R;
import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.feeditem.shell.FeedItem;
import belaevstanislav.feedagregator.feedlist.baseadapter.CursorRecyclerViewAdapter;
import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.globalinterface.OnFeedItemOpenListener;
import belaevstanislav.feedagregator.util.view.MyDrawer;

public class FeedListCursorAdapter extends CursorRecyclerViewAdapter<FeedItemViewHolder> {
    private final Data data;
    private final Context context;
    private final OnFeedItemOpenListener onFeedItemOpenListener;
    private final LayoutInflater layoutInflater;
    private final int indexColumnId;
    private final Drawer drawer;

    public FeedListCursorAdapter(Cursor cursor, Data data, Context context, Drawer drawer) {
        super(cursor);
        this.data = data;
        this.context = context;
        this.onFeedItemOpenListener = (OnFeedItemOpenListener) context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.indexColumnId = cursor.getColumnIndex(Constant.DATABASE_KEY_TABLE_ID);
        this.drawer = drawer;
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        MyDrawer.setBadge(drawer, newCursor.getCount());
        return super.swapCursor(newCursor);
    }

    @Override
    public FeedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.feed_list_row_layout, parent, false);
        return new FeedItemViewHolder(this, data, onFeedItemOpenListener, view);
    }

    @Override
    public void onBindViewHolderCursor(FeedItemViewHolder holder, Cursor cursor) {
        if (cursor.getPosition() == cursor.getCount() - 1) {
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

    public void deleteFeedItem(int position) {
        swapCursor(data.database.getAll());
        notifyItemRemoved(position);
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
