package belaevstanislav.feedagregator.feedlist;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import belaevstanislav.feedagregator.R;
import belaevstanislav.feedagregator.feeditem.shell.FeedItem;
import belaevstanislav.feedagregator.feedlist.baseadapter.CursorRecyclerViewAdapter;
import belaevstanislav.feedagregator.singleton.threads.ThreadsManager;
import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.HighPriorityAsyncTask;

public class FeedListCursorAdapter extends CursorRecyclerViewAdapter<FeedItemViewHolder> {
    private final Activity activity;
    private final LayoutInflater layoutInflater;
    private final int indexColumnId;

    public FeedListCursorAdapter(Activity activity, Cursor cursor) {
        super(cursor);
        this.activity = activity;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.indexColumnId = cursor.getColumnIndex(Constant.KEY_TABLE_ID);
    }

    @Override
    public FeedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.feed_list_row_layout, parent, false);
        return new FeedItemViewHolder(this, activity, view);
    }

    private class GetFeedItemAndDrawViewAsyncTask extends HighPriorityAsyncTask<Void, Void, FeedItem> {
        private final long id;
        private final FeedItemViewHolder holder;

        public GetFeedItemAndDrawViewAsyncTask(long id, FeedItemViewHolder holder) {
            this.id = id;
            this.holder = holder;
        }

        @Override
        protected FeedItem inBackground(Void... params) {
            return ThreadsManager.getInstance().fetchParseTask(id);
        }

        @Override
        protected void onPostExecute(FeedItem feedItem) {
            super.onPostExecute(feedItem);
            feedItem.drawView(activity, holder, true);
        }
    }

    @Override
    public void onBindViewHolderCursor(FeedItemViewHolder holder, Cursor cursor) {
        long id = cursor.getLong(indexColumnId);

        holder.setId(id);
        holder.resetSwipeState();

        // TODO отсюда можно запускать fetch некоторых item'ов далее
        if (ThreadsManager.getInstance().isFinished(id)) {
            ThreadsManager.getInstance()
                    .fetchParseTask(cursor.getLong(indexColumnId))
                    .drawView(activity, holder, true);
        } else {
            new GetFeedItemAndDrawViewAsyncTask(id, holder)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}
