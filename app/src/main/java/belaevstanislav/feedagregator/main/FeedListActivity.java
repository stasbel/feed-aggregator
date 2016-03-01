package belaevstanislav.feedagregator.main;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ProgressBar;

import com.mikepenz.materialdrawer.Drawer;

import belaevstanislav.feedagregator.R;
import belaevstanislav.feedagregator.feeditem.shell.FeedItem;
import belaevstanislav.feedagregator.feedlist.FeedItemViewHolder;
import belaevstanislav.feedagregator.feedlist.FeedListCursorAdapter;
import belaevstanislav.feedagregator.feedlist.FeedListOnScrollListener;
import belaevstanislav.feedagregator.feedlist.SwipeCallback;
import belaevstanislav.feedagregator.feedsource.twitter.TWITTER;
import belaevstanislav.feedagregator.singleton.database.DatabaseManager;
import belaevstanislav.feedagregator.singleton.storage.StorageKey;
import belaevstanislav.feedagregator.singleton.storage.StorageManager;
import belaevstanislav.feedagregator.singleton.threads.ThreadsManager;
import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.MyDrawer;
import belaevstanislav.feedagregator.util.MyToolbar;
import belaevstanislav.feedagregator.util.asynclatch.AsyncLatch;
import belaevstanislav.feedagregator.util.asynclatch.onShowFeedListListener;
import belaevstanislav.feedagregator.util.helpfullmethod.HelpfullMethod;
import belaevstanislav.feedagregator.util.helpfullmethod.IntentModifier;

public class FeedListActivity extends AppCompatActivity implements onShowFeedListListener, OnFeedItemOpenListener {
    private static FeedListCursorAdapter adapter;
    private RecyclerView feedList;
    private ProgressBar loadingBar;
    private Drawer drawer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            setContentView(R.layout.feed_list_layout);

            // toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            // drawer
            drawer = MyDrawer.createDrawer(this, toolbar);

            // main
            initializeFeedList();
            loadingBar = (ProgressBar) findViewById(R.id.loading_bar);
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
        DatabaseManager.getInstance().close();
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
            fetchFeedItems();
        }

        return super.onOptionsItemSelected(item);
    }

    public void initializeFeedList() {
        feedList = (RecyclerView) findViewById(R.id.feed_list);
        feedList.setHasFixedSize(true);
        feedList.setLayoutManager(new LinearLayoutManager(this));
        feedList.setItemAnimator(new DefaultItemAnimator());
        SwipeCallback callback = new SwipeCallback();
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(feedList);
        feedList.addOnScrollListener(new FeedListOnScrollListener(callback));
    }

    public void fetchFeedItems() {
        // TODO не удалять старые, a десерализовать
        if (!StorageManager.getInstance().getBoolean(StorageKey.IS_SAVE_NEWS)) {
            DatabaseManager.getInstance().deleteAll();
        }

        feedList.setVisibility(View.INVISIBLE);
        loadingBar.setVisibility(View.VISIBLE);

        AsyncLatch asyncLatch = new AsyncLatch(Constant.SOURCES_COUNT, this);
        TWITTER.fetchFeedItems(asyncLatch);
    }

    @Override
    public void onShowFeedList() {
        // get & insert cursor
        Cursor cursor = DatabaseManager.getInstance().getAll();
        adapter = new FeedListCursorAdapter(this, cursor);
        feedList.setAdapter(adapter);

        // renember last time & remove loading bar
        StorageManager.getInstance().saveLong(StorageKey.LAST_TIME_OF_FEED_LIST_REFRESH, HelpfullMethod.getNowTime());
        loadingBar.setVisibility(View.INVISIBLE);
        feedList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onOpen(final int position, final long id) {
        HelpfullMethod.createActivity(this, SingleFeedItemActivity.class, new IntentModifier() {
            @Override
            public void modify(Intent intent) {
                intent.putExtra(Constant.FEED_ITEM_ID_POSITION, position);
                intent.putExtra(Constant.FEED_ITEM_ID_KEY, id);
            }
        });
    }

    public static class SingleFeedItemActivity extends AppCompatActivity {
        private int position;
        private long id;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (savedInstanceState == null) {
                setContentView(R.layout.single_feed_item_layout);

                // toolbar
                MyToolbar.setToolbar(this);

                // feeditem
                Bundle bundle = getIntent().getExtras();
                position = bundle.getInt(Constant.FEED_ITEM_ID_POSITION);
                id = bundle.getLong(Constant.FEED_ITEM_ID_KEY);
                FeedItem feedItem = ThreadsManager.getInstance().fetchParseTask(id);
                View view = findViewById(android.R.id.content);
                FeedItemViewHolder viewHolder = new FeedItemViewHolder(null, null, view);
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
                DatabaseManager.getInstance().delete(id);
                adapter.swapCursor(DatabaseManager.getInstance().getAll());
                adapter.notifyItemRemoved(position);
                onBackPressed();
            }

            return super.onOptionsItemSelected(item);
        }
    }
}

// TODO OLD TODO'es
// feedlist
// TODO где-то нужно начинать поток с десеализацией старых новостей

// sources
// TODO везде настроить время + youtube через многопотоков потоки + обработка в потоках
// TODO infinite loop при нулевом запросе и при запросе только в вк

// big tasks
// TODO внешний вид новости + recycler view max + свайп влеов/вправо + (ошибки про recycler view в логах?)
// TODO разобраться с exceptiona'mi + gradle-зависимости + все сторонние библиотеки + лицензии? + Log.e
// TODO переписать все без api (с get-post запросами) (надо ли?) + Account Manager (?)
// TODO как работают thread'ы? возможно, надо добавить возможность вставлять async task в thread pool, потому что async task'ов уже многовато (fetch у пары новостей впереди)
// TODO FULL TWITTER LOGIN
// TODO сохраненные feed item?
// TODO можно подождать обработки первых нескольких feeditem'ов (async), чтобы пользователь не видел подгрузки первых картинок
// TODO (THREADS) все с приоритетами надо переписывать: нужен thread pool executor, который принимает asynk task, runnable, callable, сравнивает-
// TODO (THREADS) -своим компаратором, является fixed thread pool из 2*CORES + 1 thread'ов, и умно раздает Process.setThreadPrority в threadFactory-
// TODO (THREADS) -(backgroud или foreground (обратно пропорционально объему работы и в соствествие с приоритетом) или еще чего), тогда-
// TODO (THREADS) -можно будет переписать все запросы от Picasso в синхронизированном варианте (вставленном в async task) Итог:-
// TODO (THREADS) -все кроме асинронных логинов из api (<constant штук) будет выполнятся в thread pool'e

// TODO NEW
// TODO user interface / notifications
// TODO переписать с fragment'ами для больших экранов
// TODO overviewscrenn? swipe круг по экрану to refresh?
// TODO поиск в toolbar'e, аккаунты в drawer, selection в новости
