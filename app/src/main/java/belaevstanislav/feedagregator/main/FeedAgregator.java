package belaevstanislav.feedagregator.main;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.util.Constant;
import io.fabric.sdk.android.Fabric;

public class FeedAgregator extends Application {
    private static Context context;
    private Data data;

    public Data getData() {
        return data;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // context
        context = this;

        // data
        data = new Data(this);

        // twitter
        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig(Constant.TWITTER_API_KEY,
                Constant.TWITTER_API_SECRET);
        Fabric fabric = new Fabric.Builder(this)
                .kits(new Twitter(twitterAuthConfig), new Crashlytics(), new Answers())
                .debuggable(true)
                .build();
        Fabric.with(fabric);
    }
}
