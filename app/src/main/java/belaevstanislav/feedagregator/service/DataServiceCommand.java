package belaevstanislav.feedagregator.service;

import android.os.Parcel;
import android.os.Parcelable;

import belaevstanislav.feedagregator.util.Constant;

public enum DataServiceCommand implements Parcelable {
    FETCH_NEW_ITEMS,
    DESEREALIZE_ITEMS;

    public static final String COMMAND_KEY = Constant.DATASERVICE_COMMAND_KEY;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }

    public static final Parcelable.Creator<DataServiceCommand> CREATOR =
            new Parcelable.Creator<DataServiceCommand>() {
                @Override
                public DataServiceCommand createFromParcel(Parcel in) {
                    return values()[in.readInt()];
                }

                @Override
                public DataServiceCommand[] newArray(int size) {
                    return new DataServiceCommand[size];
                }
            };
}
