package belaevstanislav.feedagregator;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import belaevstanislav.feedagregator.feedlist.FeedListCursorAdapter;
import belaevstanislav.feedagregator.feedlist.FeedListOnScrollListener;
import belaevstanislav.feedagregator.feedlist.SwipeCallback;
import belaevstanislav.feedagregator.feedsource.twitter.TWITTER;
import belaevstanislav.feedagregator.singleton.database.DatabaseManager;
import belaevstanislav.feedagregator.singleton.storage.StorageKeys;
import belaevstanislav.feedagregator.singleton.storage.StorageManager;
import belaevstanislav.feedagregator.util.AsyncLatch;
import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.HelpfullMethod;
import belaevstanislav.feedagregator.util.InvokingMethod;

public class FeedListActivity extends Activity implements InvokingMethod {
    private static FeedListActivity activity;

    public FeedListActivity() {
        activity = this;
    }

    public static FeedListActivity getActivity() {
        return activity;
    }

    private RecyclerView feedList;
    private FeedListCursorAdapter adapter;

    public FeedListCursorAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_list_layout);

        feedList = (RecyclerView) findViewById(R.id.feed_list);
        feedList.setHasFixedSize(true);
        feedList.setLayoutManager(new LinearLayoutManager(this));
        feedList.setItemAnimator(new DefaultItemAnimator());
        SwipeCallback callback = new SwipeCallback();
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(feedList);
        feedList.addOnScrollListener(new FeedListOnScrollListener(callback));

        DatabaseManager.getInstance().deleteAll();

        AsyncLatch asyncLatch = new AsyncLatch(Constant.SOURCES_COUNT, this);

        // listview
        // TODO где-то нужно начинать поток с десеализацией старых новостей
        // TODO в таsk'ах сразу строить view'шки? (вроде нельзя)

        // TODO все, что не должно занимать время UI треда, должно происходить не в UI треде

        // TODO везде настроить время + youtube через многопотоков потоки + обработка в потоках
        // TODO infinite loop при нулевом запросе и при запросе только в вк

        // big tasks
        // TODO fragments? overviewscrenn? swipe круг по экрану to refresh?
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

        TWITTER.fetchFeedItems(asyncLatch);
    }

    @Override
    public void invoke() {
        Cursor cursor = DatabaseManager.getInstance().getAll();
        adapter = new FeedListCursorAdapter(FeedListActivity.this, cursor);
        feedList.setAdapter(adapter);
        StorageManager.getInstance().saveLong(StorageKeys.LAST_TIME_OF_FEED_LIST_REFRESH,
                HelpfullMethod.getNowTime());
        findViewById(R.id.loading_bar).setVisibility(View.GONE);
    }
}
