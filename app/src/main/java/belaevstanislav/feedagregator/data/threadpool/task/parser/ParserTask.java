package belaevstanislav.feedagregator.data.threadpool.task.parser;

import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.data.threadpool.task.GetId;
import belaevstanislav.feedagregator.feeditem.core.FeedItemCore;
import belaevstanislav.feedagregator.data.threadpool.task.Task;
import belaevstanislav.feedagregator.data.threadpool.task.TaskPriority;

public abstract class ParserTask extends Task implements GetId {
    protected final FeedItemCore core;
    protected final long id;
    protected final boolean isNeedToCache;

    protected ParserTask(Data data, FeedItemCore core, long id, boolean isNeedToCache) {
        super(TaskPriority.PARSER_PRIORITY, data);
        this.core = core;
        this.id = id;
        this.isNeedToCache = isNeedToCache;
    }

    public FeedItemCore getFeedItemCore() {
        return core;
    }

    public long getId() {
        return id;
    }
}
