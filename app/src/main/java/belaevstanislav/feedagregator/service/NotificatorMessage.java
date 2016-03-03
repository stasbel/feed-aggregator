package belaevstanislav.feedagregator.service;

import android.os.Parcel;
import android.os.Parcelable;

import belaevstanislav.feedagregator.util.Constant;

public enum NotificatorMessage implements Parcelable {
    READY_TO_SHOW;

    public static final String MESSAGE_KEY = Constant.DATASERVICE_MESSAGE_KEY;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }

    public static final Parcelable.Creator<NotificatorMessage> CREATOR =
            new Parcelable.Creator<NotificatorMessage>() {
                @Override
                public NotificatorMessage createFromParcel(Parcel in) {
                    return values()[in.readInt()];
                }

                @Override
                public NotificatorMessage[] newArray(int size) {
                    return new NotificatorMessage[size];
                }
            };
}
