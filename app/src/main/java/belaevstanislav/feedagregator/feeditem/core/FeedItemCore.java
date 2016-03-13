package belaevstanislav.feedagregator.feeditem.core;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import belaevstanislav.feedagregator.feedsource.FeedSourceName;

public abstract class FeedItemCore implements Comparable<FeedItemCore>, Parcelable {
    private final FeedSourceName type;
    private final long date;

    protected FeedItemCore(FeedSourceName type, long date) {
        this.type = type;
        this.date = date;
    }

    protected FeedItemCore(FeedItemCore core) {
        type = core.getType();
        date = core.getDate();
    }

    public FeedSourceName getType() {
        return type;
    }

    public long getDate() {
        return date;
    }

    @Override
    public int compareTo(@NonNull FeedItemCore another) {
        return ((Long)another.date).compareTo(date);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(type, flags);
        dest.writeLong(date);
    }

    protected FeedItemCore(Parcel in) {
        type = in.readParcelable(FeedSourceName.class.getClassLoader());
        date = in.readLong();
    }
}
