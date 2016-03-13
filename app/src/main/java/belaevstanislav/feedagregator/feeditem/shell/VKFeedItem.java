package belaevstanislav.feedagregator.feeditem.shell;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import belaevstanislav.feedagregator.feeditem.core.FeedItemCore;
import belaevstanislav.feedagregator.feeditem.core.VKFeedItemCore;
import belaevstanislav.feedagregator.feedlist.FeedItemViewHolder;
import belaevstanislav.feedagregator.feedsource.vk.VKDataStore;
import belaevstanislav.feedagregator.feedsource.vk.entity.VKEntity;
import belaevstanislav.feedagregator.feedsource.vk.entity.VKPhoto;
import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.view.RepostView;
import belaevstanislav.feedagregator.util.view.TextBlock;
import belaevstanislav.feedagregator.util.view.ViewMethod;

public class VKFeedItem extends FeedItem {
    private final String text;
    private final VKEntity[] entities;
    private final VKFeedItem repost;

    public static class AuthorInfoWrapper {
        public final String authorName;
        public final String authorImageUrl;

        public AuthorInfoWrapper(JSONObject item, VKDataStore dataStore) throws JSONException {
            final Integer sourceId;
            if (item.has(Constant.VK_KEY_SOURCE_ID)) {
                sourceId = item.getInt(Constant.VK_KEY_SOURCE_ID);
            } else {
                sourceId = item.getInt(Constant.VK_KEY_FROM_ID);
            }
            if (sourceId > 0) {
                JSONObject profile = dataStore.profiles.get(sourceId);
                authorName = profile.getString(Constant.VK_KEY_FIRST_NAME)
                        + " " + profile.getString(Constant.VK_KEY_LAST_NAME);
                authorImageUrl = profile.getString(Constant.VK_KEY_PHOTO_100);
            } else {
                JSONObject group = dataStore.groups.get(-sourceId);
                authorName = group.getString(Constant.VK_KEY_NAME);
                authorImageUrl = group.getString(Constant.VK_KEY_PHOTO_200);
            }
        }
    }

    public VKFeedItem(FeedItemCore core, long id, JSONObject item,
                      AuthorInfoWrapper authorInfoWrapper, VKDataStore dataStore) throws JSONException {
        super(core, id, authorInfoWrapper.authorName, authorInfoWrapper.authorImageUrl);

        this.text = item.getString(Constant.VK_KEY_TEXT);

        ArrayList<VKEntity> preEntities = new ArrayList<>();
        if (item.has(Constant.VK_KEY_ATTACHMENTS)) {
            JSONArray array = item.getJSONArray(Constant.VK_KEY_ATTACHMENTS);
            for (int index = 0; index < array.length(); index++) {
                JSONObject entity = array.getJSONObject(index);
                String type = entity.getString(Constant.VK_KEY_TYPE);
                JSONObject info = entity.getJSONObject(type);
                switch (type) {
                    case Constant.VK_TYPE_PHOTO:
                        preEntities.add(new VKPhoto(info));
                        break;
                    default:
                        break;
                }
            }
        }
        this.entities = new VKEntity[preEntities.size()];
        preEntities.toArray(entities);

        if (item.has(Constant.VK_KEY_COPY_HISTORY)) {
            // TODO больше репостов?
            JSONObject repostItem = item.getJSONArray(Constant.VK_KEY_COPY_HISTORY).getJSONObject(0);
            FeedItemCore repostCore = new VKFeedItemCore(repostItem);
            AuthorInfoWrapper repostAuthorInfo = new AuthorInfoWrapper(repostItem, dataStore);
            this.repost = new VKFeedItem(repostCore, id, repostItem, repostAuthorInfo, dataStore);
        } else {
            this.repost = null;
        }
    }

    @Override
    public int getLogo() {
        return Constant.VIEW_VK_LOGO;
    }

    @Override
    void drawSpecialHead(Context context, LinearLayout info) {
        if (repost != null) {
            info.addView(new RepostView(context));
        }
    }

    @Override
    void fillContent(Context context, LinearLayout content) {
        if (!text.equals("")) {
            TextBlock textBlock = new TextBlock(context);
            textBlock.setText(text);
            ViewMethod.linkify(textBlock, Constant.VIEW_VK_TEXT_BLOCK_LINK_COLOR);
            content.addView(textBlock);
        }

        for (VKEntity entity : entities) {
            content.addView(entity.getView(context));
        }

        if (repost != null) {
            View repostView = LayoutInflater.from(context).inflate(Constant.LAYOUT_REPOST, content, false);
            FeedItemViewHolder viewHolder = new FeedItemViewHolder(null, null, null, repostView);
            repost.drawView(context, viewHolder, false);
            content.addView(repostView);
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
        dest.writeParcelableArray(entities, flags);
        dest.writeByte((byte)(repost != null ? 1 : 0));
        dest.writeParcelable(repost, flags);
    }

    public static final Parcelable.Creator<VKFeedItem> CREATOR =
            new Parcelable.Creator<VKFeedItem>() {
                @Override
                public VKFeedItem createFromParcel(Parcel in) {
                    return new VKFeedItem(in);
                }

                @Override
                public VKFeedItem[] newArray(int size) {
                    return new VKFeedItem[size];
                }
            };

    private VKFeedItem(Parcel in) {
        super(in);
        text = in.readString();
        Parcelable[] parcelableArray = in.readParcelableArray(VKEntity.class.getClassLoader());
        entities = Arrays.copyOf(parcelableArray, parcelableArray.length, VKEntity[].class);
        final boolean isPresent = in.readByte() == 1;
        repost = isPresent ? (VKFeedItem) in.readParcelable(VKFeedItem.class.getClassLoader()) : null;
    }
}
