package belaevstanislav.feedagregator.task.parser;

import belaevstanislav.feedagregator.feeditem.core.FeedItemCore;
import belaevstanislav.feedagregator.singleton.threads.TaskPriority;
import belaevstanislav.feedagregator.task.Task;

public abstract class ParserTask implements Task {
    // TODO (#нерешаемое) костыль, но ничего не поделать, надо ждать java 8
    public static TaskPriority getPriority() {
        return TaskPriority.SECOND_PRIORITY;
    }

    private final FeedItemCore feedItemCore;

    protected ParserTask(FeedItemCore feedItemCore) {
        this.feedItemCore = feedItemCore;
    }

    public FeedItemCore getFeedItemCore() {
        return feedItemCore;
    }

    @Override
    public final TaskPriority getTaskPriority() {
        return ParserTask.getPriority();
    }
}
