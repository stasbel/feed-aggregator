package belaevstanislav.feedagregator.feedsource.vk.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import belaevstanislav.feedagregator.util.Constant;


public class VKPhoto implements VKEntity {
    private final String url604;

    public VKPhoto(JSONObject photo) throws JSONException {
        this.url604 = photo.getString(Constant.VK_KEY_PHOTO_604);
    }

    @Override
    public View getView(Context context) {
        ImageView imageView = new ImageView(context);
        Picasso.with(context).load(url604).tag(context).into(imageView);
        return imageView;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url604);
    }

    public static final Parcelable.Creator<VKPhoto> CREATOR =
            new Parcelable.Creator<VKPhoto>() {
                @Override
                public VKPhoto createFromParcel(Parcel in) {
                    return new VKPhoto(in);
                }

                @Override
                public VKPhoto[] newArray(int size) {
                    return new VKPhoto[size];
                }
            };

    private VKPhoto(Parcel in) {
        url604 = in.readString();
    }
}
