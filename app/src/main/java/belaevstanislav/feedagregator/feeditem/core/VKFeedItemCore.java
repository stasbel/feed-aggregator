package belaevstanislav.feedagregator.feeditem.core;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import belaevstanislav.feedagregator.feedsource.FeedSourceName;
import belaevstanislav.feedagregator.util.Constant;

public class VKFeedItemCore extends FeedItemCore {
    public VKFeedItemCore(JSONObject item) throws JSONException {
        super(FeedSourceName.VK, item.getLong(Constant.VK_KEY_DATE));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public static final Parcelable.Creator<VKFeedItemCore> CREATOR =
            new Parcelable.Creator<VKFeedItemCore>() {
                @Override
                public VKFeedItemCore createFromParcel(Parcel in) {
                    return new VKFeedItemCore(in);
                }

                @Override
                public VKFeedItemCore[] newArray(int size) {
                    return new VKFeedItemCore[size];
                }
            };

    private VKFeedItemCore(Parcel in) {
        super(in);
    }
}
