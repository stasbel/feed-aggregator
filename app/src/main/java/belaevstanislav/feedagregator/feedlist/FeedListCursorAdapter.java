package belaevstanislav.feedagregator.feedlist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import belaevstanislav.feedagregator.R;
import belaevstanislav.feedagregator.feeditem.shell.FeedItem;
import belaevstanislav.feedagregator.singleton.database.DatabaseManager;
import belaevstanislav.feedagregator.singleton.threads.ThreadsManager;
import belaevstanislav.feedagregator.util.Constant;
import belaevstanislav.feedagregator.util.HighPriorityAsyncTask;

public class FeedListCursorAdapter extends CursorRecyclerViewAdapter<FeedListCursorAdapter.FeedItemViewHolder> {


    private final Context context;
    private final LayoutInflater layoutInflater;
    private final int indexColumnId;

    public FeedListCursorAdapter(Context context, Cursor cursor) {
        super(cursor);
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.indexColumnId = cursor.getColumnIndex(Constant.KEY_TABLE_ID);
    }

    @Override
    public FeedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.feed_list_row_layout, parent, false);
        return new FeedItemViewHolder(view);
    }

    private class GetFeedItemAndDrawViewAsyncTask extends HighPriorityAsyncTask<Void, Void, FeedItem> {
        private final long id;
        private final FeedItemViewHolder holder;

        public GetFeedItemAndDrawViewAsyncTask(long id, FeedItemViewHolder holder) {
            this.id = id;
            this.holder = holder;
        }

        @Override
        protected FeedItem inBackground(Void... params) {
            return ThreadsManager.getInstance().fetchParseTask(id);
        }

        @Override
        protected void onPostExecute(FeedItem feedItem) {
            super.onPostExecute(feedItem);
            feedItem.drawView(context, holder, true);
        }
    }

    @Override
    public void onBindViewHolderCursor(FeedItemViewHolder holder, Cursor cursor) {
        long id = cursor.getLong(indexColumnId);

        holder.setId(id);
        holder.resetSwipeState();

        // TODO отсюда можно запускать fetch некоторых item'ов далее
        if (ThreadsManager.getInstance().isFinished(id)) {
            ThreadsManager.getInstance().fetchParseTask(cursor.getLong(indexColumnId))
                    .drawView(context, holder, true);
        } else {
            new GetFeedItemAndDrawViewAsyncTask(id, holder).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public class FeedItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView backgroundView;
        private final View foregroundView;
        private final ImageView authorImage;
        private final TextView authorName;
        private final TextView date;
        private final ImageView logo;
        private final LinearLayout content;

        public FeedItemViewHolder(View itemView) {
            super(itemView);
            this.backgroundView = (ImageView) itemView.findViewById(R.id.feed_item_backgroud);
            backgroundView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (foregroundView.getTranslationX() < 0) {
                        delete();
                    } else {
                        delete();
                    }
                }
            });
            this.foregroundView = itemView.findViewById(R.id.feed_item_foreground);
            this.authorImage = (ImageView) itemView.findViewById(R.id.feed_item_author_image);
            this.authorName = (TextView) itemView.findViewById(R.id.feed_item_author_name);
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

        public void delete() {
            // TODO getLayoutPosition (заменить везде, где можно))? выполнять удаление лениво для ускорения?
            // TODO добавить анимацию удаления
            int position = getAdapterPosition();
            DatabaseManager.getInstance().delete(getId());
            swapCursor(DatabaseManager.getInstance().getAll());
            notifyItemRemoved(position);
        }

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

        public void drawSwipeBackground(Canvas c) {
            // TODO иконки? выполнять поменьше дейтсвий? new font for text?

            final float relativeX = foregroundView.getTranslationX();
            final float absoluteTop = itemView.getTop(), absoluteBot = itemView.getBottom();
            final float relativeTop = foregroundView.getTop(), relativeBot = foregroundView.getBottom();
            final float left, right;
            final Paint backgroundPaint, textPaint;
            final String swipeText;
            final Rect swipeRect;

            if (relativeX < 0) {
                left = foregroundView.getRight() + relativeX;
                right = foregroundView.getRight();
                backgroundPaint = Constant.SWIPE_RIGHT_BACKGROUND_PAINT;
                textPaint = Constant.SWIPE_RIGHT_TEXT_PAINT;
                swipeText = Constant.SWIPE_RIGHT_TEXT;
                swipeRect = Constant.SWIPE_RIGHT_RECT;
            } else {
                left = foregroundView.getLeft();
                right = foregroundView.getLeft() + relativeX;
                backgroundPaint = Constant.SWIPE_LEFT_BACKGROUND_PAINT;
                textPaint = Constant.SWIPE_LEFT_TEXT_PAINT;
                swipeText = Constant.SWIPE_LEFT_TEXT;
                swipeRect = Constant.SWIPE_LEFT_RECT;
            }

            c.clipRect(left, absoluteTop, right, absoluteBot);
            c.drawPaint(backgroundPaint);
            final float x = (right + left - swipeRect.width()) / 2f - swipeRect.left;
            final float y = (absoluteBot + absoluteTop + swipeRect.height()) / 2f - swipeRect.bottom;
            c.drawText(swipeText, x, y, textPaint);

            backgroundView.setBottom((int) relativeBot);
            backgroundView.setTop((int) relativeTop);
            backgroundView.setLeft((int) left);
            backgroundView.setRight((int) right);
        }
    }
}
