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
import android.widget.RelativeLayout;

import com.mikepenz.materialdrawer.DrawerBuilder;

import belaevstanislav.feedagregator.R;
import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.feeditem.shell.FeedItem;
import belaevstanislav.feedagregator.feedlist.FeedItemSeparator;
import belaevstanislav.feedagregator.feedlist.FeedItemViewHolder;
import belaevstanislav.feedagregator.feedlist.FeedListCursorAdapter;
import belaevstanislav.feedagregator.feedlist.FeedListOnScrollListener;
import belaevstanislav.feedagregator.feedlist.SwipeCallback;
import belaevstanislav.feedagregator.feedlist.baseadapter.SwipeRefreshLayoutToggleScrollListener;
import belaevstanislav.feedagregator.feedsource.FeedSourceName;
import belaevstanislav.feedagregator.service.DataService;
import belaevstanislav.feedagregator.service.DataServiceCommand;
import belaevstanislav.feedagregator.service.Notificator;
import belaevstanislav.feedagregator.service.NotificatorMessage;
import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.globalinterface.FeedListQuerries;
import belaevstanislav.feedagregator.util.globalinterface.OnFeedItemActionListener;
import belaevstanislav.feedagregator.util.helpmethod.HelpMethod;
import belaevstanislav.feedagregator.util.helpmethod.IntentModifier;
import belaevstanislav.feedagregator.util.view.MenuDrawer;
import belaevstanislav.feedagregator.util.view.MyToolbar;

public class FeedListActivity extends AppCompatActivity implements OnFeedItemActionListener,
        SwipeRefreshLayout.OnRefreshListener, FeedListQuerries {
    private static FeedListCursorAdapter adapter = null;
    private Data data;
    private RecyclerView feedList;
    private Toolbar toolbar;
    private MenuDrawer drawer;
    private SwipeCallback swipeCallback;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout placeholder;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificatorMessage message = intent.getParcelableExtra(NotificatorMessage.MESSAGE_KEY);
            switch (message) {
                case READY_TO_SHOW:
                    showAll();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            setContentView(Constant.LAYOUT_FEED_LIST);

            // placeholder
            placeholder = (RelativeLayout) findViewById(R.id.feed_list_placeholder);

            // data
            data = ((FeedAgregator) getApplication()).getData();

            // toolbar
            initializeToolbar();

            // drawer
            swipeCallback = new SwipeCallback();
            drawer = new MenuDrawer(new DrawerBuilder(), this, toolbar, swipeCallback);

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
        if (drawer != null) {
            drawer.setSelectionAtPosition(1);
        }
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
                if (!swipeRefreshLayout.isRefreshing()) {
                    onRefresh();
                }
                break;
            case R.id.action_delete_all:
                data.database.deleteAll();
                showAll();
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
        feedList.addItemDecoration(new FeedItemSeparator(this));
        feedList.setLayoutManager(new LinearLayoutManager(this));
        feedList.setItemAnimator(new DefaultItemAnimator());
        ItemTouchHelper helper = new ItemTouchHelper(swipeCallback);
        helper.attachToRecyclerView(feedList);
        feedList.addOnScrollListener(new FeedListOnScrollListener(this, swipeCallback));
        feedList.addOnScrollListener(new SwipeRefreshLayoutToggleScrollListener(swipeRefreshLayout));

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

    private void showFeedList(Cursor cursor, boolean isNeedToUpdateBadge) {
        // get & insert cursor
        if (adapter == null) {
            adapter = new FeedListCursorAdapter(cursor, data, this, drawer);
            feedList.setAdapter(adapter);
        }
        adapter.swapCursor(cursor, isNeedToUpdateBadge);

        // placeholder
        if (cursor.getCount() == 0) {
            placeholder.setVisibility(View.VISIBLE);
        } else {
            placeholder.setVisibility(View.INVISIBLE);
        }

        // renember last time
        // TODO когда надо?
        // data.storage.saveLong(StorageKey.LAST_TIME_OF_FEED_LIST_REFRESH, HelpMethod.getNowTime());

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
    public void showAll() {
        swipeRefreshLayout.setRefreshing(true);
        showFeedList(data.database.getAll(), true);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showOnlySource(FeedSourceName name) {
        swipeRefreshLayout.setRefreshing(true);
        showFeedList(data.database.getOnlySource(name), false);
        swipeRefreshLayout.setRefreshing(false);
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

    @Override
    public void onDelete(int position, long id) {
        data.database.delete(id);
        Cursor cursor = null;
        switch (drawer.getSelection()) {
            case ALL:
                cursor = data.database.getAll();
                break;
            case TWITTER:
                cursor = data.database.getOnlySource(FeedSourceName.TWITTER);
                break;
            case VK:
                cursor = data.database.getOnlySource(FeedSourceName.VK);
                break;

        }
        adapter.swapCursor(cursor, true);
        adapter.notifyItemRemoved(position);
        showFeedList(cursor, true);
    }

    public static class SingleFeedItemActivity extends AppCompatActivity {
        private Data data;
        private int position;
        private long id;
        private boolean isFullWay;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (savedInstanceState == null) {
                setContentView(Constant.LAYOUT_SINGLE_FEED_ITEM);

                // data
                data = ((FeedAgregator) getApplication()).getData();

                // toolbar
                MyToolbar.setToolbar(this);

                // feeditem
                drawFeedItem();
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
                adapter.deleteFeedItem(position, id);
                onBackPressed();
            }

            return super.onOptionsItemSelected(item);
        }

        private void drawFeedItem() {
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
}

// TODO TODAY
// TODO VK (repost style)
// TODO YOUTUBE
// TODO drawer
// TODO account manager
// TODO placeholder for no news

// TODO IMPORTANT
// TODO сделать все из google документа

// TODO ISSUES
// TODO иногда пропадают фоны свайпов
// TODO иногда пропадают разделители

// TODO STYLE
// TODO выделялки у twitter'a

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
