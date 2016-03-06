package belaevstanislav.feedagregator.feeditem.shell;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.util.Linkify;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetEntities;
import com.twitter.sdk.android.core.models.UrlEntity;

import java.text.ParseException;

import belaevstanislav.feedagregator.R;
import belaevstanislav.feedagregator.feeditem.core.FeedItemCore;

public class TWITTERFeedItem extends FeedItem {
    private final String text;
    private final String imageUrl;

    public TWITTERFeedItem(FeedItemCore core, long id, Tweet tweet) throws ParseException {
        super(core, id, tweet.user.name, tweet.user.profileImageUrl);

        TweetEntities entities = tweet.entities;

        String preText = tweet.text;
        if (entities.urls != null) {
            for (int index = 0; index < entities.urls.size(); index++) {
                UrlEntity entity = entities.urls.get(index);
                preText = preText.replaceAll(entity.url, entity.displayUrl);
            }
        }
        this.text = preText;

        String preImageUrl = null;
        if (entities.media != null) {
            preImageUrl = entities.media.get(0).mediaUrl;
        }
        if (tweet.extendedEtities != null && tweet.extendedEtities.media.get(0).type.equals("photo")) {
            preImageUrl = tweet.extendedEtities.media.get(0).mediaUrl;
        }
        this.imageUrl = preImageUrl;
    }

    @Override
    public int getLogo() {
        return R.drawable.twitter_logo;
    }

    @Override
    void fillContent(LinearLayout content, Context context) {
        if (!text.equals("")) {
            TextView textView = new TextView(context);
            textView.setText(text);
            Linkify.addLinks(textView, Linkify.ALL);
            content.addView(textView);
        }

        if (imageUrl != null) {
            ImageView imageView = new ImageView(context);
            Picasso.with(context).load(imageUrl).tag(context).into(imageView);
            content.addView(imageView);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(text);
        dest.writeString(imageUrl);
    }

    public static final Parcelable.Creator<TWITTERFeedItem> CREATOR =
            new Parcelable.Creator<TWITTERFeedItem>() {
                @Override
                public TWITTERFeedItem createFromParcel(Parcel in) {
                    return new TWITTERFeedItem(in);
                }

                @Override
                public TWITTERFeedItem[] newArray(int size) {
                    return new TWITTERFeedItem[size];
                }
            };

    private TWITTERFeedItem(Parcel in) {
        super(in);
        text = in.readString();
        imageUrl = in.readString();
    }
}

