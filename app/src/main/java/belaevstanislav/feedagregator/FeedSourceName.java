package belaevstanislav.feedagregator;

import android.os.Parcel;
import android.os.Parcelable;

public enum FeedSourceName implements Parcelable {
    TWITTER;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }

    public static final Parcelable.Creator<FeedSourceName> CREATOR =
            new Parcelable.Creator<FeedSourceName>() {
                @Override
                public FeedSourceName createFromParcel(Parcel in) {
                    return FeedSourceName.values()[in.readInt()];
                }

                @Override
                public FeedSourceName[] newArray(int size) {
                    return new FeedSourceName[size];
                }
            };
}
