package belaevstanislav.feedagregator.singleton.images;

import android.os.AsyncTask;

import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutorService;

import belaevstanislav.feedagregator.FeedAgregator;
import belaevstanislav.feedagregator.singleton.SignletonManager;

public class ImagesManager implements SignletonManager {
    private static Picasso picasso;
    static {
        // TODO memory cache + disk cache optimal?
        picasso = new Picasso.Builder(FeedAgregator.getContext())
                .executor((ExecutorService)AsyncTask.THREAD_POOL_EXECUTOR)
                .build();
        Picasso.setSingletonInstance(picasso);
    }

    public static void initialize() {
    }

    public static Picasso getInstance() {
        return picasso;
    }
}
