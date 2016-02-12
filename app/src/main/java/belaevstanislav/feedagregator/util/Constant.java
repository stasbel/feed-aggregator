package belaevstanislav.feedagregator.util;

import android.content.Context;
import android.content.res.Resources;

import belaevstanislav.feedagregator.FeedAgregator;
import belaevstanislav.feedagregator.R;

// TODO много классов constant с общим предком в package?
public class Constant {
    private static final Context context = FeedAgregator.getContext();
    private static final Resources resources = context.getResources();

    // TWITTER
    public static final String TWITTER_API_KEY = resources.getString(R.string.twitter_api_key);
    public static final String TWITTER_API_SECRET = resources.getString(R.string.twitter_api_sercet);
}
