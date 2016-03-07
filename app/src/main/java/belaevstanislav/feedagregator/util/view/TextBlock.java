package belaevstanislav.feedagregator.util.view;

import android.content.Context;
import android.widget.TextView;

import belaevstanislav.feedagregator.util.Constant;

public class TextBlock extends TextView {
    public TextBlock(Context context) {
        super(context);
        setTextSize(Constant.VIEW_TEXT_BLOCK_TEXT_SIZE);
    }
}
