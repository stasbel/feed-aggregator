package belaevstanislav.feedagregator.feedlist;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import belaevstanislav.feedagregator.R;
import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.globalinterface.OnFeedItemActionListener;

public class FeedItemViewHolder extends RecyclerView.ViewHolder {
    private final Data data;
    private final FeedListCursorAdapter feedListCursorAdapter;
    private final OnFeedItemActionListener onFeedItemActionListener;
    private final ImageView backgroundView;
    private final View foregroundView;
    private final ImageView authorImage;
    private final TextView authorName;
    private final LinearLayout authorInfo;
    private final TextView date;
    private final ImageView logo;
    private final LinearLayout content;

    public FeedItemViewHolder(FeedListCursorAdapter feedListCursorAdapter, Data data,
                              OnFeedItemActionListener onFeedItemActionListener, View itemView) {
        super(itemView);
        this.data = data;
        this.feedListCursorAdapter = feedListCursorAdapter;
        this.onFeedItemActionListener = onFeedItemActionListener;
        this.backgroundView = (ImageView) itemView.findViewById(R.id.feed_item_backgroud);
        backgroundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foregroundView.getTranslationX() < 0) {
                    delete();
                } else {
                    open(false);
                }
            }
        });
        this.foregroundView = itemView.findViewById(R.id.feed_item_foreground);
        this.authorImage = (ImageView) itemView.findViewById(R.id.feed_item_author_image);
        this.authorName = (TextView) itemView.findViewById(R.id.feed_item_author_name);
        this.authorInfo = (LinearLayout) itemView.findViewById(R.id.feed_item_author_info);
        this.date = (TextView) itemView.findViewById(R.id.feed_item_date);
        this.logo = (ImageView) itemView.findViewById(R.id.feed_item_logo);
        this.content = (LinearLayout) itemView.findViewById(R.id.feed_item_content);
    }

    public ImageView getAuthorImageView() {
        return authorImage;
    }

    public TextView getAuthorNameView() {
        return authorName;
    }

    public LinearLayout getAuthorInfo() {
        return authorInfo;
    }

    public TextView getDateView() {
        return date;
    }

    public ImageView getLogoView() {
        return logo;
    }

    public LinearLayout getContentView() {
        return content;
    }

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // commands

    public void delete() {
        // TODO getLayoutPosition (заменить везде, где можно))? выполнять удаление лениво для ускорения?
        // TODO добавить анимацию удаления
        onFeedItemActionListener.onDelete(getAdapterPosition(), getId());
    }

    public void open(boolean isFullWay) {
        int position = getAdapterPosition();
        onFeedItemActionListener.onOpen(position, getId(), isFullWay);
        if (isFullWay) {
            feedListCursorAdapter.notifyItemChanged(position);
        }
    }

    // swipe

    private float lockX;
    private float gointBackAbsoluteX;
    private float goindBackRelativeX;
    private boolean isGoingBack;

    public void resetSwipeState() {
        lockX = 0f;
        gointBackAbsoluteX = 0f;
        goindBackRelativeX = 0f;
        isGoingBack = false;
        foregroundView.setTranslationX(0f);
    }

    public float getSwipeTrashold() {
        if (lockX == 0f) {
            return Constant.SWIPE_FULL_TRASHHOLD;
        } else {
            return Constant.SWIPE_LOCK_TRASHHOLD;
        }
    }

    public void onSwipeChildDraw(float dX, boolean isCurrentlyActive) {
        final float absoluteX = lockX + dX;
        if (isCurrentlyActive) {
            if (isGoingBack) {
                isGoingBack = false;
            }
            foregroundView.setTranslationX(lockX + dX);
        } else {
            if (!isGoingBack) {
                isGoingBack = true;
                goindBackRelativeX = dX;
                gointBackAbsoluteX = absoluteX;
                final float lockSize = Constant.SWIPE_LOCK_SIZE;
                final float lockMargin = Constant.SWIPE_LOCK_MARGIN;
                if (absoluteX <= -lockSize + lockMargin) {
                    lockX = -lockSize;
                } else if (absoluteX >= lockSize - lockMargin) {
                    lockX = lockSize;
                } else {
                    lockX = 0f;
                }
            }
            foregroundView.setTranslationX(lockX - (dX / goindBackRelativeX) * (lockX - gointBackAbsoluteX));
        }
    }

    public void drawSwipeBackground(Canvas canvas) {
        // TODO иконки? выполнять поменьше действий? new font for text?

        final float relativeX = foregroundView.getTranslationX();
        final float absoluteTop = itemView.getTop();
        float absoluteBot = itemView.getBottom();
        final float relativeTop = foregroundView.getTop(), relativeBot = foregroundView.getBottom();
        final float left, right;
        final Paint backgroundPaint, textPaint;
        final String swipeText;
        final Rect swipeRect;
        final Drawable swipeIcon;

        if (relativeX < 0) {
            left = foregroundView.getRight() + relativeX;
            right = itemView.getRight();
            backgroundPaint = Constant.SWIPE_RIGHT_BACKGROUND_PAINT;
            textPaint = Constant.SWIPE_RIGHT_TEXT_PAINT;
            swipeText = Constant.SWIPE_RIGHT_TEXT;
            swipeRect = Constant.SWIPE_RIGHT_RECT;
            swipeIcon = Constant.SWIPE_RIGHT_ICON;
        } else {
            left = itemView.getLeft();
            right = foregroundView.getLeft() + relativeX;
            backgroundPaint = Constant.SWIPE_LEFT_BACKGROUND_PAINT;
            textPaint = Constant.SWIPE_LEFT_TEXT_PAINT;
            swipeText = Constant.SWIPE_LEFT_TEXT;
            swipeRect = Constant.SWIPE_LEFT_RECT;
            swipeIcon = Constant.SWIPE_LEFT_ICON;
        }

        canvas.clipRect(left, absoluteTop, right, absoluteBot);
        canvas.drawPaint(backgroundPaint);

        final float iconSize = Constant.SWIPE_ICON_SIZE;
        final float blankSize = Constant.SWIPE_ICON_TEXT_BLANK_SIZE;
        final float wholeSize = iconSize + blankSize + swipeRect.height();
        final float centrX = (right + left) / 2f;
        final float centrY = (absoluteBot + absoluteTop) / 2f;

        final float textX = centrX - swipeRect.width() / 2f;
        final float textY = centrY + wholeSize / 2f ;
        canvas.drawText(swipeText, textX, textY, textPaint);

        final float iconLeft = centrX - iconSize / 2f;
        final float iconRight = centrX + iconSize / 2f;
        final float iconBot = centrY + wholeSize / 2f - swipeRect.height() - blankSize;
        final float iconTop = centrY - wholeSize / 2f;
        swipeIcon.setBounds((int) iconLeft, (int) iconTop, (int) iconRight, (int) iconBot);
        swipeIcon.draw(canvas);

        backgroundView.setBottom((int) relativeBot);
        backgroundView.setTop((int) relativeTop);
        backgroundView.setLeft((int) left);
        backgroundView.setRight((int) right);
    }
}
