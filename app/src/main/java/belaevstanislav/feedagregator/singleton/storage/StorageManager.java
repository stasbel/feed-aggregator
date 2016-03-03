package belaevstanislav.feedagregator.singleton.storage;

import belaevstanislav.feedagregator.main.FeedAgregator;
import belaevstanislav.feedagregator.singleton.SignletonManager;

public class StorageManager implements SignletonManager {
    private static Storage storage;

    private StorageManager() {
    }

    public static void initialize() {
        storage = new Storage(FeedAgregator.getContext());
    }

    public static Storage getInstance() {
        return storage;
    }
}
