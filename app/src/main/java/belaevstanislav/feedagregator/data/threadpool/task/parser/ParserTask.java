package belaevstanislav.feedagregator.data.threadpool.task.parser;

import belaevstanislav.feedagregator.data.Data;
import belaevstanislav.feedagregator.feeditem.core.FeedItemCore;
import belaevstanislav.feedagregator.data.threadpool.task.Task;
import belaevstanislav.feedagregator.data.threadpool.task.TaskPriority;

public abstract class ParserTask extends Task {
    private final FeedItemCore core;
    private final long id;

    protected ParserTask(Data data, FeedItemCore core, long id) {
        super(TaskPriority.PARSER_PRIORITY, data);
        this.core = core;
        this.id = id;
    }

    public FeedItemCore getFeedItemCore() {
        return core;
    }

    public long getId() {
        return id;
    }
}
