package belaevstanislav.feedagregator.feedlist;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import belaevstanislav.feedagregator.R;
import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.feeditem.shell.FeedItem;
import belaevstanislav.feedagregator.feedlist.baseadapter.CursorRecyclerViewAdapter;
import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.globalinterface.OnFeedItemOpenListener;

public class FeedListCursorAdapter extends CursorRecyclerViewAdapter<FeedItemViewHolder> {
    private final Data data;
    private final Context context;
    private final OnFeedItemOpenListener onFeedItemOpenListener;
    private final LayoutInflater layoutInflater;
    private final int indexColumnId;

    public FeedListCursorAdapter(Cursor cursor, Data data, Context context) {
        super(cursor);
        this.data = data;
        this.context = context;
        this.onFeedItemOpenListener = (OnFeedItemOpenListener) context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.indexColumnId = cursor.getColumnIndex(Constant.KEY_TABLE_ID);
    }

    @Override
    public FeedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.feed_list_row_layout, parent, false);
        return new FeedItemViewHolder(this, data, onFeedItemOpenListener, view);
    }

    @Override
    public void onBindViewHolderCursor(FeedItemViewHolder holder, Cursor cursor) {
        long id = cursor.getLong(indexColumnId);

        holder.setId(id);
        holder.resetSwipeState();

        // TODO отсюда можно запускать fetch некоторых item'ов далее
        if (data.taskPool.isFinished(id)) {
            data.taskPool.fetchParseTask(cursor.getLong(indexColumnId))
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
            return data.taskPool.fetchParseTask(id);
        }

        @Override
        protected void onPostExecute(FeedItem feedItem) {
            feedItem.drawView(context, holder, true);
        }
    }
}
