package belaevstanislav.feedagregator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import belaevstanislav.feedagregator.feeditem.shell.FeedItem;
import belaevstanislav.feedagregator.feedlist.FeedItemViewHolder;
import belaevstanislav.feedagregator.singleton.threads.ThreadsManager;

public class SingleFeedItemActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_list_row_layout);
        long id  = getIntent().getExtras().getLong("id");
        FeedItem feedItem = ThreadsManager.getInstance().fetchParseTask(id);
        View view = this.findViewById(android.R.id.content);
        FeedListActivity feedListActivity = FeedListActivity.getActivity();
        FeedItemViewHolder viewHolder = new FeedItemViewHolder(feedListActivity.getAdapter(), view);
        feedItem.drawView(FeedAgregator.getContext(), viewHolder, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FeedItemViewHolder.lastOpen.delete();
    }
}
