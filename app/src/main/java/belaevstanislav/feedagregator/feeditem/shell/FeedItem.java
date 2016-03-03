package belaevstanislav.feedagregator.feeditem.shell;

import android.content.Context;
import android.os.Parcel;
import android.util.Log;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.Date;

import belaevstanislav.feedagregator.feeditem.core.FeedItemCore;
import belaevstanislav.feedagregator.feedlist.FeedItemViewHolder;
import belaevstanislav.feedagregator.singleton.images.ImagesManager;
import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.ParcelableMethod;

public abstract class FeedItem extends FeedItemCore {
    private final String authorName;
    private final String authorImageUrl;

    protected FeedItem(FeedItemCore core, String authorName, String authorImageUrl) {
        super(core);
        this.authorName = authorName;
        this.authorImageUrl = authorImageUrl;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorImageUrl() {
        return authorImageUrl;
    }

    public abstract int getLogo();

    private void drawHead(Context context, FeedItemViewHolder holder,
                          boolean isNeedToDrawLogoImage) throws Exception {
        ImagesManager.getInstance().load(getAuthorImageUrl()).priority(Picasso.Priority.HIGH).fit().tag(context).into(holder.getAuthorImageView());
        holder.getAuthorNameView().setText(getAuthorName());
        holder.getDateView().setText(Constant.FEED_ITEM_HEAD_TIME_PATTERN.format(new Date(getDate() * 1000L)));
        if (isNeedToDrawLogoImage) {
            holder.getLogoView().setImageResource(getLogo());
        }
    }

    abstract void fillContent(LinearLayout content, Context context);

    private void drawContent(Context context, FeedItemViewHolder holder) {
        LinearLayout content = holder.getContentView();
        // TODO можно быть умней и проверять совпадение прошлых и новых view'ек чтобы не inflate'ить
        // TODO (очень врятли, слишком разные могут быть view'шки, а организация внутри предполагает массив)
        content.removeAllViews();

        fillContent(content, context);

        // TODO ???
        /*content.post(new Runnable() {
            @Override
            public void run() {
                if (content.getHeight() > ConstantsManager.getInstance().ROW_LAYOUT_MAX_HEIGHT_PX) {
                    content.getLayoutParams().height = ConstantsManager.getInstance().ROW_LAYOUT_MAX_HEIGHT_PX;
                    content.requestLayout();
                }
            }
        });*/
    }

    public void drawView(Context context, FeedItemViewHolder holder,
                         boolean isNeedToDrawLogoImage) {
        try {
            drawHead(context, holder, isNeedToDrawLogoImage);
            drawContent(context, holder);
        } catch (Exception exception) {
            Log.e("DRAW", "DRAW_EXCEPTION");
            exception.printStackTrace();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(authorName);
        dest.writeString(authorImageUrl);
    }

    protected FeedItem(Parcel in) {
        super(in);
        authorName = in.readString();
        authorImageUrl = in.readString();
    }

    public byte[] serialize() {
        return ParcelableMethod.marshall(this);
    }
}
