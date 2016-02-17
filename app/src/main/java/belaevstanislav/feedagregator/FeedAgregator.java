package belaevstanislav.feedagregator;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import belaevstanislav.feedagregator.util.Constant;
import io.fabric.sdk.android.Fabric;

public class FeedAgregator extends Application {
    private static Context context;

    public FeedAgregator() {
        context = this;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig(Constant.TWITTER_API_KEY,
                Constant.TWITTER_API_SECRET);
        Fabric fabric = new Fabric.Builder(this)
                .kits(new Twitter(twitterAuthConfig))
                //.debuggable(true)
                .build();
        Fabric.with(fabric);
    }
}
