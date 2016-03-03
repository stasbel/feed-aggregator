package belaevstanislav.feedagregator.singleton.images;

import com.squareup.picasso.Picasso;

import belaevstanislav.feedagregator.main.FeedAgregator;
import belaevstanislav.feedagregator.singleton.SignletonManager;

public class ImagesManager implements SignletonManager {
    private static Picasso picasso;

    static {
        // TODO memory cache + disk cache optimal?
        picasso = new Picasso.Builder(FeedAgregator.getContext())
                .build();
        Picasso.setSingletonInstance(picasso);
    }

    private ImagesManager() {
    }

    public static void initialize() {
    }

    public static Picasso getInstance() {
        return picasso;
    }
}
