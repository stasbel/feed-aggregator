package belaevstanislav.feedagregator.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import belaevstanislav.feedagregator.main.FeedAgregator;
import belaevstanislav.feedagregator.R;

// TODO много классов constant с общим предком в package?
public class Constant {
    private static final Context context = FeedAgregator.getContext();
    private static final Resources resources = context.getResources();
    private static final DisplayMetrics displayMetrics = resources.getDisplayMetrics();

    private static int getResolvedColor(int resourceId) {
        return ContextCompat.getColor(context, resourceId);
    }

    // INTENTKEYS
    public static final String FEED_ITEM_ID_POSITION = "position";
    public static final String FEED_ITEM_ID_KEY = "id";

    // SCREEN
    public static final float SCREEN_HEIGHT_PX = displayMetrics.heightPixels;
    public static final float SCREEN_WIDTH_PX = displayMetrics.widthPixels;

    // STORAGE
    public static final long STORAGE_DEFAULT_LONG = 0L;
    public static final long STORAGE_DEFAULT_LAST_TWEET_ID = 0L;
    public static final boolean STORAGE_DEFAULT_IS_SAVE_NEWS = true;

    // FEEDSOURCE
    public static final int SOURCES_COUNT = 1;

    // DATABASE
    public static final String DATABASE_NAME = "UnReadFeedItem.db";
    public static final int DATABASE_VERSION = 6;
    public static final String COMMON_INFORMATION_TABLE_NAME = "common_information";
    public static final String FEED_ITEM_BYTECODE_TABLE_NAME = "feed_item_bytecode";
    public static final String KEY_TABLE_ID = "_id";
    public static final String KEY_TABLE_TIME = "time";
    public static final String KEY_TABLE_SOURCE = "source";
    public static final String KEY_TABLE_BYTECODE = "bytecode";

    // TWITTER
    public static final String TWITTER_API_KEY = resources.getString(R.string.twitter_api_key);
    public static final String TWITTER_API_SECRET = resources.getString(R.string.twitter_api_sercet);
    // TODO change to 200
    public static final Integer MAX_TWEETS_PER_PAGE = 100;
    public static final Integer FIRST_TWITTER_QUERY_PAGE_SIZE = 10;
    public static final SimpleDateFormat TWITTER_TIME_PATTERN = new SimpleDateFormat("EEE MMM dd HH:mm:ss +SSSS yyyy", Locale.ENGLISH);
    static {
        TWITTER_TIME_PATTERN.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    // FEEDITEM
    public static final SimpleDateFormat FEED_ITEM_HEAD_TIME_PATTERN = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    static {
        FEED_ITEM_HEAD_TIME_PATTERN.setTimeZone(TimeZone.getDefault());
    }

    // FEEDLIST
    public static final float ROW_LAYOUT_MAX_HEIGHT = resources.getDimensionPixelSize(R.dimen.row_layout_max_height);
    public static final float SWIPE_FULL_TRASHHOLD = 1f;
    public static final float SWIPE_LOCK_SIZE = resources.getDimensionPixelSize(R.dimen.swipe_lock_size);
    public static final float SWIPE_LOCK_TRASHHOLD = 1 - (SWIPE_LOCK_SIZE / SCREEN_WIDTH_PX);
    public static final float SWIPE_LOCK_MARGIN = resources.getDimensionPixelSize(R.dimen.swipe_lock_margin);
    public static final Paint SWIPE_LEFT_BACKGROUND_PAINT = new Paint();
    static {
        SWIPE_LEFT_BACKGROUND_PAINT.setColor(getResolvedColor(R.color.swipe_left_background));
    }
    public static final Paint SWIPE_RIGHT_BACKGROUND_PAINT = new Paint();
    static {
        SWIPE_RIGHT_BACKGROUND_PAINT.setColor(getResolvedColor(R.color.swipe_right_background));
    }
    private static final float SWIPE_TEXT_SIZE = resources.getDimensionPixelSize(R.dimen.swipe_text_size);
    public static final String SWIPE_LEFT_TEXT = resources.getString(R.string.swipe_left_text);
    public static final Rect SWIPE_LEFT_RECT = new Rect();
    public static final Paint SWIPE_LEFT_TEXT_PAINT = new Paint();
    static {
        SWIPE_LEFT_TEXT_PAINT.setColor(getResolvedColor(R.color.swipe_text_color));
        SWIPE_LEFT_TEXT_PAINT.setTextSize(SWIPE_TEXT_SIZE);
        SWIPE_LEFT_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);
        SWIPE_LEFT_TEXT_PAINT.getTextBounds(SWIPE_LEFT_TEXT, 0, SWIPE_LEFT_TEXT.length(), SWIPE_LEFT_RECT);
    }
    public static final String SWIPE_RIGHT_TEXT = resources.getString(R.string.swipe_right_text);
    public static final Paint SWIPE_RIGHT_TEXT_PAINT = new Paint();
    public static final Rect SWIPE_RIGHT_RECT = new Rect();
    static {
        SWIPE_RIGHT_TEXT_PAINT.setColor(getResolvedColor(R.color.swipe_text_color));
        SWIPE_RIGHT_TEXT_PAINT.setTextSize(SWIPE_TEXT_SIZE);
        SWIPE_RIGHT_TEXT_PAINT.setTextAlign(Paint.Align.LEFT);
        SWIPE_RIGHT_TEXT_PAINT.getTextBounds(SWIPE_RIGHT_TEXT, 0, SWIPE_RIGHT_TEXT.length(), SWIPE_RIGHT_RECT);
    }
}
