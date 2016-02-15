package belaevstanislav.feedagregator.feeditem.core;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import belaevstanislav.feedagregator.FeedSourceName;

public abstract class FeedItemCore implements Comparable<FeedItemCore>, Parcelable {
    private final long date;
    private final FeedSourceName type;

    protected FeedItemCore(long date, FeedSourceName type) {
        this.date = date;
        this.type = type;
    }

    protected FeedItemCore(FeedItemCore core) {
        date = core.getDate();
        type = core.getType();
    }

    public long getDate() {
        return date;
    }

    public FeedSourceName getType() {
        return type;
    }

    @Override
    public int compareTo(@NonNull FeedItemCore another) {
        return ((Long)another.date).compareTo(date);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(date);
        dest.writeParcelable(type, flags);
    }

    protected FeedItemCore(Parcel in) {
        date = in.readLong();
        type = in.readParcelable(FeedSourceName.class.getClassLoader());
    }
}
