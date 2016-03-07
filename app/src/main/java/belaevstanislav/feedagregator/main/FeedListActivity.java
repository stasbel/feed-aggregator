package belaevstanislav.feedagregator.main;

import android.app.Activity;
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
import belaevstanislav.feedagregator.util.view.MyDrawer;
import belaevstanislav.feedagregator.util.view.MyToolbar;
import belaevstanislav.feedagregator.util.globalinterface.OnFeedItemOpenListener;
import belaevstanislav.feedagregator.util.helpmethod.HelpMethod;
import belaevstanislav.feedagregator.util.helpmethod.IntentModifier;

public class FeedListActivity extends AppCompatActivity implements OnFeedItemOpenListener, SwipeRefreshLayout.OnRefreshListener {
    private static FeedListCursorAdapter adapter = null;
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

            // deserealize
            startDeserealizingDataService();
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

    private static void handleEmptyLongClick(final Activity activity, final int id) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View v = activity.findViewById(id);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_list_menu, menu);
        handleEmptyLongClick(this, R.id.action_delete_all);
        handleEmptyLongClick(this, R.id.action_refresh);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                // TODO исправить: быстро 2 раза = 2x items
                onRefresh();
                break;
            case R.id.action_delete_all:
                data.database.deleteAll();
                showFeedList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        startFetchingDataService();
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

    private void startDeserealizingDataService() {
        Intent intent = new Intent(this, DataService.class);
        intent.putExtra(DataServiceCommand.COMMAND_KEY, (Parcelable) DataServiceCommand.DESEREALIZE_ITEMS);
        startService(intent);
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
        if (adapter == null) {
            adapter = new FeedListCursorAdapter(cursor, data, this);
            feedList.setAdapter(adapter);
        } else {
            adapter.swapCursor(cursor);
        }

        // renember last time
        // TODO когда надо?
        // data.storage.saveLong(StorageKey.LAST_TIME_OF_FEED_LIST_REFRESH, HelpMethod.getNowTime());

        // remove loading bar
        swipeRefreshLayout.setRefreshing(false);

        // scroll flags
        feedList.post(new Runnable() {
            @Override
            public void run() {
                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
                if (isRecyclerScrollable()) {
                    params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                            | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                } else {
                    params.setScrollFlags(0);
                }
                toolbar.setLayoutParams(params);
            }
        });
    }

    @Override
    public void onOpen(final int position, final long id, final boolean isFullWay) {
        HelpMethod.createActivity(this, SingleFeedItemActivity.class, new IntentModifier() {
            @Override
            public void modify(Intent intent) {
                intent.putExtra(Constant.FEED_ITEM_POSITION_KEY, position);
                intent.putExtra(Constant.FEED_ITEM_ID_KEY, id);
                intent.putExtra(Constant.FEED_ITEM_IS_FULL_WAY, isFullWay);
            }
        });
    }

    public static class SingleFeedItemActivity extends AppCompatActivity {
        private Data data;
        private int position;
        private long id;
        private boolean isFullWay;

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
                isFullWay = bundle.getBoolean(Constant.FEED_ITEM_IS_FULL_WAY);
                FeedItem feedItem = data.taskPool.fetchFeedItem(id);
                View view = findViewById(android.R.id.content);
                FeedItemViewHolder viewHolder = new FeedItemViewHolder(null, data, null, view);
                feedItem.drawView(this, viewHolder, true);
            }
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();

            if (!isFullWay) {
                adapter.notifyItemChanged(position);
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.single_feed_item_menu, menu);
            handleEmptyLongClick(this, R.id.action_delete);
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
// TODO сделать все из google документа
// TODO main activity + badge

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
// TODO все размеры из layout ов перенести в dimens?
