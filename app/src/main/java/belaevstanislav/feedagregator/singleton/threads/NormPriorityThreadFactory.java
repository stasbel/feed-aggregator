package belaevstanislav.feedagregator.singleton.threads;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

public class NormPriorityThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(@NonNull Runnable r) {
        Thread thread = new Thread(r);
        thread.setPriority(Thread.NORM_PRIORITY);
        return thread;
    }
}
