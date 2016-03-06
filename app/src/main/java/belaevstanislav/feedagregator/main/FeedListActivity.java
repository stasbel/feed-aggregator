package belaevstanislav.feedagregator.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;

import belaevstanislav.feedagregator.R;
import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.data.storage.StorageKey;
import belaevstanislav.feedagregator.feeditem.shell.FeedItem;
import belaevstanislav.feedagregator.feedlist.FeedItemViewHolder;
import belaevstanislav.feedagregator.feedlist.FeedListCursorAdapter;
import belaevstanislav.feedagregator.feedlist.FeedListOnScrollListener;
import belaevstanislav.feedagregator.feedlist.SwipeCallback;
import belaevstanislav.feedagregator.service.DataService;
import belaevstanislav.feedagregator.service.DataServiceCommand;
import belaevstanislav.feedagregator.service.Notificator;
import belaevstanislav.feedagregator.service.NotificatorMessage;
import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.MyDrawer;
import belaevstanislav.feedagregator.util.MyToolbar;
import belaevstanislav.feedagregator.util.globalinterface.OnFeedItemOpenListener;
import belaevstanislav.feedagregator.util.helpfullmethod.HelpfullMethod;
import belaevstanislav.feedagregator.util.helpfullmethod.IntentModifier;

public class FeedListActivity extends AppCompatActivity implements OnFeedItemOpenListener, SwipeRefreshLayout.OnRefreshListener {
    private static FeedListCursorAdapter adapter;
    private Data data;
    private RecyclerView feedList;
    private Toolbar toolbar;
    private Drawer drawer;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificatorMessage message = intent.getParcelableExtra(NotificatorMessage.MESSAGE_KEY);
            switch (message) {
                case READY_TO_SHOW:
                    showFeedList();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            setContentView(R.layout.feed_list_layout);

            // data
            data = ((FeedAgregator) getApplication()).getData();

            // toolbar
            initializeToolbar();

            // drawer
            drawer = MyDrawer.createDrawer(this, toolbar);

            // swiperefresh
            (swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh))
                    .setOnRefreshListener(this);

            // notificator
            IntentFilter intentFilter = new IntentFilter(Notificator.ACTION);
            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);

            // main
            initializeFeedList();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        drawer.setSelectionAtPosition(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        data.database.close();
        stopDataService();
    }

    private void stopDataService() {
        Intent intent = new Intent(this, DataService.class);
        stopService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_list_menu, menu);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View v = findViewById(R.id.action_refresh);

                if (v != null) {
                    v.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return false;
                        }
                    });
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            // TODO исправить: быстро 2 раза = 2x items
            onRefresh();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        fetchFeedItems();
    }

    private void initializeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeFeedList() {
        feedList = (RecyclerView) findViewById(R.id.feed_list);
        feedList.setHasFixedSize(true);
        feedList.setLayoutManager(new LinearLayoutManager(this));
        feedList.setItemAnimator(new DefaultItemAnimator());
        SwipeCallback callback = new SwipeCallback();
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(feedList);
        feedList.addOnScrollListener(new FeedListOnScrollListener(this, callback));

        // TODO adadpter -> deserialize old items -> get rid of error
    }

    private void fetchFeedItems() {
        // TODO не удалять старые, a десерализовать
        if (!data.storage.getBoolean(StorageKey.IS_SAVE_NEWS)) {
            data.database.deleteAll();
        }

        startFetchingDataService();

        // TODO delete?
        //AsyncLatch asyncLatch = new AsyncLatch(Constant.SOURCES_COUNT, this);
        //TWITTER.fetchFeedItems(asyncLatch);
    }

    private void startFetchingDataService() {
        Intent intent = new Intent(this, DataService.class);
        intent.putExtra(DataServiceCommand.COMMAND_KEY, (Parcelable) DataServiceCommand.FETCH_NEW_ITEMS);
        startService(intent);
    }

    private boolean isRecyclerScrollable() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) feedList.getLayoutManager();
        RecyclerView.Adapter adapter = feedList.getAdapter();
        return !(layoutManager == null || adapter == null)
                && layoutManager.findLastCompletelyVisibleItemPosition() < adapter.getItemCount() - 1;
    }

    private void showFeedList() {
        // get & insert cursor
        Cursor cursor = data.database.getAll();
        adapter = new FeedListCursorAdapter(cursor, data, this);
        feedList.setAdapter(adapter);

        // renember last time & remove loading bar
        data.storage.saveLong(StorageKey.LAST_TIME_OF_FEED_LIST_REFRESH, HelpfullMethod.getNowTime());
        swipeRefreshLayout.setRefreshing(false);

        // scroll flags
        feedList.post(new Runnable() {
            @Override
            public void run() {
                if (isRecyclerScrollable()) {
                    AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
                    params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                            | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                    toolbar.setLayoutParams(params);
                }
            }
        });
    }

    @Override
    public void onOpen(final int position, final long id) {
        HelpfullMethod.createActivity(this, SingleFeedItemActivity.class, new IntentModifier() {
            @Override
            public void modify(Intent intent) {
                intent.putExtra(Constant.FEED_ITEM_POSITION_KEY, position);
                intent.putExtra(Constant.FEED_ITEM_ID_KEY, id);
            }
        });
    }

    public static class SingleFeedItemActivity extends AppCompatActivity {
        private Data data;
        private int position;
        private long id;

        public SingleFeedItemActivity() {
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (savedInstanceState == null) {
                setContentView(R.layout.single_feed_item_layout);

                // data
                data = ((FeedAgregator) getApplication()).getData();

                // toolbar
                MyToolbar.setToolbar(this);

                // feeditem
                Bundle bundle = getIntent().getExtras();
                position = bundle.getInt(Constant.FEED_ITEM_POSITION_KEY);
                id = bundle.getLong(Constant.FEED_ITEM_ID_KEY);
                FeedItem feedItem = data.taskPool.fetchParseTask(id);
                View view = findViewById(android.R.id.content);
                FeedItemViewHolder viewHolder = new FeedItemViewHolder(null, data, null, view);
                feedItem.drawView(this, viewHolder, true);
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.single_feed_item_menu, menu);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    final View v = findViewById(R.id.action_delete);

                    if (v != null) {
                        v.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                return false;
                            }
                        });
                    }
                }
            });
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.action_delete) {
                data.database.delete(id);
                adapter.deleteFeedItem(position);
                onBackPressed();
            }

            return super.onOptionsItemSelected(item);
        }
    }
}

// TODO IMPORTANT
// TODO десеаризация старых
// TODO ???
// TODO сделать все из google документа
// TODO переработать thread pool и task'и

// TODO NEW
// TODO feed source class
// TODO global thread safaty
// TODO внешний вид новости
// TODO user interface / notifications
// TODO переписать с fragment'ами для больших экранов
// TODO overviewscrenn? swipe круг по экрану to refresh?
// TODO поиск в toolbar'e, аккаунты в drawer, selection в новости
// TODO account manager?
// TODO ошибки recycler view
// TODO exeptions?
// TODO старые новости
// TODO можно подождать обработки первых нескольких feeditem'ов (async), чтобы пользователь не видел подгрузки первых картинок
// TODO threads
// TODO везде настроить время + youtube через многопотоков потоки + обработка в потоках
// TODO infinite loop при нулевом запросе и при запросе только в вк
// TODO свой view
// TODO content provider?
