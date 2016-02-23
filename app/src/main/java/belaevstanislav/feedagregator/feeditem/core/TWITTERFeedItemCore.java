package belaevstanislav.feedagregator.feeditem.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;

import belaevstanislav.feedagregator.feedsource.FeedSourceName;
import belaevstanislav.feedagregator.util.Constant;

public class TWITTERFeedItemCore extends FeedItemCore {
    public TWITTERFeedItemCore(Tweet tweet) throws ParseException {
        super(Constant.TWITTER_TIME_PATTERN.parse(tweet.createdAt).getTime() / 1000,
                FeedSourceName.TWITTER);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public static final Parcelable.Creator<TWITTERFeedItemCore> CREATOR =
            new Parcelable.Creator<TWITTERFeedItemCore>() {
                @Override
                public TWITTERFeedItemCore createFromParcel(Parcel in) {
                    return new TWITTERFeedItemCore(in);
                }

                @Override
                public TWITTERFeedItemCore[] newArray(int size) {
                    return new TWITTERFeedItemCore[size];
                }
            };

    private TWITTERFeedItemCore(Parcel in) {
        super(in);
    }
}
