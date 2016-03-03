package belaevstanislav.feedagregator.singleton.database;

import belaevstanislav.feedagregator.main.FeedAgregator;
import belaevstanislav.feedagregator.singleton.SignletonManager;

public class DatabaseManager implements SignletonManager {
    private static UnReadFeedItemDatabaseHelper databaseHelper;

    private DatabaseManager() {
    }

    public static void initialize() {
        databaseHelper = new UnReadFeedItemDatabaseHelper(FeedAgregator.getContext());
        // TODO ???
        //databaseHelper.setWriteAheadLoggingEnabled(true);
    }

    public static UnReadFeedItemDatabaseHelper getInstance() {
        return databaseHelper;
    }
}
