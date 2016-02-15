package belaevstanislav.feedagregator.singleton.threads;

import belaevstanislav.feedagregator.singleton.SignletonManager;

public class ThreadsManager implements SignletonManager {
    private static PriorityTaskPool priorityTaskPool;

    public static void initialize() {
        priorityTaskPool = new PriorityTaskPool();
        // TODO ???
        priorityTaskPool.prestartAllCoreThreads();
    }

    public static PriorityTaskPool getInstance() {
        return priorityTaskPool;
    }
}
