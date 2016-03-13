package belaevstanislav.feedagregator.util.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import belaevstanislav.feedagregator.R;
import belaevstanislav.feedagregator.util.Constant;

public class RepostView extends ImageView {
    public RepostView(Context context) {
        super(context);
        setColorFilter(Constant.VIEW_REPOST_COLOR);
        setImageResource(Constant.VIEW_REPOST_ICON);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Constant.VIEW_REPOST_ICON_SIZE,
                Constant.VIEW_REPOST_ICON_SIZE);
        setLayoutParams(params);
    }
}
