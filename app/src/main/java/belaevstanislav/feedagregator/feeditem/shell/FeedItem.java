package belaevstanislav.feedagregator.feeditem.shell;

import android.content.Context;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.Date;

import belaevstanislav.feedagregator.R;
import belaevstanislav.feedagregator.feeditem.core.FeedItemCore;
import belaevstanislav.feedagregator.feedlist.FeedItemViewHolder;
import belaevstanislav.feedagregator.service.util.ParcelableMethod;
import belaevstanislav.feedagregator.util.Constant;

public abstract class FeedItem extends FeedItemCore {
    private final long id;
    private final String authorName;
    private final String authorImageUrl;

    protected FeedItem(FeedItemCore core, long id, String authorName, String authorImageUrl) {
        super(core);
        this.id = id;
        this.authorName = authorName;
        this.authorImageUrl = authorImageUrl;
    }

    public long getId() {
        return id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorImageUrl() {
        return authorImageUrl;
    }

    public abstract int getLogo();

    private void drawCommonHead(Context context, FeedItemViewHolder holder,
                                boolean isNeedToDrawLogoImage) throws Exception {
        Picasso.with(context).load(getAuthorImageUrl()).fit().tag(context).placeholder(R.drawable.athor_image_placeholder).into(holder.getAuthorImageView());
        holder.getAuthorNameView().setText(getAuthorName());
        holder.getDateView().setText(Constant.FEED_ITEM_HEAD_TIME_PATTERN.format(new Date(getDate() * 1000L)));
        if (isNeedToDrawLogoImage) {
            holder.getLogoView().setImageResource(getLogo());
        }
        holder.getAuthorInfo().removeAllViews();
    }

    abstract void drawSpecialHead(Context context, LinearLayout info);

    abstract void fillContent(Context context, LinearLayout content);

    private void drawContent(Context context, FeedItemViewHolder holder) {
        final LinearLayout content = holder.getContentView();
        // TODO можно быть умней и проверять совпадение прошлых и новых view'ек чтобы не inflate'ить
        // TODO (очень врятли, слишком разные могут быть view'шки, а организация внутри предполагает массив)
        content.removeAllViews();

        fillContent(context, content);

        // TODO ???
        /*content.post(new Runnable() {
            @Override
            public void run() {
                if (content.getLayoutParams().height > Constant.ROW_LAYOUT_MAX_HEIGHT) {
                    content.getLayoutParams().height = (int) Constant.ROW_LAYOUT_MAX_HEIGHT;
                    content.requestLayout();
                }
            }
        });*/
    }

    public void drawView(Context context, FeedItemViewHolder holder,
                         boolean isNeedToDrawLogoImage) {
        try {
            drawCommonHead(context, holder, isNeedToDrawLogoImage);
            drawSpecialHead(context, holder.getAuthorInfo());
            drawContent(context, holder);
        } catch (Exception exception) {
            Log.e("DRAW", "DRAW_EXCEPTION");
            exception.printStackTrace();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(id);
        dest.writeString(authorName);
        dest.writeString(authorImageUrl);
    }

    protected FeedItem(Parcel in) {
        super(in);
        id = in.readLong();
        authorName = in.readString();
        authorImageUrl = in.readString();
    }

    public byte[] serialize() {
        return ParcelableMethod.marshall(this);
    }
}
